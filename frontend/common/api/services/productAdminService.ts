import client from '../client';
import {
  ProductAdminPageResponse,
  ProductAdminResponse,
  ProductAdminCreateRequest,
  ProductAdminUpdateRequest,
  ProductAdminFilters,
} from '@/common/types/backend';

export const productAdminService = {
  getProducts: async (filters: ProductAdminFilters = {}): Promise<ProductAdminPageResponse> => {
    const params = new URLSearchParams();

    if (filters.search) params.append('search', filters.search);
    if (filters.categoryId !== undefined) params.append('categoryId', filters.categoryId.toString());
    if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString());
    if (filters.page) params.append('page', filters.page.toString());
    if (filters.pageSize) params.append('pageSize', filters.pageSize.toString());

    return await client.get(`/admin/products?${params.toString()}`);
  },

  getProductById: async (id: number): Promise<ProductAdminResponse> => {
    return await client.get(`/admin/products/${id}`);
  },

  getProductBySlug: async (slug: string): Promise<ProductAdminResponse> => {
    return await client.get(`/admin/products/slug/${slug}`);
  },

  createProduct: async (data: ProductAdminCreateRequest): Promise<ProductAdminResponse> => {
    return await client.post('/admin/products', data);
  },

  updateProduct: async (
    id: number,
    data: ProductAdminUpdateRequest
  ): Promise<ProductAdminResponse> => {
    return await client.put(`/admin/products/${id}`, data);
  },

  setProductActive: async (id: number, active: boolean): Promise<void> => {
    await client.patch(`/admin/products/${id}/active?active=${active}`);
  },
};
