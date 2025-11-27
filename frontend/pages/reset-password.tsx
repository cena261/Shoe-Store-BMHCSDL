import { NextPage } from 'next';

import { useNotLoggedIn } from '@/common/hooks/auth';
import ResetPassword from '@/modules/reset-password/components/ResetPassword';

const ResetPasswordPage: NextPage = () => {
  useNotLoggedIn();

  return <ResetPassword />;
};

export default ResetPasswordPage;
