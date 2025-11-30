import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import {
  SecurityMetric,
  CodeSnippet,
  ExplainerPanel,
  AuditTable,
  AuditTableColumn,
} from '@/modules/admin/components/security/shared';
import { securityAdminService } from '@/common/api/services/securityAdminService';
import { labelAdminService } from '@/common/api/services/labelAdminService';
import { useAdminOnly } from '@/common/hooks/auth';
import type {
  DACInfoResponse,
  VPDDebugResponse,
  OrderLabelSummaryResponse,
  DatabaseRole,
  AccessibleObject,
} from '@/common/types/backend';

export default function AccessControlPage() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [dacInfo, setDACInfo] = useState<DACInfoResponse | null>(null);
  const [vpdInfo, setVPDInfo] = useState<VPDDebugResponse | null>(null);
  const [labelSummary, setLabelSummary] = useState<OrderLabelSummaryResponse | null>(null);

  useEffect(() => {
    loadAccessControlData();
  }, []);

  const loadAccessControlData = async () => {
    try {
      setIsLoading(true);
      const [dac, vpd, labels] = await Promise.all([
        securityAdminService.getDACInfo(),
        securityAdminService.getVPDDebugInfo(),
        labelAdminService.getOrderLabelSummary(),
      ]);

      setDACInfo(dac);
      setVPDInfo(vpd);
      setLabelSummary(labels);
    } catch (error) {
      console.error('Failed to load access control data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const roleColumns: AuditTableColumn<DatabaseRole>[] = [
    {
      key: 'grantedRole',
      label: 'Role Name',
      width: '300px',
    },
    {
      key: 'adminOption',
      label: 'Admin Option',
      width: '150px',
    },
    {
      key: 'defaultRole',
      label: 'Default Role',
      width: '150px',
    },
  ];

  const privilegeColumns: AuditTableColumn<AccessibleObject>[] = [
    {
      key: 'objectName',
      label: 'Object Name',
      width: '300px',
    },
    {
      key: 'objectType',
      label: 'Object Type',
      width: '150px',
    },
    {
      key: 'privilege',
      label: 'Privilege',
      width: '150px',
    },
    {
      key: 'grantable',
      label: 'Grantable',
      width: '120px',
    },
  ];

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
          <h1 className="text-4xl font-bold text-zinc-900 mb-2">Access Control</h1>
          <p className="text-zinc-500">
            Requirements 6, 7, 8: DAC, VPD, and OLS Security Mechanisms
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <SecurityMetric
            label="Database Roles"
            value={dacInfo?.databaseRoles.length || 0}
            subtext="DAC Roles"
            color="blue"
          />
          <SecurityMetric
            label="Accessible Objects"
            value={dacInfo?.accessibleObjects.length || 0}
            subtext="DAC Privileges"
            color="green"
          />
          <SecurityMetric
            label="VPD Active"
            value={vpdInfo?.vpdPolicyActive ? 'Yes' : 'No'}
            subtext="Row-Level Security"
            color="yellow"
          />
          <SecurityMetric
            label="Labeled Orders"
            value={labelSummary?.totalOrders || 0}
            subtext="OLS Labels"
            color="zinc"
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 6: DAC (Discretionary Access Control)</h2>

          <ExplainerPanel
            title="How DAC Works in Oracle"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'DAC uses GRANT and REVOKE statements to control access to database objects. Object owners can grant privileges (SELECT, INSERT, UPDATE, DELETE) to specific users or roles.',
              },
              {
                heading: 'Use Case',
                content:
                  'Controls which database users can access tables, views, and procedures. Ensures separation of duties between application users and database administrators.',
              },
            ]}
          />

          <CodeSnippet
            language="sql"
            title="DAC Example: GRANT Privileges"
            description="Granting SELECT and INSERT privileges to a role"
            code={`-- Create role
CREATE ROLE app_user;

-- Grant table privileges
GRANT SELECT, INSERT ON ORDERS TO app_user;
GRANT SELECT ON PRODUCTS TO app_user;

-- Grant role to user
GRANT app_user TO shoestore_app;

-- Revoke privilege
REVOKE INSERT ON ORDERS FROM app_user;`}
          />

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div>
              <h3 className="text-lg font-semibold text-zinc-900 mb-4">Database Roles</h3>
              <AuditTable
                columns={roleColumns}
                data={dacInfo?.databaseRoles || []}
                emptyMessage="No roles found"
                maxHeight="300px"
              />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-zinc-900 mb-4">Accessible Objects</h3>
              <AuditTable
                columns={privilegeColumns}
                data={dacInfo?.accessibleObjects || []}
                emptyMessage="No accessible objects"
                maxHeight="300px"
              />
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 7: VPD (Virtual Private Database)</h2>

          <ExplainerPanel
            title="How VPD Works with Row-Level Security"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'VPD uses Oracle DBMS_RLS to add dynamic WHERE clauses to SQL queries. The policy function checks the CLIENT_IDENTIFIER and returns a predicate that filters rows based on user context.',
              },
              {
                heading: 'Use Case',
                content:
                  'Ensures users can only see their own orders. When a customer queries the ORDERS table, VPD automatically adds "WHERE USER_ID = :current_user_id" to restrict visibility.',
              },
            ]}
          />

          <CodeSnippet
            language="sql"
            title="VPD Policy Function"
            description="PL/SQL function that returns row-level security predicate"
            code={`CREATE OR REPLACE FUNCTION vpd_orders_policy(
  schema_name VARCHAR2,
  table_name VARCHAR2
) RETURN VARCHAR2 IS
  v_user_id NUMBER;
  v_role VARCHAR2(50);
BEGIN
  -- Get user context
  SELECT user_id, role INTO v_user_id, v_role
  FROM app_users
  WHERE email = SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER');

  -- Admin sees all orders
  IF v_role = 'Admin' THEN
    RETURN '';
  END IF;

  -- Regular users see only their orders
  RETURN 'USER_ID = ' || v_user_id;
END;

-- Apply policy to ORDERS table
BEGIN
  DBMS_RLS.ADD_POLICY(
    object_schema => 'SHOESTORE',
    object_name => 'ORDERS',
    policy_name => 'orders_vpd_policy',
    function_schema => 'SHOESTORE',
    policy_function => 'vpd_orders_policy',
    statement_types => 'SELECT'
  );
END;`}
          />

          <div className="bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm">
            <h3 className="text-lg font-semibold text-zinc-900 mb-4">Current VPD Context</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-zinc-500">VPD Policy Active:</span>
                <span className={`font-semibold ${vpdInfo?.vpdPolicyActive ? 'text-green-600' : 'text-red-600'}`}>
                  {vpdInfo?.vpdPolicyActive ? 'Yes' : 'No'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-zinc-500">Client Identifier:</span>
                <span className="text-zinc-900 font-mono text-sm">{vpdInfo?.currentClientIdentifier}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-zinc-500">Current User:</span>
                <span className="text-zinc-900">{vpdInfo?.currentUser.email}</span>
              </div>
              <div className="flex flex-col gap-2">
                <span className="text-zinc-500">Predicate:</span>
                <code className="text-zinc-800 font-mono text-sm bg-zinc-100 p-3 rounded-2xl border border-zinc-200">
                  {vpdInfo?.predicate || 'No predicate (admin sees all)'}
                </code>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-zinc-500">Visible Orders:</span>
                <span className="text-zinc-900 font-semibold">{vpdInfo?.visibleOrders?.length || 0}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 8: OLS (Oracle Label Security)</h2>

          <ExplainerPanel
            title="How OLS Label-Based Security Works"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'OLS assigns security labels (PUBLIC, CONFIDENTIAL, ADMIN_ONLY) to orders. Users have label authorizations that determine which labels they can access. Higher clearance allows access to lower labels.',
              },
              {
                heading: 'Use Case',
                content:
                  'Classifies orders by sensitivity. Regular orders are PUBLIC, sensitive orders are CONFIDENTIAL (staff only), and highly sensitive orders are ADMIN_ONLY.',
              },
            ]}
          />

          <CodeSnippet
            language="sql"
            title="OLS Label Policy Setup"
            description="Creating labels and authorization levels"
            code={`-- Create label policy
BEGIN
  SA_SYSDBA.CREATE_POLICY(
    policy_name => 'order_label_policy',
    column_name => 'SECURITY_LABEL'
  );
END;

-- Create levels
BEGIN
  SA_COMPONENTS.CREATE_LEVEL('order_label_policy', 1000, 'PUBLIC');
  SA_COMPONENTS.CREATE_LEVEL('order_label_policy', 2000, 'CONFIDENTIAL');
  SA_COMPONENTS.CREATE_LEVEL('order_label_policy', 3000, 'ADMIN_ONLY');
END;

-- Assign user authorizations
BEGIN
  SA_USER_ADMIN.SET_USER_LABELS(
    policy_name => 'order_label_policy',
    user_name => 'ADMIN_USER',
    max_read_label => 'ADMIN_ONLY'
  );

  SA_USER_ADMIN.SET_USER_LABELS(
    policy_name => 'order_label_policy',
    user_name => 'STAFF_USER',
    max_read_label => 'CONFIDENTIAL'
  );
END;`}
          />

          <div className="bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm">
            <h3 className="text-lg font-semibold text-zinc-900 mb-4">Label Distribution</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 bg-zinc-50 rounded-2xl border border-zinc-100">
                <div>
                  <div className="text-zinc-900 font-semibold">PUBLIC</div>
                  <div className="text-xs text-zinc-500">Accessible to all users</div>
                </div>
                <div className="text-2xl font-bold text-zinc-900">
                  {labelSummary?.labelCounts.PUBLIC || 0}
                </div>
              </div>
              <div className="flex items-center justify-between p-4 bg-zinc-50 rounded-2xl border border-zinc-100">
                <div>
                  <div className="text-zinc-900 font-semibold">CONFIDENTIAL</div>
                  <div className="text-xs text-zinc-500">Staff and admin only</div>
                </div>
                <div className="text-2xl font-bold text-yellow-600">
                  {labelSummary?.labelCounts.CONFIDENTIAL || 0}
                </div>
              </div>
              <div className="flex items-center justify-between p-4 bg-zinc-50 rounded-2xl border border-zinc-100">
                <div>
                  <div className="text-zinc-900 font-semibold">ADMIN_ONLY</div>
                  <div className="text-xs text-zinc-500">Administrators only</div>
                </div>
                <div className="text-2xl font-bold text-red-600">
                  {labelSummary?.labelCounts.ADMIN_ONLY || 0}
                </div>
              </div>
              <div className="pt-4 border-t border-zinc-100">
                <div className="flex items-center justify-between">
                  <span className="text-zinc-500">Total Labeled Orders:</span>
                  <span className="text-zinc-900 font-semibold text-xl">
                    {labelSummary?.totalOrders || 0}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
