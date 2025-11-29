import type { NextPage } from 'next';
import { useAdminOnly } from '@/common/hooks/auth';
import AdminOrderDetail from '@/modules/admin/components/orders/AdminOrderDetail';

const OrderDetailPage: NextPage = () => {
  useAdminOnly();

  return <AdminOrderDetail />;
};

export default OrderDetailPage;
