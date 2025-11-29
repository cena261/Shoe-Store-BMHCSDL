import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { orderAdminService } from '@/common/api/services/orderAdminService';
import { OrderAdminResponse, OrderStatus } from '@/common/types/backend';
import { AiOutlineArrowLeft } from 'react-icons/ai';
import { BiLoaderAlt } from 'react-icons/bi';
import { useModal } from '@/common/recoil/modal';
import { useToast } from '@/common/recoil/toast/toast.hooks';

const AdminOrderDetail = () => {
  const router = useRouter();
  const { id } = router.query;
  const { openModal, closeModal } = useModal();
  const toast = useToast();

  const [order, setOrder] = useState<OrderAdminResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedStatus, setSelectedStatus] = useState<OrderStatus | ''>('');

  const statusOptions: { value: OrderStatus; label: string; color: string }[] = [
    { value: 'PENDING', label: 'Chờ xác nhận', color: 'bg-yellow-100 text-yellow-600' },
    { value: 'CONFIRMED', label: 'Đợi giao hàng', color: 'bg-blue-100 text-blue-600' },
    { value: 'ON_DELIVERY', label: 'Đang giao hàng', color: 'bg-purple-100 text-purple-600' },
    { value: 'SUCCESS', label: 'Giao hàng thành công', color: 'bg-green-100 text-green-600' },
    { value: 'CANCELLED', label: 'Đã hủy', color: 'bg-orange-100 text-orange-600' },
    { value: 'FAILED', label: 'Giao hàng thất bại', color: 'bg-red-100 text-red-600' },
  ];

  const fetchOrder = async () => {
    if (!id) return;

    try {
      setLoading(true);
      setError(null);

      const data = await orderAdminService.getOrderById(Number(id));
      setOrder(data);
      setSelectedStatus(data.orderStatus as OrderStatus);
    } catch (err: any) {
      setError(err.message || 'Failed to load order details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrder();
  }, [id]);

  const getAvailableStatuses = (currentStatus: string): OrderStatus[] => {
    switch (currentStatus) {
      case 'PENDING':
        return ['CONFIRMED', 'CANCELLED', 'FAILED'];
      case 'CONFIRMED':
        return ['ON_DELIVERY', 'CANCELLED', 'FAILED'];
      case 'ON_DELIVERY':
        return ['SUCCESS', 'FAILED'];
      case 'SUCCESS':
      case 'CANCELLED':
      case 'FAILED':
        return [];
      default:
        return [];
    }
  };

  const isStatusFinalized = (status: string) => {
    return ['SUCCESS', 'CANCELLED', 'FAILED'].includes(status);
  };

  const handleUpdateStatus = () => {
    if (!selectedStatus || !order) return;

    if (selectedStatus === order.orderStatus) {
      toast.warning('Status is already set to this value');
      return;
    }

    openModal(
      <div className="rounded-3xl bg-white p-8">
        <h2 className="text-2xl font-bold">Update Order Status</h2>
        <p className="mt-4 text-zinc-600">
          Are you sure you want to update order #{order.orderId} status from{' '}
          <span className="font-semibold">{getStatusLabel(order.orderStatus)}</span> to{' '}
          <span className="font-semibold">{getStatusLabel(selectedStatus)}</span>?
        </p>
        {selectedStatus === 'CONFIRMED' && (
          <p className="mt-2 text-sm text-amber-600">
            Note: Stock will be deducted when confirming the order.
          </p>
        )}
        {(selectedStatus === 'CANCELLED' || selectedStatus === 'FAILED') &&
          (order.orderStatus === 'CONFIRMED' || order.orderStatus === 'ON_DELIVERY') && (
            <p className="mt-2 text-sm text-amber-600">
              Note: Stock will be restored when cancelling/failing the order.
            </p>
          )}
        <div className="mt-6 flex gap-4">
          <button
            onClick={closeModal}
            className="flex-1 rounded-full border border-zinc-200 px-6 py-3 font-semibold transition hover:border-zinc-900"
          >
            Cancel
          </button>
          <button
            onClick={async () => {
              try {
                const updatedOrder = await orderAdminService.updateOrderStatus(
                  order.orderId,
                  selectedStatus
                );
                setOrder(updatedOrder);
                closeModal();
                toast.success('Order status updated successfully');
              } catch (err: any) {
                toast.error(err.message || 'Failed to update order status');
              }
            }}
            className="flex-1 rounded-full bg-black px-6 py-3 font-semibold text-white transition hover:scale-105"
          >
            Update Status
          </button>
        </div>
      </div>
    );
  };

  const handleCancelOrder = () => {
    if (!order) return;

    if (order.orderStatus === 'SUCCESS') {
      toast.error('Cannot cancel a completed order');
      return;
    }

    openModal(
      <div className="rounded-3xl bg-white p-8">
        <h2 className="text-2xl font-bold text-red-600">Cancel Order</h2>
        <p className="mt-4 text-zinc-600">
          Are you sure you want to cancel order #{order.orderId}?
        </p>
        <p className="mt-2 text-sm text-amber-600">
          This action will set the order status to CANCELLED and restore stock if applicable.
        </p>
        <div className="mt-6 flex gap-4">
          <button
            onClick={closeModal}
            className="flex-1 rounded-full border border-zinc-200 px-6 py-3 font-semibold transition hover:border-zinc-900"
          >
            No, Keep Order
          </button>
          <button
            onClick={async () => {
              try {
                await orderAdminService.cancelOrder(order.orderId);
                closeModal();
                toast.success('Order cancelled successfully');
                router.push('/admin/orders');
              } catch (err: any) {
                toast.error(err.message || 'Failed to cancel order');
              }
            }}
            className="flex-1 rounded-full bg-red-600 px-6 py-3 font-semibold text-white transition hover:scale-105"
          >
            Yes, Cancel Order
          </button>
        </div>
      </div>
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  };

  const getStatusColor = (status: string | null | undefined) => {
    if (!status) return 'bg-zinc-100 text-zinc-600';
    const statusOption = statusOptions.find((opt) => opt.value === status);
    return statusOption?.color || 'bg-zinc-100 text-zinc-600';
  };

  const getStatusLabel = (status: string | null | undefined) => {
    if (!status) return 'Không rõ';
    const statusOption = statusOptions.find((opt) => opt.value === status);
    return statusOption?.label || status;
  };

  if (loading) {
    return (
      <section className="px-4 py-20 sm:px-8">
        <div className="mx-auto flex max-w-7xl items-center justify-center py-20">
          <BiLoaderAlt className="animate-spin text-zinc-400" size={48} />
        </div>
      </section>
    );
  }

  if (error || !order) {
    return (
      <section className="px-4 py-20 sm:px-8">
        <div className="mx-auto max-w-7xl">
          <div className="rounded-3xl border border-red-200 bg-red-50 p-12 text-center text-red-600">
            <p className="text-lg">{error || 'Order not found'}</p>
            <button onClick={() => router.push('/admin/orders')} className="btn mt-4">
              Back to Orders
            </button>
          </div>
        </div>
      </section>
    );
  }

  const availableStatuses = getAvailableStatuses(order.orderStatus);
  const canUpdateStatus = availableStatuses.length > 0;

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-8">
        <div className="space-y-4">
          <button
            onClick={() => router.push('/admin/orders')}
            className="flex items-center gap-2 text-zinc-600 transition hover:text-zinc-900"
          >
            <AiOutlineArrowLeft size={20} />
            <span className="font-semibold">Back to Orders List</span>
          </button>
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Order Details</p>
          <h1 className="text-5xl font-bold">Order #{order.orderId}</h1>
        </div>

        <div className="grid gap-8 lg:grid-cols-3">
          <div className="lg:col-span-2 space-y-6">
            <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
              <h2 className="text-2xl font-bold mb-6">Order Items</h2>
              <div className="space-y-4">
                {order.orderItems.map((item) => (
                  <div
                    key={item.orderItemId}
                    className="flex items-start gap-4 rounded-xl border border-zinc-200 p-4"
                  >
                    <div className="flex-1">
                      <h3 className="font-bold text-zinc-900">{item.productName}</h3>
                      <p className="mt-1 text-sm text-zinc-600">
                        {item.colorName} | Size {item.sizeValue} | SKU: {item.sku}
                      </p>
                      <div className="mt-2 flex items-center gap-4 text-sm">
                        <span className="text-zinc-600">
                          Quantity: <span className="font-semibold">{item.quantity}</span>
                        </span>
                        <span className="text-zinc-600">
                          Unit Price: <span className="font-semibold">{formatCurrency(item.unitPrice)}</span>
                        </span>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-lg font-bold text-zinc-900">
                        {formatCurrency(item.subtotal)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
              <div className="mt-6 border-t border-zinc-200 pt-6">
                <div className="flex items-center justify-between">
                  <span className="text-xl font-bold">Total Amount</span>
                  <span className="text-3xl font-bold text-zinc-900">
                    {formatCurrency(order.totalAmount)}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            <div className="rounded-3xl border border-zinc-200 bg-white shadow-sm overflow-hidden">
              <div className="border-b border-zinc-200 bg-zinc-50 px-6 py-4">
                <h3 className="font-semibold text-zinc-900">Order Information</h3>
              </div>
              <div className="divide-y divide-zinc-100">
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Order ID</span>
                  <p className="mt-1 font-bold text-zinc-900">#{order.orderId}</p>
                </div>
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Order Date</span>
                  <p className="mt-1 font-semibold text-zinc-900">{formatDate(order.orderDate)}</p>
                </div>
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Status</span>
                  <p className="mt-1">
                    <span className={`inline-block rounded-full px-3 py-1 text-sm font-semibold ${getStatusColor(order.orderStatus)}`}>
                      {getStatusLabel(order.orderStatus)}
                    </span>
                  </p>
                </div>
              </div>
            </div>

            <div className="rounded-3xl border border-zinc-200 bg-white shadow-sm overflow-hidden">
              <div className="border-b border-zinc-200 bg-zinc-50 px-6 py-4">
                <h3 className="font-semibold text-zinc-900">Shipping Information</h3>
              </div>
              <div className="divide-y divide-zinc-100">
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Name</span>
                  <p className="mt-1 font-semibold text-zinc-900">{order.shippingName}</p>
                </div>
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Phone</span>
                  <p className="mt-1 font-semibold text-zinc-900">{order.shippingPhone}</p>
                </div>
                <div className="px-6 py-3">
                  <span className="text-sm font-medium text-zinc-500">Address</span>
                  <p className="mt-1 font-semibold text-zinc-900">
                    {order.shippingTenDuong}
                    <br />
                    {order.shippingXaQuan}
                    <br />
                    {order.shippingTinhThanh}
                  </p>
                </div>
              </div>
            </div>

            {canUpdateStatus && (
              <div className="rounded-3xl border border-zinc-200 bg-white p-6 shadow-sm">
                <h3 className="mb-4 font-semibold text-zinc-900">Update Status</h3>
                <select
                  value={selectedStatus}
                  onChange={(e) => setSelectedStatus(e.target.value as OrderStatus)}
                  className="input w-full mb-4"
                >
                  <option value={order.orderStatus}>{getStatusLabel(order.orderStatus)} (Hiện tại)</option>
                  {availableStatuses.map((status) => (
                    <option key={status} value={status}>
                      {getStatusLabel(status)}
                    </option>
                  ))}
                </select>
                <button
                  onClick={handleUpdateStatus}
                  disabled={selectedStatus === order.orderStatus || !selectedStatus}
                  className="btn w-full disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Update Status
                </button>
              </div>
            )}

            {!isStatusFinalized(order.orderStatus) && (
              <button
                onClick={handleCancelOrder}
                className="w-full rounded-full border-2 border-red-500 px-6 py-3 font-semibold text-red-500 transition hover:bg-red-500 hover:text-white"
              >
                Cancel Order
              </button>
            )}
          </div>
        </div>
      </div>
    </section>
  );
};

export default AdminOrderDetail;
