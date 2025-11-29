import type { NextPage } from 'next';
import { useAdminOnly } from '@/common/hooks/auth';
import AdminUsersPage from '@/modules/admin/components/users/AdminUsersPage';

const UsersPage: NextPage = () => {
  useAdminOnly();

  return <AdminUsersPage />;
};

export default UsersPage;
