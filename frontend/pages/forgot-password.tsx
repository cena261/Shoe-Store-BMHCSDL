import { NextPage } from 'next';

import { useNotLoggedIn } from '@/common/hooks/auth';
import ForgotPassword from '@/modules/forgot-password/components/ForgotPassword';

const ForgotPasswordPage: NextPage = () => {
  useNotLoggedIn();

  return <ForgotPassword />;
};

export default ForgotPasswordPage;
