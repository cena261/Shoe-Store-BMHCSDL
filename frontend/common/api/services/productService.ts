import apiClient from '../client';
import type {
  BackendProduct,
  BackendCategory,
  ProductFilters,
  PaginatedResponse,
  ProductDetailResponse,
} from '@/common/types/backend';

export const productService = {
  getProducts: async (
    filters?: ProductFilters
  ): Promise<BackendProduct[]> => {
    const params: Record<string, string | number> = {};

    if (filters?.search) params.search = filters.search;
    if (filters?.gender) params.gender = filters.gender;
    if (filters?.sizes) params.sizes = filters.sizes;
    if (filters?.minPrice !== undefined) params.minPrice = filters.minPrice;
    if (filters?.maxPrice !== undefined) params.maxPrice = filters.maxPrice;
    if (filters?.sortBy) params.sortBy = filters.sortBy;
    if (filters?.page !== undefined) params.page = filters.page;
    if (filters?.pageSize !== undefined) params.pageSize = filters.pageSize;

    const response: PaginatedResponse<BackendProduct> = await apiClient.get(
      '/products',
      { params }
    );

    return response.products || [];
  },

  getProductDetail: async (
    slug: string,
    styleColor?: string
  ): Promise<ProductDetailResponse> => {
    const params: Record<string, string> = {};
    if (styleColor) params.styleColor = styleColor;

    return apiClient.get(`/products/${slug}`, { params });
  },

  getProductBySlug: async (slug: string): Promise<BackendProduct> => {
    return apiClient.get(`/products/${slug}`);
  },

  getCategories: async (): Promise<BackendCategory[]> => {
    return apiClient.get('/categories');
  },
};
