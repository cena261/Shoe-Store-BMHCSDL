import { useSetRecoilState } from 'recoil';

import { userAtom } from './user.atom';
import { cartAtom } from '../cart/cart.atom';
import { cartService } from '@/common/api/services/cartService';
import { mapBackendCartToFrontend } from '@/common/lib/mapBackendCart';
import type { UserResponse } from '@/common/types/backend';

export const useLogin = () => {
  const setUser = useSetRecoilState(userAtom);
  const setCart = useSetRecoilState(cartAtom);

  const handleLogin = async (
    user: UserResponse,
    jwt: string
  ) => {
    const userForStorage = {
      id: user.userId.toString(),
      email: user.email,
      username: user.fullName,
    };

    localStorage.setItem('user', JSON.stringify(userForStorage));
    localStorage.setItem('jwt', jwt);
    setUser(userForStorage);

    try {
      console.log('Fetching backend cart...');
      const backendCartItems = await cartService.getCart();
      console.log('Backend cart items:', backendCartItems);

      const frontendCartProducts = mapBackendCartToFrontend(backendCartItems);
      console.log('Mapped frontend products:', frontendCartProducts);

      const cartData = {
        opened: false,
        id: '',
        attributes: {
          products: frontendCartProducts,
        },
      };

      setCart(cartData);
      localStorage.setItem('cart', JSON.stringify(cartData));
      console.log('Cart set successfully');
    } catch (error) {
      console.error('Failed to load cart from backend:', error);
      const emptyCart = {
        opened: false,
        id: '',
        attributes: {
          products: [],
        },
      };
      setCart(emptyCart);
      localStorage.setItem('cart', JSON.stringify(emptyCart));
    }
  };

  return { handleLogin };
};

export const useLogout = () => {
  const setUser = useSetRecoilState(userAtom);
  const setCart = useSetRecoilState(cartAtom);

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('jwt');
    localStorage.removeItem('cart');
    setUser({ username: '', email: '', id: '' });
    setCart({
      opened: false,
      id: '',
      attributes: {
        products: [],
      },
    });
  };

  return { handleLogout };
};
