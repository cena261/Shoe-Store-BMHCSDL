import { useRecoilValue, useSetRecoilState } from 'recoil';

import { cartAtom } from './cart.atom';
import { userAtom } from '../user/user.atom';
import { cartService } from '@/common/api/services/cartService';

export const useToggleCart = (alwaysFalse = false) => {
  const setCart = useSetRecoilState(cartAtom);

  return () => {
    setCart((prevState) => ({
      ...prevState,
      opened: alwaysFalse ? false : !prevState.opened,
    }));
  };
};

export const useAddToCart = () => {
  const setCart = useSetRecoilState(cartAtom);
  const user = useRecoilValue(userAtom);
  const isAuthenticated = !!user.id;

  return async (product: SimpleProduct, size: number, variantId?: number) => {
    if (isAuthenticated && variantId) {
      try {
        await cartService.addItem(variantId, 1);

        setCart((prev) => {
          if (
            prev.attributes.products.some(
              (p) => p.id === product.id && p.size === size
            )
          ) {
            return {
              ...prev,
              opened: true,
              attributes: {
                products: prev.attributes.products.map((p) =>
                  product.id === p.id && p.size === size
                    ? { ...p, quantity: p.quantity + 1 }
                    : p
                ),
              },
            };
          }

          return {
            ...prev,
            opened: true,
            attributes: {
              products: [
                ...prev.attributes.products,
                { ...product, quantity: 1, size, cartId: undefined },
              ],
            },
          };
        });
      } catch (error) {
        console.error('Failed to add item to backend cart:', error);
      }
    } else {
      setCart((prev) => {
        if (
          prev.attributes.products.some(
            (p) => p.id === product.id && p.size === size
          )
        ) {
          return {
            ...prev,
            opened: true,
            attributes: {
              products: prev.attributes.products.map((p) =>
                product.id === p.id && p.size === size
                  ? { ...p, quantity: p.quantity + 1 }
                  : p
              ),
            },
          };
        }

        return {
          ...prev,
          opened: true,
          attributes: {
            products: [
              ...prev.attributes.products,
              { ...product, quantity: 1, size },
            ],
          },
        };
      });
    }
  };
};

export const useRemoveFromCart = () => {
  const setCart = useSetRecoilState(cartAtom);
  const user = useRecoilValue(userAtom);
  const isAuthenticated = !!user.id;

  return async (id: string, size: number, cartId?: number) => {
    if (isAuthenticated && cartId) {
      try {
        await cartService.removeItem(cartId);

        setCart((prev) => ({
          ...prev,
          attributes: {
            products: prev.attributes.products.filter((product) => {
              if (product.id === id && product.size === size) return false;
              return true;
            }),
          },
        }));
      } catch (error) {
        console.error('Failed to remove item from backend cart:', error);
      }
    } else {
      setCart((prev) => ({
        ...prev,
        attributes: {
          products: prev.attributes.products.filter((product) => {
            if (product.id === id && product.size === size) return false;
            return true;
          }),
        },
      }));
    }
  };
};

export const useUpdateQuantity = () => {
  const setCart = useSetRecoilState(cartAtom);
  const user = useRecoilValue(userAtom);
  const isAuthenticated = !!user.id;

  return async (id: string, size: number, quantity: number, cartId?: number) => {
    if (isAuthenticated && cartId) {
      try {
        await cartService.updateItem(cartId, quantity);

        setCart((prev) => ({
          ...prev,
          attributes: {
            products: prev.attributes.products.map((product) =>
              product.id === id && product.size === size
                ? { ...product, quantity }
                : product
            ),
          },
        }));
      } catch (error) {
        console.error('Failed to update item in backend cart:', error);
      }
    } else {
      setCart((prev) => ({
        ...prev,
        attributes: {
          products: prev.attributes.products.map((product) =>
            product.id === id && product.size === size
              ? { ...product, quantity }
              : product
          ),
        },
      }));
    }
  };
};

export const useClearCart = () => {
  const setCart = useSetRecoilState(cartAtom);
  const user = useRecoilValue(userAtom);
  const isAuthenticated = !!user.id;

  return async () => {
    if (isAuthenticated) {
      try {
        await cartService.clearCart();

        setCart((prev) => ({
          ...prev,
          attributes: {
            products: [],
          },
        }));
      } catch (error) {
        console.error('Failed to clear backend cart:', error);
      }
    } else {
      setCart((prev) => ({
        ...prev,
        attributes: {
          products: [],
        },
      }));
    }
  };
};
