import apiClient from '../client';
import type { Address, CreateAddressRequest } from '@/common/types/backend';

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    return apiClient.get('/addresses');
  },

  createAddress: async (data: CreateAddressRequest): Promise<Address> => {
    return apiClient.post('/addresses', data);
  },

  updateAddress: async (id: number, data: CreateAddressRequest): Promise<Address> => {
    return apiClient.put(`/addresses/${id}`, data);
  },

  deleteAddress: async (id: number): Promise<void> => {
    return apiClient.delete(`/addresses/${id}`);
  },

  setDefaultAddress: async (id: number): Promise<Address> => {
    return apiClient.put(`/addresses/${id}/default`);
  },
};
