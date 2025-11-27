import { useState, useEffect } from 'react';

import { useRecoilValue } from 'recoil';

import { authService } from '@/common/api/services/authService';
import { addressService } from '@/common/api/services/addressService';
import { orderService } from '@/common/api/services/orderService';
import { useModal } from '@/common/recoil/modal';
import userAtom, { useLogout } from '@/common/recoil/user';
import type { Address, Order } from '@/common/types/backend';

import AddressManagement from './AddressManagement';
import ChangePasswordModal from './ChangePasswordModal';
import OrderHistory from './OrderHistory';

const Account = () => {
  const user = useRecoilValue(userAtom);
  const { handleLogout } = useLogout();
  const { openModal } = useModal();

  const [addresses, setAddresses] = useState<Address[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loadingAddresses, setLoadingAddresses] = useState(true);
  const [loadingOrders, setLoadingOrders] = useState(true);

  useEffect(() => {
    loadAddresses();
    loadOrders();
  }, []);

  const loadAddresses = async () => {
    try {
      setLoadingAddresses(true);
      const data = await addressService.getAddresses();
      setAddresses(data);
    } catch (error) {
      console.error('Failed to load addresses:', error);
    } finally {
      setLoadingAddresses(false);
    }
  };

  const loadOrders = async () => {
    try {
      setLoadingOrders(true);
      const data = await orderService.getMyOrders();
      setOrders(data);
    } catch (error) {
      console.error('Failed to load orders:', error);
    } finally {
      setLoadingOrders(false);
    }
  };

  const handleChangePassword = () => {
    openModal(<ChangePasswordModal />);
  };

  const defaultAddress = addresses.find((addr) => addr.isDefault === true);

  return (
    <section className="px-4 py-20 sm:px-8">
      <div className="mx-auto flex max-w-4xl flex-col gap-10">
        <div className="space-y-4">
          <p className="text-sm uppercase tracking-[0.35em] text-zinc-400">Dashboard</p>
          <h1 className="text-5xl font-bold">
            Xin chào <span>{user.fullName || user.username || 'Khách'}!</span>
          </h1>
          <p className="text-base text-zinc-500">
            Quản lý thông tin cá nhân, địa chỉ, trạng thái đơn hàng.
          </p>
          <div className="flex flex-wrap gap-4">
            <button className="btn rounded-full px-6 py-3" onClick={handleLogout}>
              Đăng xuất
            </button>
            <a
              href="/shoes"
              className="rounded-full border border-zinc-900 px-6 py-3 font-semibold transition hover:bg-zinc-900 hover:text-white"
            >
              Xem danh sách các sản phẩm
            </a>
          </div>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm">
            <h2 className="text-lg font-semibold">Trang cá nhân</h2>
            <p className="mt-3 text-sm text-zinc-500">
              Tên: <span>{user.fullName || user.username || 'Khách'}</span>
            </p>
            <p className="text-sm text-zinc-500">
              Email: <span>{user.email || 'guest@example.com'}</span>
            </p>
            {user.phone && (
              <p className="text-sm text-zinc-500">
                Điện thoại: <span>{user.phone}</span>
              </p>
            )}
            <button
              className="mt-4 rounded-full border border-zinc-200 px-4 py-2 text-sm font-semibold transition hover:border-zinc-900 hover:bg-zinc-900 hover:text-white"
              onClick={handleChangePassword}
            >
              Đổi mật khẩu
            </button>
          </article>

          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm">
            <h2 className="text-lg font-semibold">Địa chỉ đã lưu</h2>
            <div className="mt-3 min-h-[80px]">
              {loadingAddresses ? (
                <p className="text-sm text-zinc-500">Đang tải địa chỉ...</p>
              ) : defaultAddress ? (
                <div>
                  <p className="text-sm text-zinc-500">
                    {defaultAddress.fullName}, {defaultAddress.phone}
                  </p>
                  <p className="text-sm text-zinc-500">
                    {defaultAddress.tenDuong}, {defaultAddress.xaQuan}, {defaultAddress.tinhThanh}
                  </p>
                  <span className="mt-1 inline-block text-xs font-semibold text-green-600">
                    (Mặc định)
                  </span>
                </div>
              ) : (
                <p className="text-sm text-zinc-500">Chưa có địa chỉ nào</p>
              )}
            </div>
            <button
              className="mt-4 rounded-full border border-zinc-200 px-4 py-2 text-sm font-semibold transition hover:border-zinc-900 hover:bg-zinc-900 hover:text-white"
              onClick={() =>
                openModal(<AddressManagement addresses={addresses} onUpdate={loadAddresses} />)
              }
            >
              Quản lý địa chỉ
            </button>
          </article>

          <article className="rounded-3xl border border-zinc-200 p-6 shadow-sm md:col-span-2">
            <h2 className="text-lg font-semibold">Các đơn hàng gần đây</h2>
            <div className="mt-5">
              {loadingOrders ? (
                <p className="text-sm text-zinc-500">Đang tải đơn hàng...</p>
              ) : orders.length > 0 ? (
                <OrderHistory orders={orders.slice(0, 3)} />
              ) : (
                <p className="text-sm text-zinc-500">Chưa có đơn hàng nào</p>
              )}
            </div>
            {orders.length > 3 && (
              <button
                className="mt-4 rounded-full border border-zinc-200 px-4 py-2 text-sm font-semibold transition hover:border-zinc-900 hover:bg-zinc-900 hover:text-white"
                onClick={() => openModal(<OrderHistory orders={orders} showAll />)}
              >
                Xem tất cả đơn hàng
              </button>
            )}
          </article>
        </div>
      </div>
    </section>
  );
};

export default Account;
