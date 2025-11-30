import client from '../client';
import {
  OrderExportSummary,
  ExportOrdersRequest,
  ExportOrdersResponse,
  OrderExportDownloadResponse,
} from '@/common/types/backend';

export const exportAdminService = {
  createExport: async (data: ExportOrdersRequest): Promise<ExportOrdersResponse> => {
    return await client.post('/admin/orders/export', data);
  },

  downloadExport: async (exportId: number): Promise<OrderExportDownloadResponse> => {
    return await client.get(`/admin/orders/export/${exportId}/download`);
  },

  getExportHistory: async (limit: number = 50, createdBy?: number): Promise<OrderExportSummary[]> => {
    const params = new URLSearchParams();
    params.append('limit', limit.toString());
    if (createdBy) params.append('createdBy', createdBy.toString());
    return await client.get(`/admin/orders/exports?${params.toString()}`);
  },
};
