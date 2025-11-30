import client from '../client';
import {
  OrderLabelSummaryResponse,
  OrderLabelResponse,
  UpdateOrderLabelRequest,
} from '@/common/types/backend';

export const labelAdminService = {
  getOrderLabelSummary: async (): Promise<OrderLabelSummaryResponse> => {
    return await client.get('/admin/orders/labels/summary');
  },

  getOrderLabel: async (orderId: number): Promise<OrderLabelResponse> => {
    return await client.get(`/admin/orders/${orderId}/label`);
  },

  updateOrderLabel: async (
    orderId: number,
    data: UpdateOrderLabelRequest
  ): Promise<OrderLabelResponse> => {
    return await client.put(`/admin/orders/${orderId}/label`, data);
  },
};
