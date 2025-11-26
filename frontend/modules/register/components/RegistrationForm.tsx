import { useState } from 'react';

import { useFormik } from 'formik';
import { useRouter } from 'next/router';

import { authService } from '@/common/api/services/authService';
import LoaderButton from '@/common/components/button/components/LoaderButton';
import InputComponent from '@/common/components/input/components/InputComponent';
import InputPasswordComponent from '@/common/components/input/components/InputPasswordComponent';
import { useModal } from '@/common/recoil/modal';
import { useLogin } from '@/common/recoil/user';

import { ErrorEmail } from '../modals/ErrorModal';

const RegistrationForm = () => {
  const router = useRouter();
  const { openModal } = useModal();
  const { handleLogin } = useLogin();
  const [loading, setLoading] = useState(false);

  const formik = useFormik({
    initialValues: {
      fullName: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
    },
    onSubmit: async (values) => {
      setLoading(true);

      try {
        const response = await authService.register({
          email: values.email,
          password: values.password,
          fullName: values.fullName,
          phone: values.phone || undefined,
        });

        await handleLogin(response.user, response.token);
        router.push('/');
      } catch (error: any) {
        const errorMessage = error?.response?.data?.message || error.message;
        if (errorMessage.includes('already exists') || errorMessage.includes('existed')) {
          openModal(<ErrorEmail email={values.email} />);
        }
        setLoading(false);
      }
    },
    validate: (values) => {
      const errors: { [key: string]: string } = {};

      if (!values.fullName || values.fullName.length < 3) {
        errors.fullName = 'Full name must be at least 3 characters';
      }

      if (!values.email) {
        errors.email = 'Email is required';
      } else if (
        !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)
      ) {
        errors.email = 'Invalid email';
      }

      if (!values.password) {
        errors.password = 'Password is required';
      } else if (values.password.length < 8) {
        errors.password = 'Password must be at least 8 characters';
      }

      if (!values.confirmPassword) {
        errors.confirmPassword = 'Please confirm your password';
      } else if (values.password !== values.confirmPassword) {
        errors.confirmPassword = 'Passwords must match';
      }

      return errors;
    },
    validateOnBlur: true,
    validateOnChange: false,
  });

  return (
    <div className="flex w-full justify-center">
      <form
        className="flex w-160 flex-col gap-1"
        onSubmit={formik.handleSubmit}
      >
        <InputComponent
          label="Full Name"
          placeholder="Enter your full name..."
          value={formik.values.fullName}
          name="fullName"
          handleChange={formik.handleChange}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          className="border-none bg-white/25 text-zinc-100 placeholder:text-zinc-400"
        />
        <InputComponent
          label="Email"
          placeholder="Enter your email..."
          value={formik.values.email}
          name="email"
          handleChange={formik.handleChange}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          className="border-none bg-white/25 text-zinc-100 placeholder:text-zinc-400"
        />
        <InputComponent
          label="Phone (Optional)"
          placeholder="Enter your phone number..."
          value={formik.values.phone}
          name="phone"
          handleChange={formik.handleChange}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          className="border-none bg-white/25 text-zinc-100 placeholder:text-zinc-400"
        />
        <InputPasswordComponent
          label="Password"
          placeholder="Enter your password..."
          value={formik.values.password}
          name="password"
          handleChange={formik.handleChange}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          className="border-none bg-white/25 text-zinc-100 placeholder:text-zinc-400"
        />
        <InputPasswordComponent
          label="Confirm password"
          placeholder="Confirm your password..."
          value={formik.values.confirmPassword}
          name="confirmPassword"
          handleChange={formik.handleChange}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          className="border-none bg-white/25 text-zinc-100 placeholder:text-zinc-400"
        />

        <LoaderButton
          type="submit"
          loading={loading}
          className="mt-1 h-10 rounded-md bg-white py-0 font-semibold text-black disabled:cursor-not-allowed disabled:bg-white/75"
        >
          Register
        </LoaderButton>
      </form>
    </div>
  );
};

export default RegistrationForm;
