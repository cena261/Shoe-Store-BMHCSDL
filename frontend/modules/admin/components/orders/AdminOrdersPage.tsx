import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import { orderAdminService } from '@/common/api/services/orderAdminService';
import { OrderAdminResponse, OrderStatus } from '@/common/types/backend';
import { AiOutlineSearch, AiOutlineEye, AiOutlineArrowLeft } from 'react-icons/ai';
import { BiLoaderAlt } from 'react-icons/bi';
import { useToast } from '@/common/recoil/toast/toast.hooks';

const AdminOrdersPage = () => {
  const router = useRouter();
  const toast = useToast();

  const [orders, setOrders] = useState<OrderAdminResponse[]>([]);
  const [filteredOrders, setFilteredOrders] = useState<OrderAdminResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<OrderStatus | ''>('');

  const statusOptions: { value: OrderStatus | ''; label: string; color: string }[] = [
    { value: '', label: 'Tất cả trạng thái', color: 'bg-zinc-100 text-zinc-600' },
    { value: 'PENDING', label: 'Chờ xác nhận', color: 'bg-yellow-100 text-yellow-600' },
    { value: 'CONFIRMED', label: 'Đợi giao hàng', color: 'bg-blue-100 text-blue-600' },
    { value: 'ON_DELIVERY', label: 'Đang giao hàng', color: 'bg-purple-100 text-purple-600' },
    { value: 'SUCCESS', label: 'Giao hàng thành công', color: 'bg-green-100 text-green-600' },
    { value: 'CANCELLED', label: 'Đã hủy', color: 'bg-orange-100 text-orange-600' },
    { value: 'FAILED', label: 'Giao hàng thất bại', color: 'bg-red-100 text-red-600' },
  ];

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await orderAdminService.getAllOrders();
      setOrders(data);
      setFilteredOrders(data);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  useEffect(() => {
    let result = [...orders];

    if (statusFilter) {
      result = result.filter((order) => order.orderStatus === statusFilter);
    }

    if (search) {
      const searchLower = search.toLowerCase();
      result = result.filter(
        (order) =>
          order.orderId.toString().includes(searchLower) ||
          order.shippingName.toLowerCase().includes(searchLower) ||
          order.shippingPhone.toLowerCase().includes(searchLower) ||
          order.shippingTinhThanh.toLowerCase().includes(searchLower)
      );
    }

    setFilteredOrders(result);
  }, [orders, statusFilter, search]);

  const handleClearSearch = () => {
    setSearch('');
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

  const getStatusCounts = () => {
    const counts: { [key: string]: number } = {
      '': orders.length,
      PENDING: 0,
      CONFIRMED: 0,
      ON_DELIVERY: 0,
      SUCCESS: 0,
      CANCELLED: 0,
      FAILED: 0,
    };

    orders.forEach((order) => {
      if (order.orderStatus) {
        counts[order.orderStatus] = (counts[order.orderStatus] || 0) + 1;
      }
    });

    return counts;
  };

  const statusCounts = getStatusCounts();

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-8">
        <div className="space-y-4">
          <button
            onClick={() => router.push('/admin')}
            className="flex items-center gap-2 text-zinc-600 transition hover:text-zinc-900"
          >
            <AiOutlineArrowLeft size={20} />
            <span className="font-semibold">Back to Admin Dashboard</span>
          </button>
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Order Management</p>
          <h1 className="text-5xl font-bold">Orders</h1>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {statusOptions.slice(1).map((status) => (
            <div
              key={status.value}
              className="rounded-3xl border border-zinc-200 bg-white p-4 shadow-sm"
            >
              <p className="text-sm font-medium text-zinc-500">{status.label}</p>
              <p className="mt-1 text-3xl font-bold">{statusCounts[status.value]}</p>
            </div>
          ))}
        </div>

        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex flex-1 gap-2">
            <div className="relative flex-1 max-w-md">
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by order ID, customer, phone, location..."
                className="input w-full pr-10"
              />
              <AiOutlineSearch className="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400" />
            </div>
            {search && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="rounded-full border border-zinc-200 px-6 py-2 font-semibold transition hover:border-zinc-900"
              >
                Clear
              </button>
            )}
          </div>

          <div className="flex flex-wrap gap-2">
            {statusOptions.map((status) => (
              <button
                key={status.value}
                onClick={() => setStatusFilter(status.value)}
                className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
                  statusFilter === status.value
                    ? 'bg-black text-white'
                    : 'border border-zinc-200 hover:border-zinc-900'
                }`}
              >
                {status.label}
              </button>
            ))}
          </div>
        </div>

        {error && (
          <div className="rounded-3xl border border-red-200 bg-red-50 p-4 text-center text-red-600">
            {error}
          </div>
        )}

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <BiLoaderAlt className="animate-spin text-zinc-400" size={48} />
          </div>
        ) : filteredOrders.length === 0 ? (
          <div className="rounded-3xl border border-zinc-200 bg-white p-12 text-center">
            <p className="text-lg text-zinc-500">No orders found</p>
          </div>
        ) : (
          <div className="rounded-3xl border border-zinc-200 bg-white shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="border-b border-zinc-200 bg-zinc-50">
                  <tr>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Order ID</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Customer</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Phone</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Order Date</th>
                    <th className="px-6 py-4 text-right text-sm font-semibold text-zinc-700">Total Amount</th>
                    <th className="px-6 py-4 text-center text-sm font-semibold text-zinc-700">Status</th>
                    <th className="px-6 py-4 text-left text-sm font-semibold text-zinc-700">Location</th>
                    <th className="px-6 py-4 text-center text-sm font-semibold text-zinc-700">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredOrders.map((order) => (
                    <tr
                      key={order.orderId}
                      className="border-b border-zinc-100 hover:bg-zinc-50 transition"
                    >
                      <td className="px-6 py-4">
                        <span className="font-bold text-zinc-900">#{order.orderId}</span>
                      </td>
                      <td className="px-6 py-4 text-zinc-700">{order.shippingName}</td>
                      <td className="px-6 py-4 text-zinc-700">{order.shippingPhone}</td>
                      <td className="px-6 py-4 text-zinc-700">{formatDate(order.orderDate)}</td>
                      <td className="px-6 py-4 text-right font-semibold text-zinc-900">
                        {formatCurrency(order.totalAmount)}
                      </td>
                      <td className="px-6 py-4 text-center">
                        <span
                          className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${getStatusColor(
                            order.orderStatus
                          )}`}
                        >
                          {getStatusLabel(order.orderStatus)}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-zinc-700">{order.shippingTinhThanh}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center justify-center">
                          <button
                            onClick={() => router.push(`/admin/orders/${order.orderId}`)}
                            className="rounded-full bg-cyan-500 p-2 text-white transition hover:bg-cyan-600"
                            title="View Details"
                          >
                            <AiOutlineEye size={16} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {!loading && filteredOrders.length > 0 && (
          <div className="text-center text-sm text-zinc-600">
            Showing {filteredOrders.length} of {orders.length} orders
          </div>
        )}
      </div>
    </section>
  );
};

export default AdminOrdersPage;
