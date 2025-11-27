import { useEffect, useState } from 'react';

import { useFormik } from 'formik';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';

import { addressService } from '@/common/api/services/addressService';
import { orderService } from '@/common/api/services/orderService';
import cartAtom from '@/common/recoil/cart';
import { useClearCart } from '@/common/recoil/cart/cart.hooks';
import { useModal } from '@/common/recoil/modal';
import userAtom from '@/common/recoil/user';
import type { Address } from '@/common/types/backend';

import AddressForm from './AddressForm';

const Checkout = () => {
  const user = useRecoilValue(userAtom);
  const cart = useRecoilValue(cartAtom);
  const clearCart = useClearCart();
  const router = useRouter();
  const { openModal } = useModal();

  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddress, setSelectedAddress] = useState<Address | null>(null);
  const [showAddressForm, setShowAddressForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!user.id) {
      router.push('/login');
      return;
    }

    const fetchAddresses = async () => {
      try {
        const data = await addressService.getAddresses();
        setAddresses(data);

        const defaultAddress = data.find((addr) => addr.isDefault === true);
        if (defaultAddress) {
          setSelectedAddress(defaultAddress);
        } else if (data.length === 0) {
          setShowAddressForm(true);
        } else if (data.length > 0) {
          setSelectedAddress(data[0]);
        }
      } catch (error) {
        console.error('Failed to load addresses:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAddresses();
  }, [user.id, router]);

  const addressFormik = useFormik({
    initialValues: {
      fullName: '',
      phone: '',
      tenDuong: '',
      xaQuan: '',
      tinhThanh: '',
      isDefault: false,
    },
    validate: (values) => {
      const errors: { [key: string]: string } = {};

      if (!values.fullName || values.fullName.trim().length < 2) {
        errors.fullName = 'Họ và tên phải có ít nhất 2 ký tự';
      }

      if (!values.phone || !/^[0-9]{10,11}$/.test(values.phone)) {
        errors.phone = 'Số điện thoại không hợp lệ (10-11 chữ số)';
      }

      if (!values.tenDuong || values.tenDuong.trim().length < 5) {
        errors.tenDuong = 'Địa chỉ đường phải có ít nhất 5 ký tự';
      }

      if (!values.xaQuan || values.xaQuan.trim().length < 3) {
        errors.xaQuan = 'Xã/Quận phải có ít nhất 3 ký tự';
      }

      if (!values.tinhThanh) {
        errors.tinhThanh = 'Vui lòng chọn tỉnh/thành phố';
      }

      return errors;
    },
    validateOnBlur: true,
    validateOnChange: false,
    onSubmit: () => {},
  });

  const totalPrice = cart.attributes.products.reduce((acc, product) => {
    return (
      acc +
      product.quantity *
        (product.attributes.promotionPrice || product.attributes.price)
    );
  }, 0);

  const handlePlaceOrder = async () => {
    if (cart.attributes.products.length === 0) {
      openModal(
        <div className="max-w-md rounded-md bg-white p-10">
          <h2 className="text-xl font-semibold">Giỏ hàng trống</h2>
          <p className="mt-2">Vui lòng thêm sản phẩm vào giỏ hàng trước khi đặt hàng.</p>
          <button className="btn mt-5 w-full" onClick={() => router.push('/shoes')}>
            Mua sắm ngay
          </button>
        </div>
      );
      return;
    }

    setSubmitting(true);

    try {
      let addressId: number;

      if (showAddressForm) {
        const errors = await addressFormik.validateForm();
        if (Object.keys(errors).length > 0) {
          addressFormik.setTouched({
            fullName: true,
            phone: true,
            tenDuong: true,
            xaQuan: true,
            tinhThanh: true,
          });
          setSubmitting(false);
          return;
        }

        const newAddress = await addressService.createAddress({
          fullName: addressFormik.values.fullName,
          phone: addressFormik.values.phone,
          tenDuong: addressFormik.values.tenDuong,
          xaQuan: addressFormik.values.xaQuan,
          tinhThanh: addressFormik.values.tinhThanh,
          isDefault: addressFormik.values.isDefault,
        });

        addressId = newAddress.addressId;
      } else if (selectedAddress) {
        addressId = selectedAddress.addressId;
      } else {
        openModal(
          <div className="max-w-md rounded-md bg-white p-10">
            <h2 className="text-xl font-semibold">Chưa chọn địa chỉ</h2>
            <p className="mt-2">Vui lòng chọn địa chỉ giao hàng.</p>
          </div>
        );
        setSubmitting(false);
        return;
      }

      const order = await orderService.createOrder({ addressId });

      clearCart();

      router.push(`/new-order?orderId=${order.orderId}`);
    } catch (error: any) {
      console.error('Failed to place order:', error);
      openModal(
        <div className="max-w-md rounded-md bg-white p-10">
          <h2 className="text-xl font-semibold text-red-500">Đặt hàng thất bại</h2>
          <p className="mt-2">
            {error?.response?.data?.message || 'Đã có lỗi xảy ra. Vui lòng thử lại.'}
          </p>
        </div>
      );
    } finally {
      setSubmitting(false);
    }
  };

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

  return (
    <div className="container mx-auto mt-10 px-4 pb-20">
      <h1 className="mb-8 text-3xl font-bold">Thanh toán</h1>

      <div className="grid grid-cols-1 gap-10 lg:grid-cols-2">
        <div>
          {!showAddressForm && addresses.length > 0 && (
            <div className="mb-6">
              <h2 className="mb-4 text-2xl font-semibold">Địa chỉ giao hàng</h2>

              <div className="space-y-3">
                {addresses.map((address) => (
                  <div
                    key={address.addressId}
                    className={`cursor-pointer rounded-lg border-2 p-4 transition-colors ${
                      selectedAddress?.addressId === address.addressId
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                    onClick={() => setSelectedAddress(address)}
                  >
                    <div className="flex items-start justify-between">
                      <div>
                        <p className="font-semibold">{address.fullName}</p>
                        <p className="text-sm text-gray-600">{address.phone}</p>
                        <p className="mt-1 text-sm">
                          {address.tenDuong}, {address.xaQuan}, {address.tinhThanh}
                        </p>
                      </div>
                      {address.isDefault === true && (
                        <span className="rounded bg-green-100 px-2 py-1 text-xs font-semibold text-green-600">
                          Mặc định
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              <button
                className="mt-4 text-sm text-blue-600 hover:underline"
                onClick={() => setShowAddressForm(true)}
              >
                + Thêm địa chỉ mới
              </button>
            </div>
          )}

          {showAddressForm && (
            <div>
              <AddressForm formik={addressFormik} />

              {addresses.length > 0 && (
                <button
                  className="mt-4 text-sm text-blue-600 hover:underline"
                  onClick={() => setShowAddressForm(false)}
                >
                  ← Chọn địa chỉ đã lưu
                </button>
              )}
            </div>
          )}
        </div>

        <div>
          <div className="rounded-lg border p-6">
            <h2 className="mb-4 text-2xl font-semibold">Đơn hàng của bạn</h2>

            <div className="mb-4 max-h-96 space-y-3 overflow-y-auto">
              {cart.attributes.products.map((product) => (
                <div key={product.id + product.size} className="flex gap-3 border-b pb-3">
                  <img
                    src={product.attributes.images.data[0]?.attributes.hash}
                    alt={product.attributes.name}
                    className="h-20 w-20 rounded object-cover"
                  />
                  <div className="flex-1">
                    <p className="font-semibold">{product.attributes.name}</p>
                    <p className="text-sm text-gray-600">
                      Size: {product.size} | Số lượng: {product.quantity}
                    </p>
                    <p className="mt-1 font-semibold">
                      {((product.attributes.promotionPrice || product.attributes.price) *
                        product.quantity).toLocaleString('vi-VN')}
                      ₫
                    </p>
                  </div>
                </div>
              ))}
            </div>

            <div className="border-t pt-4">
              <div className="flex justify-between text-lg font-semibold">
                <span>Tổng cộng:</span>
                <span>{totalPrice.toLocaleString('vi-VN')}₫</span>
              </div>

              <p className="mt-2 text-sm text-gray-600">Miễn phí vận chuyển</p>

              <button
                className="btn mt-6 w-full py-3 text-lg disabled:cursor-not-allowed disabled:opacity-50"
                onClick={handlePlaceOrder}
                disabled={submitting}
              >
                {submitting ? 'Đang xử lý...' : 'Đặt hàng'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
