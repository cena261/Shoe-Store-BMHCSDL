import { useEffect, useState } from 'react';

import { motion } from 'framer-motion';
import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';

import { orderService } from '@/common/api/services/orderService';
import { defaultEase } from '@/common/animations/easings';
import type { Order } from '@/common/types/backend';

const containerAnimation = {
  from: {
    opacity: 0,
    y: -100,
  },
  to: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.5,
      ease: defaultEase,
    },
  },
};

const NewOrderPage: NextPage = () => {
  const router = useRouter();
  const { orderId } = router.query;

  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!orderId) return;

    const fetchOrder = async () => {
      try {
        const data = await orderService.getOrderById(Number(orderId));
        setOrder(data);
      } catch (err) {
        console.error('Failed to load order:', err);
        setError('Không thể tải thông tin đơn hàng');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <div className="inline-block h-12 w-12 animate-spin rounded-full border-4 border-solid border-current border-r-transparent"></div>
          <p className="mt-4 text-gray-600">Đang tải...</p>
        </div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="flex min-h-screen items-center justify-center p-5">
        <div className="max-w-md text-center">
          <h1 className="text-3xl font-bold text-red-500">Lỗi</h1>
          <p className="mt-4 text-gray-600">{error || 'Không tìm thấy đơn hàng'}</p>
          <Link href="/">
            <a className="btn mt-5 inline-block">Về trang chủ</a>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <motion.div
      className="container mx-auto mt-10 px-4 pb-20"
      variants={containerAnimation}
      initial="from"
      animate="to"
    >
      <div className="mx-auto max-w-3xl">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
            <svg
              className="h-10 w-10 text-green-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>

          <h1 className="text-4xl font-bold">Đặt hàng thành công!</h1>
          <p className="mt-2 text-lg text-gray-600">
            Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ xử lý đơn hàng của bạn ngay lập tức.
          </p>
        </div>

        <div className="rounded-lg border bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-2xl font-semibold">Chi tiết đơn hàng</h2>

          <div className="grid grid-cols-2 gap-4 border-b pb-4">
            <div>
              <p className="text-sm text-gray-600">Mã đơn hàng</p>
              <p className="font-semibold">#{order.orderId}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Ngày đặt</p>
              <p className="font-semibold">
                {new Date(order.orderDate).toLocaleDateString('vi-VN', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Trạng thái</p>
              <p className="font-semibold">
                <span className="rounded bg-yellow-100 px-2 py-1 text-sm text-yellow-700">
                  {order.orderStatus}
                </span>
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Tổng tiền</p>
              <p className="text-xl font-bold text-green-600">
                {order.totalAmount.toLocaleString('vi-VN')}₫
              </p>
            </div>
          </div>

          <div className="mt-4 border-b pb-4">
            <h3 className="mb-2 font-semibold">Địa chỉ giao hàng</h3>
            <p className="font-semibold">{order.shippingName}</p>
            <p className="text-sm text-gray-600">{order.shippingPhone}</p>
            <p className="text-sm">
              {order.shippingTenDuong}, {order.shippingXaQuan}, {order.shippingTinhThanh}
            </p>
          </div>

          <div className="mt-4">
            <h3 className="mb-3 font-semibold">Sản phẩm</h3>
            <div className="space-y-3">
              {order.orderItems.map((item) => (
                <div key={item.orderItemId} className="flex justify-between border-b pb-3">
                  <div>
                    <p className="font-semibold">{item.productName}</p>
                    <p className="text-sm text-gray-600">
                      {item.colorName} - Size {item.sizeValue}
                    </p>
                    <p className="text-sm text-gray-600">Số lượng: {item.quantity}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">
                      {item.subtotal.toLocaleString('vi-VN')}₫
                    </p>
                    <p className="text-sm text-gray-600">
                      {item.unitPrice.toLocaleString('vi-VN')}₫ / sản phẩm
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="mt-8 flex justify-center gap-4">
          <Link href="/">
            <a className="btn">Về trang chủ</a>
          </Link>
          <Link href="/shoes">
            <a className="btn bg-white text-black hover:bg-gray-100">Tiếp tục mua sắm</a>
          </Link>
        </div>
      </div>
    </motion.div>
  );
};

export default NewOrderPage;
