import { useState } from 'react';

import { useModal } from '@/common/recoil/modal';
import type { Order } from '@/common/types/backend';

interface OrderHistoryProps {
  orders: Order[];
  showAll?: boolean;
}

const OrderHistory = ({ orders, showAll = false }: OrderHistoryProps) => {
  const { closeModal } = useModal();
  const [expandedOrder, setExpandedOrder] = useState<number | null>(null);

  const getStatusConfig = (status: string) => {
    const statusMap: { [key: string]: { label: string; bgClass: string; textClass: string } } = {
      PENDING: { label: 'Chờ xác nhận', bgClass: 'bg-amber-100', textClass: 'text-amber-700' },
      CONFIRMED: {
        label: 'Chờ giao hàng',
        bgClass: 'bg-blue-100',
        textClass: 'text-blue-700',
      },
      ON_DELIVERY: {
        label: 'Đang giao hàng',
        bgClass: 'bg-indigo-100',
        textClass: 'text-indigo-700',
      },
      SUCCESS: {
        label: 'Giao hàng thành công',
        bgClass: 'bg-emerald-100',
        textClass: 'text-emerald-700',
      },
      CANCELLED: { label: 'Đã hủy đơn', bgClass: 'bg-red-100', textClass: 'text-red-700' },
      FAILED: { label: 'Giao hàng thất bại', bgClass: 'bg-orange-100', textClass: 'text-orange-700' },
    };

    return statusMap[status] || { label: status, bgClass: 'bg-gray-100', textClass: 'text-gray-700' };
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const toggleOrderDetails = (orderId: number) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId);
  };

  if (showAll) {
    return (
      <div className="w-full max-w-6xl rounded-3xl bg-white p-10">
        <div className="mb-8 flex items-center justify-between">
          <h2 className="text-4xl font-bold">Tất cả đơn hàng</h2>
          <button
            onClick={closeModal}
            className="text-4xl text-zinc-400 transition hover:text-zinc-600"
          >
            &times;
          </button>
        </div>

        <div className="max-h-[600px] space-y-5 overflow-y-auto">
          {orders.map((order) => {
            const statusConfig = getStatusConfig(order.orderStatus);
            const isExpanded = expandedOrder === order.orderId;

            return (
              <div key={order.orderId} className="rounded-2xl bg-zinc-100/80 p-6">
                <div
                  className="flex cursor-pointer flex-wrap items-center justify-between gap-4"
                  onClick={() => toggleOrderDetails(order.orderId)}
                >
                  <div>
                    <p className="text-lg font-semibold">Đơn hàng #{order.orderId}</p>
                    <p className="text-base text-zinc-500">
                      Đặt vào lúc {formatDate(order.orderDate)} •{' '}
                      {order.totalAmount.toLocaleString('vi-VN')}₫
                    </p>
                  </div>
                  <span
                    className={`rounded-full ${statusConfig.bgClass} px-4 py-2 text-sm font-semibold ${statusConfig.textClass}`}
                  >
                    {statusConfig.label}
                  </span>
                </div>

                {isExpanded && order.orderItems && order.orderItems.length > 0 && (
                  <div className="mt-5 space-y-3 border-t border-zinc-300 pt-5">
                    <p className="text-base font-semibold">Sản phẩm:</p>
                    {order.orderItems.map((item) => (
                      <div key={item.orderItemId} className="flex justify-between text-base">
                        <div>
                          <p className="font-medium">{item.productName}</p>
                          <p className="text-sm text-zinc-500">
                            {item.colorName} - Size {item.sizeValue} - x{item.quantity}
                          </p>
                        </div>
                        <p className="font-semibold">{item.subtotal.toLocaleString('vi-VN')}₫</p>
                      </div>
                    ))}
                    <div className="mt-3 border-t border-zinc-300 pt-3">
                      <p className="text-base font-semibold">Địa chỉ giao hàng:</p>
                      <p className="text-base text-zinc-600">{order.shippingName}</p>
                      <p className="text-base text-zinc-600">{order.shippingPhone}</p>
                      <p className="text-base text-zinc-600">
                        {order.shippingTenDuong}, {order.shippingXaQuan}, {order.shippingTinhThanh}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {orders.map((order) => {
        const statusConfig = getStatusConfig(order.orderStatus);

        return (
          <div
            key={order.orderId}
            className="flex flex-wrap items-center justify-between gap-4 rounded-2xl bg-zinc-100/80 p-4"
          >
            <div>
              <p className="text-sm font-semibold">Đơn hàng #{order.orderId}</p>
              <p className="text-xs text-zinc-500">
                Đặt vào lúc {formatDate(order.orderDate)} •{' '}
                {order.totalAmount.toLocaleString('vi-VN')}₫
              </p>
            </div>
            <span
              className={`rounded-full ${statusConfig.bgClass} px-3 py-1 text-xs font-semibold ${statusConfig.textClass}`}
            >
              {statusConfig.label}
            </span>
          </div>
        );
      })}
    </div>
  );
};

export default OrderHistory;
