export interface ApiResponse<T> {
  status: 'success' | 'error';
  code: string;
  message: string;
  data?: T;
}

export interface PaginatedResponse<T> {
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  products: T[];
}

export interface BackendProduct {
  productId: number;
  productName: string;
  slug: string;
  description: string;
  price: number;
  promotionPrice?: number | null;
  categoryId: number;
  categoryName?: string;
  rating?: number | null;
  isActive: number;
  createdAt: string;
  mainImageUrl?: string;
  colorVariants?: BackendColorVariant[];
  images?: BackendProductImage[];
}

export interface BackendColorVariant {
  colorName: string;
  mainImageUrl: string;
  styleColor: string;
  availableSizes?: string[];
}

export interface BackendProductImage {
  imageId: number;
  productId: number;
  imageUrl: string;
  displayOrder: number;
}

export interface BackendProductVariant {
  variantId: number;
  productId: number;
  sizeValue: string;
  colorName: string;
  sku: string;
  styleColor: string;
  stockQty: number;
  priceOverride?: number | null;
  isActive: number;
}

export interface BackendCategory {
  categoryId: number;
  categoryName: string;
  description?: string;
  imageUrl?: string;
}

export interface ProductFilters {
  search?: string;
  gender?: string;
  sizes?: string;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: string;
  page?: number;
  pageSize?: number;
}

export interface ProductDetailResponse {
  productId: number;
  productName: string;
  slug: string;
  categoryName: string;
  description: string;
  price: number;
  promotionPrice: number | null;
  displayPrice: number;
  rating: number | null;
  selectedStyleColor: string;
  selectedColorName: string;
  availableColors: ColorOption[];
  images: string[];
  availableSizes: SizeOption[];
}

export interface ColorOption {
  styleColor: string;
  colorName: string;
  thumbnailImage: string;
  isAvailable: boolean;
}

export interface SizeOption {
  variantId: number;
  sizeValue: string;
  stockQty: number;
  isAvailable: boolean;
  priceOverride: number | null;
}

export interface CartItemResponse {
  cartId: number;
  variantId: number;
  productName: string;
  slug: string;
  category: string;
  colorName: string;
  sizeValue: string;
  sku: string;
  imageUrl: string;
  price: number;
  promotionPrice: number | null;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  addedAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone?: string;
}

export interface UserResponse {
  userId: number;
  email: string;
  fullName: string;
  phone: string;
  createdAt: string;
  lastLogin: string | null;
  isActive: number;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}
