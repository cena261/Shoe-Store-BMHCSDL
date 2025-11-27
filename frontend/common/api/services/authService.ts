import apiClient from '../client';
import type {
  AuthResponse,
  ForgotPasswordRequest,
  ForgotPasswordResponse,
  LoginRequest,
  RegisterRequest,
  ResetPasswordRequest,
  ResetPasswordResponse,
  VerifyResetTokenRequest,
  VerifyResetTokenResponse,
} from '@/common/types/backend';

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    return apiClient.post('/auth/login', credentials);
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    return apiClient.post('/auth/register', data);
  },

  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    return apiClient.post('/auth/change-password', data);
  },

  forgotPassword: async (data: ForgotPasswordRequest): Promise<ForgotPasswordResponse> => {
    return apiClient.post('/auth/forgot-password', data);
  },

  verifyResetToken: async (data: VerifyResetTokenRequest): Promise<VerifyResetTokenResponse> => {
    return apiClient.post('/auth/verify-reset-token', data);
  },

  resetPassword: async (data: ResetPasswordRequest): Promise<ResetPasswordResponse> => {
    return apiClient.post('/auth/reset-password', data);
  },
};
