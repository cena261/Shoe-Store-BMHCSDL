import apiClient from '../client';
import type { CartItemResponse } from '@/common/types/backend';

export const cartService = {
  getCart: async (): Promise<CartItemResponse[]> => {
    return apiClient.get('/cart');
  },

  addItem: async (
    variantId: number,
    quantity: number
  ): Promise<CartItemResponse> => {
    return apiClient.post('/cart', { variantId, quantity });
  },

  updateItem: async (
    cartId: number,
    quantity: number
  ): Promise<CartItemResponse> => {
    return apiClient.put(`/cart/${cartId}`, { quantity });
  },

  removeItem: async (cartId: number): Promise<void> => {
    return apiClient.delete(`/cart/${cartId}`);
  },

  clearCart: async (): Promise<void> => {
    return apiClient.delete('/cart');
  },
};
