import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import {
  SecurityMetric,
  CodeSnippet,
  ExplainerPanel,
  AuditTable,
  AuditTableColumn,
  FilterBar,
} from '@/modules/admin/components/security/shared';
import { auditAdminService } from '@/common/api/services/auditAdminService';
import { useAdminOnly } from '@/common/hooks/auth';
import type { FGAEventResponse } from '@/common/types/backend';

export default function FGALogsPage() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [fgaLogs, setFGALogs] = useState<FGAEventResponse[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [policyFilter, setPolicyFilter] = useState('ALL');

  useEffect(() => {
    loadFGALogs();
  }, []);

  const loadFGALogs = async () => {
    try {
      setIsLoading(true);
      const logs = await auditAdminService.getAllFGA(200);
      setFGALogs(logs);
    } catch (error) {
      console.error('Failed to load FGA logs:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const uniquePolicies = Array.from(new Set(fgaLogs.map((log) => log.policyName))).sort();
  const uniqueObjects = Array.from(new Set(fgaLogs.map((log) => log.objectName))).sort();

  const filteredLogs = fgaLogs.filter((log) => {
    const matchesSearch =
      log.dbUser.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.objectName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.sqlText.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.clientIdentifier.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesPolicy = policyFilter === 'ALL' || log.policyName === policyFilter;

    return matchesSearch && matchesPolicy;
  });

  const fgaColumns: AuditTableColumn<FGAEventResponse>[] = [
    {
      key: 'fgaId',
      label: 'FGA ID',
      width: '100px',
    },
    {
      key: 'eventTime',
      label: 'Event Time',
      render: (value) => new Date(value).toLocaleString(),
      width: '180px',
    },
    {
      key: 'dbUser',
      label: 'DB User',
      width: '120px',
    },
    {
      key: 'objectName',
      label: 'Object',
      width: '150px',
    },
    {
      key: 'policyName',
      label: 'Policy',
      render: (value) => <span className="text-blue-600 font-medium">{value}</span>,
      width: '180px',
    },
    {
      key: 'sqlText',
      label: 'SQL Query',
      render: (value) => (
        <code className="text-xs text-zinc-500 font-mono bg-zinc-100 px-2 py-1 rounded border border-zinc-200 block truncate max-w-md">
          {value}
        </code>
      ),
    },
  ];

  return (
    <section className="px-4 py-20 sm:px-8 bg-zinc-50 min-h-screen">
      <div className="mx-auto flex max-w-7xl flex-col gap-10">
        <div>
          <button
            onClick={() => router.push('/admin/security/overview')}
            className="text-zinc-500 hover:text-zinc-900 mb-4 flex items-center gap-2 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Back to Security Dashboard
          </button>
          <h1 className="text-4xl font-bold text-zinc-900 mb-2">FGA Logs</h1>
          <p className="text-zinc-500">
            Requirement 9: Fine-Grained Auditing for sensitive column access
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <SecurityMetric
            label="Total FGA Events"
            value={fgaLogs.length}
            subtext="Sensitive column access"
            color="blue"
          />
          <SecurityMetric
            label="Active Policies"
            value={uniquePolicies.length}
            subtext="FGA policies enabled"
            color="green"
          />
          <SecurityMetric
            label="Unique Users"
            value={new Set(fgaLogs.map(l => l.dbUser)).size}
            subtext="Accessing sensitive data"
            color="yellow"
          />
        </div>

        <ExplainerPanel
          title="How Fine-Grained Auditing (FGA) Works"
          variant="info"
          sections={[
            {
              heading: 'Implementation',
              content:
                'Uses Oracle DBMS_FGA to define audit policies on specific columns (e.g., CREDIT_CARD, SALARY). Auditing is triggered only when these specific columns are accessed or conditionally based on data values.',
            },
            {
              heading: 'Use Case',
              content:
                'Monitors access to highly sensitive data. Unlike standard auditing which logs the operation, FGA logs the exact SQL query executed, providing deeper context into "SELECT" operations on sensitive fields.',
            },
          ]}
        />

        <CodeSnippet
          language="sql"
          title="FGA Policy Example"
          description="Creating an FGA policy for credit card access"
          code={`BEGIN
  DBMS_FGA.ADD_POLICY(
    object_schema => 'SHOESTORE',
    object_name => 'CUSTOMERS',
    policy_name => 'audit_sensitive_columns',
    audit_column => 'CREDIT_CARD_NUMBER, SSN',
    enable => TRUE,
    statement_types => 'SELECT,INSERT,UPDATE,DELETE',
    audit_trail => DBMS_FGA.DB + DBMS_FGA.EXTENDED
  );
END;`}
        />

        <div className="bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm">
          <h3 className="text-lg font-semibold text-zinc-900 mb-4">Active FGA Policies</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {uniquePolicies.map((policy, index) => {
              const policyEvents = fgaLogs.filter((log) => log.policyName === policy);
              const policyObject = policyEvents[0]?.objectName || 'Unknown';
              return (
                <div key={index} className="p-4 bg-zinc-50 rounded-2xl border border-zinc-100">
                  <div className="flex items-center justify-between mb-2">
                    <div className="text-zinc-900 font-semibold">{policy}</div>
                    <div className="text-zinc-500 text-sm">{policyEvents.length} events</div>
                  </div>
                  <div className="text-xs text-zinc-500">
                    Object: <span className="text-zinc-600">{policyObject}</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <FilterBar
          searchPlaceholder="Search by SQL query, user, or policy..."
          searchValue={searchTerm}
          onSearchChange={setSearchTerm}
          filters={[
            {
              label: 'Policy',
              value: policyFilter,
              onChange: setPolicyFilter,
              options: [
                { label: 'All Policies', value: 'ALL' },
                ...uniquePolicies.map((p) => ({ label: p, value: p })),
              ],
            },
          ]}
          actions={
            <button
              onClick={loadFGALogs}
              className="px-4 py-2 bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-900 rounded-2xl text-sm transition-colors shadow-sm"
            >
              Refresh
            </button>
          }
        />

        <AuditTable
          columns={fgaColumns}
          data={filteredLogs}
          isLoading={isLoading}
          emptyMessage="No FGA logs found"
          maxHeight="600px"
        />
      </div>
    </section>
  );
}
