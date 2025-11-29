import { NextPage } from 'next';
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';

import { useLoggedIn } from '@/common/hooks/auth';
import userAtom from '@/common/recoil/user';
import Account from '@/modules/account/components/Account';

const AccountPage: NextPage = () => {
  useLoggedIn();
  const router = useRouter();
  const user = useRecoilValue(userAtom);

  useEffect(() => {
    if (user.roles?.includes('Admin')) {
      router.push('/admin');
    }
  }, [user, router]);

  if (user.roles?.includes('Admin')) {
    return null;
  }

  return <Account />;
};

export default AccountPage;
