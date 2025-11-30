import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { RequirementCard, SecurityMetric, ExplainerPanel } from '@/modules/admin/components/security/shared';
import { securityAdminService } from '@/common/api/services/securityAdminService';
import { auditAdminService } from '@/common/api/services/auditAdminService';
import { labelAdminService } from '@/common/api/services/labelAdminService';
import { exportAdminService } from '@/common/api/services/exportAdminService';
import { useAdminOnly } from '@/common/hooks/auth';
import type {
  SecurityContextResponse,
  DACInfoResponse,
  OrderLabelSummaryResponse,
  OrderExportSummary,
} from '@/common/types/backend';

export default function SecurityOverview() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [securityContext, setSecurityContext] = useState<SecurityContextResponse | null>(null);
  const [dacInfo, setDACInfo] = useState<DACInfoResponse | null>(null);
  const [labelSummary, setLabelSummary] = useState<OrderLabelSummaryResponse | null>(null);
  const [exportHistory, setExportHistory] = useState<OrderExportSummary[]>([]);
  const [auditLogCount, setAuditLogCount] = useState(0);
  const [fgaLogCount, setFGALogCount] = useState(0);

  useEffect(() => {
    loadSecurityData();
  }, []);

  const loadSecurityData = async () => {
    try {
      setIsLoading(true);
      const [context, dac, labels, exports, auditLogs, fgaLogs] = await Promise.all([
        securityAdminService.getCurrentSession(),
        securityAdminService.getDACInfo(),
        labelAdminService.getOrderLabelSummary(),
        exportAdminService.getExportHistory(10),
        auditAdminService.getAllLogs(100),
        auditAdminService.getAllFGA(100),
      ]);

      setSecurityContext(context);
      setDACInfo(dac);
      setLabelSummary(labels);
      setExportHistory(exports);
      setAuditLogCount(auditLogs.length);
      setFGALogCount(fgaLogs.length);
    } catch (error) {
      console.error('Failed to load security data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-zinc-50 p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-center h-96">
            <div className="w-12 h-12 border-2 border-zinc-200 border-t-zinc-900 rounded-full animate-spin" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <section className="px-4 py-20 sm:px-8 bg-zinc-50 min-h-screen">
      <div className="mx-auto flex max-w-7xl flex-col gap-10">
        <div className="space-y-4">
          <button
            onClick={() => router.push('/admin')}
            className="text-zinc-500 hover:text-zinc-900 mb-4 flex items-center gap-2 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            Back to Admin Dashboard
          </button>
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Admin Panel</p>
          <h1 className="text-5xl font-bold text-zinc-900">Security & Analytics</h1>
          <p className="text-base text-zinc-500">
            Comprehensive overview of all 11 database security requirements implementation
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <SecurityMetric
            label="Active Sessions"
            value={securityContext?.userId || 0}
            subtext={`Profile: ${securityContext?.profileName || 'Unknown'}`}
            color="blue"
          />
          <SecurityMetric
            label="Audit Logs"
            value={auditLogCount}
            subtext="Last 100 events"
            color="green"
          />
          <SecurityMetric
            label="FGA Events"
            value={fgaLogCount}
            subtext="Fine-grained auditing"
            color="yellow"
          />
          <SecurityMetric
            label="Encrypted Exports"
            value={exportHistory.length}
            subtext="Hybrid encryption"
            color="zinc"
          />
        </div>

        <div>
          <h2 className="text-2xl font-bold text-zinc-900 mb-6">11 Security Requirements</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <RequirementCard
              reqNumber={1}
              title="Authentication"
              description="User registration, login, and logout with JWT tokens and BCrypt password hashing"
              status="implemented"
              metric={{
                label: 'Current User',
                value: securityContext?.applicationUsername || 'Unknown',
              }}
              onClick={() => router.push('/admin/security/sessions')}
            />

            <RequirementCard
              reqNumber={2}
              title="Symmetric Encryption (DES)"
              description="Database-level symmetric encryption using Oracle DBMS_CRYPTO with DES algorithm"
              status="implemented"
              onClick={() => router.push('/admin/security/encryption')}
            />

            <RequirementCard
              reqNumber={3}
              title="Asymmetric Encryption (RSA)"
              description="Public/private key encryption using RSA for sensitive data protection"
              status="implemented"
              onClick={() => router.push('/admin/security/encryption')}
            />

            <RequirementCard
              reqNumber={4}
              title="Hybrid Encryption"
              description="Combined AES + RSA encryption for order data exports with optimal performance"
              status="implemented"
              metric={{
                label: 'Total Exports',
                value: exportHistory.length,
              }}
              onClick={() => router.push('/admin/security/encryption')}
            />

            <RequirementCard
              reqNumber={5}
              title="Tablespace & Session Management"
              description="Oracle tablespace allocation, user profiles, and session tracking"
              status="implemented"
              metric={{
                label: 'Default Tablespace',
                value: securityContext?.defaultTablespace || 'Unknown',
              }}
              onClick={() => router.push('/admin/security/sessions')}
            />

            <RequirementCard
              reqNumber="6, 7, 8"
              title="Access Control (DAC, VPD, OLS)"
              description="Unified access control using Discretionary Access Control, Virtual Private Database (Row-Level Security), and Oracle Label Security"
              status="implemented"
              metric={{
                label: 'Database Roles',
                value: dacInfo?.databaseRoles.length || 0,
              }}
              onClick={() => router.push('/admin/security/access-control')}
            />

            <RequirementCard
              reqNumber={9}
              title="RBAC (Role-Based Access Control)"
              description="Role hierarchy with Customer, Staff, and Admin roles for authorization"
              status="implemented"
              onClick={() => router.push('/admin/users')}
            />

            <RequirementCard
              reqNumber={10}
              title="Standard Auditing"
              description="Comprehensive audit trail for INSERT, UPDATE, DELETE operations with triggers"
              status="implemented"
              metric={{
                label: 'Total Logs',
                value: auditLogCount,
              }}
              onClick={() => router.push('/admin/security/audit-logs')}
            />

            <RequirementCard
              reqNumber={11}
              title="FGA (Fine-Grained Auditing)"
              description="Column-level auditing for sensitive order data using Oracle DBMS_FGA"
              status="implemented"
              metric={{
                label: 'FGA Events',
                value: fgaLogCount,
              }}
              onClick={() => router.push('/admin/security/fga-logs')}
            />
          </div>
        </div>
      </div>
    </section>
  );
}
