import type { NextPage } from 'next';
import { useAdminOnly } from '@/common/hooks/auth';
import AdminUserDetail from '@/modules/admin/components/users/AdminUserDetail';

const UserDetailPage: NextPage = () => {
  useAdminOnly();

  return <AdminUserDetail />;
};

export default UserDetailPage;
