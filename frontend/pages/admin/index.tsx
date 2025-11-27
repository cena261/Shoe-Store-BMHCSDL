import { NextPage } from 'next';

import { useAdminOnly } from '@/common/hooks/auth';
import AdminDashboard from '@/modules/admin/components/AdminDashboard';

const AdminPage: NextPage = () => {
  useAdminOnly();

  return <AdminDashboard />;
};

export default AdminPage;
