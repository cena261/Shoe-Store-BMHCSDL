import type { NextPage } from 'next';
import { useAdminOnly } from '@/common/hooks/auth';
import AdminOrdersPage from '@/modules/admin/components/orders/AdminOrdersPage';

const OrdersPage: NextPage = () => {
  useAdminOnly();

  return <AdminOrdersPage />;
};

export default OrdersPage;
