import type { NextPage } from 'next';
import AdminProductCreate from '@/modules/admin/components/products/AdminProductCreate';
import { useAdminOnly } from '@/common/hooks/auth';

const AdminProductCreatePage: NextPage = () => {
  useAdminOnly();

  return <AdminProductCreate />;
};

export default AdminProductCreatePage;
