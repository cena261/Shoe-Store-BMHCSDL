import { useState } from 'react';

import { useFormik } from 'formik';

import { addressService } from '@/common/api/services/addressService';
import InputComponent from '@/common/components/input/components/InputComponent';
import { VIETNAMESE_PROVINCES } from '@/common/lib/vietnameseProvinces';
import { useModal } from '@/common/recoil/modal';
import type { Address, CreateAddressRequest } from '@/common/types/backend';

interface AddressManagementProps {
  addresses: Address[];
  onUpdate: () => void;
}

const AddressManagement = ({ addresses, onUpdate }: AddressManagementProps) => {
  const { closeModal } = useModal();
  const [showForm, setShowForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState<Address | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const formik = useFormik({
    initialValues: {
      fullName: editingAddress?.fullName || '',
      phone: editingAddress?.phone || '',
      tenDuong: editingAddress?.tenDuong || '',
      xaQuan: editingAddress?.xaQuan || '',
      tinhThanh: editingAddress?.tinhThanh || '',
      isDefault: editingAddress?.isDefault || false,
    },
    enableReinitialize: true,
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
    onSubmit: async (values) => {
      setSubmitting(true);
      try {
        if (editingAddress) {
          await addressService.updateAddress(editingAddress.addressId, values);
        } else {
          await addressService.createAddress(values);
        }
        onUpdate();
        setShowForm(false);
        setEditingAddress(null);
        formik.resetForm();
      } catch (error) {
        console.error('Failed to save address:', error);
      } finally {
        setSubmitting(false);
      }
    },
  });

  const handleEdit = (address: Address) => {
    setEditingAddress(address);
    setShowForm(true);
  };

  const handleDelete = async (addressId: number) => {
    try {
      await addressService.deleteAddress(addressId);
      onUpdate();
    } catch (error) {
      console.error('Failed to delete address:', error);
    }
  };

  const handleSetDefault = async (addressId: number) => {
    try {
      await addressService.setDefaultAddress(addressId);
      onUpdate();
    } catch (error) {
      console.error('Failed to set default address:', error);
    }
  };

  const handleAddNew = () => {
    setEditingAddress(null);
    formik.resetForm();
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingAddress(null);
    formik.resetForm();
  };

  return (
    <div className="w-full max-w-6xl rounded-3xl bg-white p-10">
      <div className="mb-8 flex items-center justify-between">
        <h2 className="text-4xl font-bold">Quản lý địa chỉ</h2>
        <button
          onClick={closeModal}
          className="text-4xl text-zinc-400 transition hover:text-zinc-600"
        >
          &times;
        </button>
      </div>

      {!showForm ? (
        <div className="space-y-6">
          <div className="max-h-[600px] space-y-4 overflow-y-auto">
            {addresses.map((address) => (
              <div
                key={address.addressId}
                className="rounded-2xl border-2 border-zinc-200 p-6 transition hover:border-zinc-300"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <p className="text-xl font-semibold">{address.fullName}</p>
                    <p className="text-lg text-zinc-600">{address.phone}</p>
                    <p className="mt-2 text-base">
                      {address.tenDuong}, {address.xaQuan}, {address.tinhThanh}
                    </p>
                    {address.isDefault && (
                      <span className="mt-3 inline-block rounded-lg bg-green-100 px-4 py-2 text-sm font-semibold text-green-600">
                        Mặc định
                      </span>
                    )}
                  </div>
                  <div className="flex gap-3">
                    <button
                      onClick={() => handleEdit(address)}
                      className="rounded-xl border border-blue-200 bg-blue-50 px-6 py-3 text-base font-semibold text-blue-700 transition hover:bg-blue-100"
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => handleDelete(address.addressId)}
                      className="rounded-xl border border-red-200 bg-red-50 px-6 py-3 text-base font-semibold text-red-700 transition hover:bg-red-100"
                    >
                      Xóa
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <button onClick={handleAddNew} className="btn w-full py-4 text-lg font-semibold">
            + Thêm địa chỉ mới
          </button>
        </div>
      ) : (
        <form onSubmit={formik.handleSubmit} className="space-y-6">
          <InputComponent
            label="Họ và tên"
            placeholder="Nhập họ và tên người nhận..."
            name="fullName"
            handleChange={formik.handleChange}
            value={formik.values.fullName}
            errors={formik.errors}
            handleBlur={formik.handleBlur}
          />

          <InputComponent
            label="Số điện thoại"
            placeholder="Nhập số điện thoại..."
            name="phone"
            handleChange={formik.handleChange}
            value={formik.values.phone}
            errors={formik.errors}
            handleBlur={formik.handleBlur}
          />

          <InputComponent
            label="Tên đường / Tòa nhà"
            placeholder="Nhập địa chỉ đường..."
            name="tenDuong"
            handleChange={formik.handleChange}
            value={formik.values.tenDuong}
            errors={formik.errors}
            handleBlur={formik.handleBlur}
          />

          <InputComponent
            label="Xã / Quận"
            placeholder="Nhập xã / quận..."
            name="xaQuan"
            handleChange={formik.handleChange}
            value={formik.values.xaQuan}
            errors={formik.errors}
            handleBlur={formik.handleBlur}
          />

          <div className="flex flex-col gap-1">
            <label className="text-sm font-semibold">Tỉnh / Thành phố</label>
            <select
              name="tinhThanh"
              value={formik.values.tinhThanh}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className="input"
            >
              <option value="">Chọn tỉnh / thành phố</option>
              {VIETNAMESE_PROVINCES.map((province) => (
                <option key={province} value={province}>
                  {province}
                </option>
              ))}
            </select>
            {formik.errors.tinhThanh && formik.touched.tinhThanh && (
              <span className="text-sm text-red-500">{formik.errors.tinhThanh}</span>
            )}
          </div>

          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => formik.setFieldValue('isDefault', !formik.values.isDefault)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                formik.values.isDefault
                  ? 'bg-blue-500 text-white hover:bg-blue-600'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              {formik.values.isDefault ? '✓ Địa chỉ mặc định' : 'Đặt làm địa chỉ mặc định'}
            </button>
          </div>

          <div className="flex gap-4">
            <button
              type="button"
              onClick={handleCancel}
              className="flex-1 rounded-full border border-zinc-300 py-4 text-lg font-semibold transition hover:bg-zinc-100"
            >
              Hủy
            </button>
            <button
              type="submit"
              className="btn flex-1 py-4 text-lg font-semibold disabled:cursor-not-allowed disabled:opacity-50"
              disabled={submitting}
            >
              {submitting ? 'Đang lưu...' : editingAddress ? 'Cập nhật' : 'Thêm địa chỉ'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

export default AddressManagement;
