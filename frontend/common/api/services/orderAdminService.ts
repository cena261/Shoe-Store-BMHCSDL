import client from '../client';
import {
  OrderAdminResponse,
  UpdateOrderStatusRequest,
} from '@/common/types/backend';

export const orderAdminService = {
  getAllOrders: async (): Promise<OrderAdminResponse[]> => {
    return await client.get('/orders/admin/orders');
  },

  getOrderById: async (id: number): Promise<OrderAdminResponse> => {
    return await client.get(`/orders/admin/orders/${id}`);
  },

  updateOrderStatus: async (id: number, status: string): Promise<OrderAdminResponse> => {
    const data: UpdateOrderStatusRequest = { orderStatus: status };
    return await client.put(`/orders/admin/orders/${id}/status`, data);
  },

  cancelOrder: async (id: number): Promise<void> => {
    await client.delete(`/orders/admin/orders/${id}`);
  },
};
