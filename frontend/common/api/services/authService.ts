import apiClient from '../client';
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/common/types/backend';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    return apiClient.post('/auth/login', credentials);
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    return apiClient.post('/auth/register', data);
  },
};
