import type { NextPage } from 'next';
import AdminProductsPage from '@/modules/admin/components/products/AdminProductsPage';
import { useAdminOnly } from '@/common/hooks/auth';

const AdminProducts: NextPage = () => {
  useAdminOnly();

  return <AdminProductsPage />;
};

export default AdminProducts;
