import client from '../client';
import {
  SecurityContextResponse,
  SessionAuditResponse,
  DACInfoResponse,
  VPDDebugResponse,
} from '@/common/types/backend';

export const securityAdminService = {
  getCurrentSession: async (): Promise<SecurityContextResponse> => {
    return await client.get('/admin/security/current-session');
  },

  getSessionAuditLogs: async (
    limit: number = 100,
    userId?: number,
    username?: string
  ): Promise<SessionAuditResponse[]> => {
    const params = new URLSearchParams();
    params.append('limit', limit.toString());
    if (userId) params.append('userId', userId.toString());
    if (username) params.append('username', username);
    return await client.get(`/admin/security/sessions?${params.toString()}`);
  },

  getDACInfo: async (): Promise<DACInfoResponse> => {
    return await client.get('/admin/security/dac');
  },

  getVPDDebugInfo: async (): Promise<VPDDebugResponse> => {
    return await client.get('/admin/vpd/orders');
  },
};
