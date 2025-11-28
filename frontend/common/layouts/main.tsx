import { useEffect, useRef } from 'react';

import { ApolloProvider } from '@apollo/client';
import { useRouter } from 'next/router';
import { RecoilRoot, useRecoilValue } from 'recoil';

import Cart from '../components/cart/components/Cart';
import Footer from '../components/footer/components/Footer';
import ModalManager from '../components/modal/components/ModalManager';
import NavBar from '../components/nav/components/NavBar';
import ToastContainer from '../components/toast/ToastContainer';
import { client } from '../graphql/client';
import { toastState } from '../recoil/toast/toast.atom';
import { useToast } from '../recoil/toast/toast.hooks';

const MainLayout = ({ children }: { children: JSX.Element }) => {
  const router = useRouter();

  const lastScrollY = useRef(0);
  const lastPathName = useRef('');

  useEffect(() => {
    const node = document.getElementById('__next');
    if (node) {
      node.classList.add('on-scrollbar');
      if (router.pathname === '/shoes' && lastPathName.current === '/[slug]')
        node.scrollTop = lastScrollY.current;
      else node.scrollTop = 0;
    }

    return () => {
      if (node) {
        lastPathName.current = router.pathname;
        if (router.pathname === '/shoes') lastScrollY.current = node.scrollTop;
      }
    };
  }, [router]);

  return (
    <RecoilRoot>
      <ApolloProvider client={client}>
        <ToastProvider />
        <ModalManager />
        <NavBar onHomePage={router.pathname === '/'} />
        <Cart />

        {router.pathname !== '/' && (
          <>
            <div className="min-h-full px-3 sm:px-6 xl:px-12 2xl:px-24">
              {children}
            </div>
            <Footer />
          </>
        )}

        {router.pathname === '/' && children}
      </ApolloProvider>
    </RecoilRoot>
  );
};

const ToastProvider = () => {
  const toasts = useRecoilValue(toastState);
  const { removeToast } = useToast();

  return <ToastContainer toasts={toasts} removeToast={removeToast} />;
};

export default MainLayout;
