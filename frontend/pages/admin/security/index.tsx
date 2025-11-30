import { useEffect } from 'react';
import { useRouter } from 'next/router';

export default function SecurityIndex() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/admin/security/overview');
  }, [router]);

  return null;
}
