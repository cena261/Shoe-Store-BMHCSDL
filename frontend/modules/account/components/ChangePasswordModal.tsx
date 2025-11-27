import { useState } from 'react';

import { useFormik } from 'formik';

import { authService } from '@/common/api/services/authService';
import { useModal } from '@/common/recoil/modal';

const ChangePasswordModal = () => {
  const { closeModal } = useModal();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const formik = useFormik({
    initialValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
    validate: (values) => {
      const errors: { [key: string]: string } = {};

      if (!values.currentPassword) {
        errors.currentPassword = 'Phải có mật khẩu hiện tại';
      }

      if (!values.newPassword) {
        errors.newPassword = 'Phải có mật khẩu mới';
      } else if (values.newPassword.length < 8) {
        errors.newPassword = 'Mật khẩu mới phải có ít nhất 8 ký tự';
      }

      if (!values.confirmPassword) {
        errors.confirmPassword = 'Phải xác nhận mật khẩu mới';
      } else if (values.newPassword !== values.confirmPassword) {
        errors.confirmPassword = 'Mật khẩu không khớp';
      }

      return errors;
    },
    onSubmit: async (values, { setSubmitting }) => {
      setError('');
      setSuccess(false);

      try {
        await authService.changePassword({
          currentPassword: values.currentPassword,
          newPassword: values.newPassword,
        });

        setSuccess(true);
        setTimeout(() => {
          closeModal();
        }, 2000);
      } catch (err: any) {
        if (err.response?.data?.code === '1010') {
          setError('Mật khẩu hiện tại không đúng');
        } else {
          setError(err.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại.');
        }
      } finally {
        setSubmitting(false);
      }
    },
  });

  return (
    <div className="w-full max-w-5xl rounded-3xl bg-white p-12">
      <div className="mb-10 flex items-center justify-between">
        <h2 className="text-5xl font-bold">Đổi mật khẩu</h2>
        <button
          onClick={closeModal}
          className="text-5xl text-zinc-400 transition hover:text-zinc-600"
        >
          &times;
        </button>
      </div>

      {success && (
        <div className="mb-8 rounded-xl bg-green-100 p-6 text-xl text-green-700">
          Đổi mật khẩu thành công! Đang đóng...
        </div>
      )}

      {error && (
        <div className="mb-8 rounded-xl bg-red-100 p-6 text-xl text-red-700">{error}</div>
      )}

      <form onSubmit={formik.handleSubmit} className="space-y-8">
        <div>
          <label className="mb-4 block text-xl font-semibold">Mật khẩu hiện tại</label>
          <input
            type="password"
            name="currentPassword"
            className="input w-full text-xl"
            placeholder="Nhập mật khẩu hiện tại"
            value={formik.values.currentPassword}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.touched.currentPassword && formik.errors.currentPassword && (
            <p className="mt-2 text-base italic text-red-500">{formik.errors.currentPassword}</p>
          )}
        </div>

        <div>
          <label className="mb-4 block text-xl font-semibold">Mật khẩu mới</label>
          <input
            type="password"
            name="newPassword"
            className="input w-full text-xl"
            placeholder="Nhập mật khẩu mới"
            value={formik.values.newPassword}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.touched.newPassword && formik.errors.newPassword && (
            <p className="mt-2 text-base italic text-red-500">{formik.errors.newPassword}</p>
          )}
        </div>

        <div>
          <label className="mb-4 block text-xl font-semibold">Xác nhận mật khẩu mới</label>
          <input
            type="password"
            name="confirmPassword"
            className="input w-full text-xl"
            placeholder="Xác nhận mật khẩu mới"
            value={formik.values.confirmPassword}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          {formik.touched.confirmPassword && formik.errors.confirmPassword && (
            <p className="mt-2 text-base italic text-red-500">{formik.errors.confirmPassword}</p>
          )}
        </div>

        <button
          type="submit"
          className="btn w-full py-5 text-xl font-semibold disabled:cursor-not-allowed disabled:opacity-50"
          disabled={formik.isSubmitting || success}
        >
          {formik.isSubmitting ? 'Đang cập nhật...' : 'Cập nhật mật khẩu'}
        </button>
      </form>
    </div>
  );
};

export default ChangePasswordModal;
