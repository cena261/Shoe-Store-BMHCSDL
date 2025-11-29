import client from '../client';
import {
  UserAdminPageResponse,
  UserAdminResponse,
  UserAdminFilters,
  UserRolesResponse,
  UserSummaryResponse,
  RoleResponse,
  AssignRoleRequest,
} from '@/common/types/backend';

export const userAdminService = {
  getUsers: async (filters: UserAdminFilters = {}): Promise<UserAdminPageResponse> => {
    const params = new URLSearchParams();

    if (filters.search) params.append('search', filters.search);
    if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString());
    if (filters.page) params.append('page', filters.page.toString());
    if (filters.pageSize) params.append('pageSize', filters.pageSize.toString());

    return await client.get(`/admin/users?${params.toString()}`);
  },

  getUserById: async (id: number): Promise<UserAdminResponse> => {
    return await client.get(`/admin/users/${id}`);
  },

  setUserActive: async (id: number, active: boolean): Promise<void> => {
    await client.patch(`/admin/users/${id}/active?active=${active}`);
  },

  getUserRoles: async (userId: number): Promise<UserRolesResponse> => {
    return await client.get(`/admin/users/${userId}/roles`);
  },

  assignRole: async (userId: number, roleName: string): Promise<UserRolesResponse> => {
    const data: AssignRoleRequest = { roleName };
    return await client.post(`/admin/users/${userId}/roles`, data);
  },

  revokeRole: async (userId: number, roleName: string): Promise<UserRolesResponse> => {
    return await client.delete(`/admin/users/${userId}/roles/${roleName}`);
  },

  getAllRoles: async (): Promise<RoleResponse[]> => {
    return await client.get('/admin/roles');
  },

  getUsersByRole: async (roleName: string): Promise<UserSummaryResponse[]> => {
    return await client.get(`/admin/roles/${roleName}/users`);
  },
};
