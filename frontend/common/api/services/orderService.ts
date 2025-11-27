import apiClient from '../client';
import type { Order, CreateOrderRequest } from '@/common/types/backend';

export const orderService = {
  createOrder: async (data: CreateOrderRequest): Promise<Order> => {
    return apiClient.post('/orders', data);
  },

  getMyOrders: async (): Promise<Order[]> => {
    return apiClient.get('/orders/my');
  },

  getOrderById: async (id: number): Promise<Order> => {
    return apiClient.get(`/orders/${id}`);
  },
};
