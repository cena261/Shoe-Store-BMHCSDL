import client from '../client';
import { AuditLogResponse, FGAEventResponse } from '@/common/types/backend';

export const auditAdminService = {
  getAllLogs: async (limit: number = 100): Promise<AuditLogResponse[]> => {
    return await client.get(`/admin/audit/logs?limit=${limit}`);
  },

  getLogsByObject: async (objectName: string, limit: number = 100): Promise<AuditLogResponse[]> => {
    return await client.get(`/admin/audit/logs/object/${objectName}?limit=${limit}`);
  },

  getLogsByOrder: async (orderId: number, limit: number = 50): Promise<AuditLogResponse[]> => {
    return await client.get(`/admin/audit/logs/order/${orderId}?limit=${limit}`);
  },

  getLogsByUser: async (username: string, limit: number = 100): Promise<AuditLogResponse[]> => {
    return await client.get(`/admin/audit/logs/user/${username}?limit=${limit}`);
  },

  searchLogs: async (params: {
    objectName?: string;
    username?: string;
    action?: string;
    limit?: number;
  }): Promise<AuditLogResponse[]> => {
    const queryParams = new URLSearchParams();
    if (params.objectName) queryParams.append('objectName', params.objectName);
    if (params.username) queryParams.append('username', params.username);
    if (params.action) queryParams.append('action', params.action);
    if (params.limit) queryParams.append('limit', params.limit.toString());
    return await client.get(`/admin/audit/logs/search?${queryParams.toString()}`);
  },

  getFGAUsers: async (limit: number = 50): Promise<FGAEventResponse[]> => {
    return await client.get(`/admin/audit/fga/users?limit=${limit}`);
  },

  getFGAByPolicy: async (policyName: string, limit: number = 50): Promise<FGAEventResponse[]> => {
    return await client.get(`/admin/audit/fga/policy/${policyName}?limit=${limit}`);
  },

  getAllFGA: async (limit: number = 100): Promise<FGAEventResponse[]> => {
    return await client.get(`/admin/audit/fga/all?limit=${limit}`);
  },
};
