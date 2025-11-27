import { useState } from 'react';

import { useFormik } from 'formik';
import { useRouter } from 'next/router';

import { authService } from '@/common/api/services/authService';
import LoaderButton from '@/common/components/button/components/LoaderButton';
import InputComponent from '@/common/components/input/components/InputComponent';
import InputPasswordComponent from '@/common/components/input/components/InputPasswordComponent';
import { useModal } from '@/common/recoil/modal';
import { useLogin } from '@/common/recoil/user';

import IncorrectCredentials from '../modals/IncorrectCredentials';

const LoginForm = () => {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const { handleLogin } = useLogin();
  const { openModal } = useModal();

  const formik = useFormik({
    initialValues: {
      email: '',
      password: '',
    },
    onSubmit: async (values) => {
      setLoading(true);

      try {
        const response = await authService.login(values);
        await handleLogin(response.user, response.token);
        router.push('/');
      } catch (error: any) {
        openModal(<IncorrectCredentials />);
        setLoading(false);
      }
    },
    validate: (values) => {
      const errors: { [key: string]: string } = {};

      if (!values.email) {
        errors.email = 'Required';
      }

      if (
        values.email &&
        !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)
      ) {
        errors.email = 'Invalid email';
      }

      if (!values.password) {
        errors.password = 'Required';
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
          label="Email"
          placeholder="Enter your email..."
          name="email"
          handleChange={formik.handleChange}
          value={formik.values.email}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
        />
        <InputPasswordComponent
          label="Password"
          placeholder="Enter your password..."
          name="password"
          handleChange={formik.handleChange}
          value={formik.values.password}
          errors={formik.errors}
          handleBlur={formik.handleBlur}
          rightLink={{
            href: '/forgot-password',
            text: 'Forgot password?',
          }}
        />

        <LoaderButton
          className="mt-1 rounded-md py-2"
          type="submit"
          loading={loading}
        >
          Login
        </LoaderButton>
      </form>
    </div>
  );
};

export default LoginForm;
