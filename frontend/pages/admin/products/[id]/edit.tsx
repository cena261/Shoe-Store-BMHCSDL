import type { NextPage } from 'next';
import AdminProductEdit from '@/modules/admin/components/products/AdminProductEdit';
import { useAdminOnly } from '@/common/hooks/auth';

const AdminProductEditPage: NextPage = () => {
  useAdminOnly();

  return <AdminProductEdit />;
};

export default AdminProductEditPage;
