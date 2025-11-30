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
import type { AuditLogResponse } from '@/common/types/backend';

export default function AuditLogsPage() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [auditLogs, setAuditLogs] = useState<AuditLogResponse[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [actionFilter, setActionFilter] = useState('ALL');

  useEffect(() => {
    loadAuditLogs();
  }, []);

  const loadAuditLogs = async () => {
    try {
      setIsLoading(true);
      const logs = await auditAdminService.getAllLogs(200);
      setAuditLogs(logs);
    } catch (error) {
      console.error('Failed to load audit logs:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const filteredLogs = auditLogs.filter((log) => {
    const matchesSearch =
      log.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.objectName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      log.details.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesAction = actionFilter === 'ALL' || log.action === actionFilter;

    return matchesSearch && matchesAction;
  });

  const insertCount = auditLogs.filter((l) => l.action === 'INSERT').length;
  const updateCount = auditLogs.filter((l) => l.action === 'UPDATE').length;
  const deleteCount = auditLogs.filter((l) => l.action === 'DELETE').length;

  const auditColumns: AuditTableColumn<AuditLogResponse>[] = [
    {
      key: 'auditId',
      label: 'Audit ID',
      width: '100px',
    },
    {
      key: 'eventTime',
      label: 'Event Time',
      render: (value) => new Date(value).toLocaleString(),
      width: '180px',
    },
    {
      key: 'username',
      label: 'Username',
      width: '150px',
    },
    {
      key: 'action',
      label: 'Action',
      render: (value) => {
        const colors: Record<string, string> = {
          INSERT: 'text-green-600',
          UPDATE: 'text-yellow-600',
          DELETE: 'text-red-600',
        };
        return <span className={`font-semibold ${colors[value]}`}>{value}</span>;
      },
      width: '100px',
    },
    {
      key: 'objectName',
      label: 'Object',
      width: '150px',
    },
    {
      key: 'ipAddress',
      label: 'IP Address',
      width: '130px',
    },
    {
      key: 'details',
      label: 'Details',
      render: (value) => (
        <span className="text-xs text-zinc-500 font-mono truncate block max-w-xs">
          {value}
        </span>
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
          <h1 className="text-4xl font-bold text-zinc-900 mb-2">Standard Audit Logs</h1>
          <p className="text-zinc-500">
            Requirement 10: Comprehensive audit trail for INSERT, UPDATE, DELETE operations
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <SecurityMetric
            label="Total Events"
            value={auditLogs.length}
            subtext="Last 200 events"
            color="blue"
          />
          <SecurityMetric
            label="INSERT Operations"
            value={insertCount}
            subtext="New records created"
            color="green"
          />
          <SecurityMetric
            label="UPDATE Operations"
            value={updateCount}
            subtext="Records modified"
            color="yellow"
          />
          <SecurityMetric
            label="DELETE Operations"
            value={deleteCount}
            subtext="Records deleted"
            color="red"
          />
        </div>

        <ExplainerPanel
          title="How Standard Auditing Works"
          variant="info"
          sections={[
            {
              heading: 'Implementation',
              content:
                'Uses Oracle database triggers to automatically log all INSERT, UPDATE, and DELETE operations on critical tables (ORDERS, PRODUCTS, USERS). Triggers capture the operation type, user, timestamp, IP address, and change details.',
            },
            {
              heading: 'Audit Information',
              content:
                'Each audit record includes: event timestamp, username (from CLIENT_IDENTIFIER), database operation (INSERT/UPDATE/DELETE), affected table, IP address, and detailed change information (old/new values for updates).',
            },
            {
              heading: 'Use Case',
              content:
                'Provides complete accountability trail for compliance and security. Answers questions like "Who deleted this order?", "When was this product updated?", and "What IP address made this change?".',
            },
          ]}
        />

        <CodeSnippet
          language="sql"
          title="Audit Trigger Example"
          description="Oracle trigger for logging ORDER updates"
          code={`CREATE OR REPLACE TRIGGER trg_audit_orders_update
AFTER UPDATE ON ORDERS
FOR EACH ROW
DECLARE
  v_username VARCHAR2(100);
  v_ip_address VARCHAR2(50);
BEGIN
  -- Get user context
  v_username := SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER');
  v_ip_address := SYS_CONTEXT('USERENV', 'IP_ADDRESS');

  -- Insert audit record
  INSERT INTO AUDIT_LOGS (
    EVENT_TIME,
    USERNAME,
    IP_ADDRESS,
    OBJECT_NAME,
    ACTION,
    ORDER_ID,
    DETAILS
  ) VALUES (
    SYSTIMESTAMP,
    v_username,
    v_ip_address,
    'ORDERS',
    'UPDATE',
    :NEW.ORDER_ID,
    'Status changed from ' || :OLD.ORDER_STATUS ||
    ' to ' || :NEW.ORDER_STATUS
  );
END;`}
        />

        <FilterBar
          searchPlaceholder="Search by username, object, or details..."
          searchValue={searchTerm}
          onSearchChange={setSearchTerm}
          filters={[
            {
              label: 'Action',
              value: actionFilter,
              onChange: setActionFilter,
              options: [
                { label: 'All Actions', value: 'ALL' },
                { label: 'INSERT', value: 'INSERT' },
                { label: 'UPDATE', value: 'UPDATE' },
                { label: 'DELETE', value: 'DELETE' },
              ],
            },
          ]}
          actions={
            <button
              onClick={loadAuditLogs}
              className="px-4 py-2 bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-900 rounded-2xl text-sm transition-colors shadow-sm"
            >
              Refresh
            </button>
          }
        />

        <AuditTable
          columns={auditColumns}
          data={filteredLogs}
          isLoading={isLoading}
          emptyMessage="No audit logs found"
          maxHeight="600px"
        />
      </div>
    </section>
  );
}
