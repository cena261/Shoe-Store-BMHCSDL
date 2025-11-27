import { useState } from 'react';

import { motion } from 'framer-motion';
import Link from 'next/link';

import { authService } from '@/common/api/services/authService';

const slideAnimation = {
  from: {
    opacity: 0,
    transform: 'translateY(-50px)',
  },
  to: {
    opacity: 1,
    transform: 'translateY(0)',
    transition: {
      duration: 0.8,
      ease: [0.6, 0.01, -0.05, 0.9],
    },
  },
};

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const [emailFormError, setEmailFormError] = useState('');
  const [emailSuccess, setEmailSuccess] = useState('');
  const [emailSubmitting, setEmailSubmitting] = useState(false);

  const validateEmail = (email: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setEmailError('');
    setEmailFormError('');
    setEmailSuccess('');

    if (!email) {
      setEmailError('Bắt buộc');
      return;
    }

    if (!validateEmail(email)) {
      setEmailError('Email không hợp lệ');
      return;
    }

    setEmailSubmitting(true);

    try {
      await authService.forgotPassword({ email });
      setEmailSuccess(
        'Email reset password đã được gửi đến địa chỉ email của bạn.',
      );
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message || 'Có lỗi xảy ra. Vui lòng thử lại.';
      setEmailFormError(errorMessage);
    } finally {
      setEmailSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center">
      <motion.div
        variants={slideAnimation}
        initial="from"
        animate="to"
        className="-mt-24 w-full p-4 sm:mt-0 sm:p-7 2xl:-mt-36"
      >
        <h1 className="text-center text-4xl font-semibold leading-tight xl:text-6xl">
          Quên mật khẩu?
        </h1>
        <p className="mt-4 text-center text-gray-600">
          Điền email tạo tài khoản để đặt lại mật khẩu.
        </p>

        <div className="mt-4 w-full text-center">
          <Link href="/login">
            <a className="underline">Nhớ lại mật khẩu?</a>
          </Link>
        </div>

        <div className="mt-10">
          <div className="flex w-full justify-center">
            <form className="flex w-160 flex-col gap-1" onSubmit={handleEmailSubmit}>
              <label className="flex flex-col">
                <span className="text-lg font-semibold">Email</span>
                <input
                  type="email"
                  className="input"
                  placeholder="Enter your email..."
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={emailSubmitting}
                />
                {emailError && (
                  <div className="h-4 text-xs italic text-red-500">{emailError}</div>
                )}
              </label>

              {emailSuccess && (
                <div className="rounded-lg bg-green-50 p-4 text-center text-sm text-green-700">
                  <svg
                    className="mx-auto mb-2 h-8 w-8 text-green-500"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                    />
                  </svg>
                  {emailSuccess}
                </div>
              )}
              {emailFormError && (
                <div className="text-center text-xs italic text-red-500">{emailFormError}</div>
              )}

              <button
                type="submit"
                className="btn mt-1 h-10 rounded-md py-2 disabled:cursor-not-allowed disabled:opacity-50"
                disabled={emailSubmitting}
              >
                {emailSubmitting ? 'Đang gửi...' : 'Gửi'}
              </button>
            </form>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default ForgotPassword;
