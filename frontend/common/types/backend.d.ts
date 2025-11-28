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

export interface Address {
  addressId: number;
  fullName: string;
  phone: string;
  tenDuong: string;
  xaQuan: string;
  tinhThanh: string;
  isDefault: boolean;
}

export interface CreateAddressRequest {
  fullName: string;
  phone: string;
  tenDuong: string;
  xaQuan: string;
  tinhThanh: string;
  isDefault: boolean;
}

export interface Order {
  orderId: number;
  orderDate: string;
  orderStatus: string;
  totalAmount: number;
  shippingName: string;
  shippingPhone: string;
  shippingTenDuong: string;
  shippingXaQuan: string;
  shippingTinhThanh: string;
  orderItems: OrderItem[];
}

export interface OrderItem {
  orderItemId: number;
  variantId: number;
  productName: string;
  colorName: string;
  sizeValue: string;
  sku: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface CreateOrderRequest {
  addressId: number;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ForgotPasswordResponse {
  email: string;
  codeLength: number;
  expiresInMinutes: number;
}

export interface VerifyResetTokenRequest {
  email: string;
  code: string;
}

export interface VerifyResetTokenResponse {
  valid: boolean;
  message: string;
}

export interface ResetPasswordRequest {
  email: string;
  code: string;
  newPassword: string;
}

export interface ResetPasswordResponse {
  email: string;
  passwordChanged: boolean;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ProductAdminResponse {
  productId: number;
  productName: string;
  slug: string;
  categoryId: number;
  categoryName: string;
  description: string;
  price: number;
  promotionPrice?: number | null;
  rating?: number | null;
  isActive: number;
  createdAt: string;
  variants: ProductVariantAdminResponse[];
  images: ProductImageAdminResponse[];
}

export interface ProductVariantAdminResponse {
  variantId: number;
  sizeValue: string;
  colorName: string;
  sku: string;
  styleColor: string;
  stockQty: number;
  priceOverride?: number | null;
  isActive: number;
}

export interface ProductImageAdminResponse {
  imageId: number;
  imageUrl: string;
  displayOrder: number;
  styleColor?: string;
}

export interface ProductAdminPageResponse {
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  products: ProductAdminResponse[];
}

export interface ProductAdminCreateRequest {
  productName: string;
  slug: string;
  categoryId: number;
  description?: string;
  price: number;
  promotionPrice?: number | null;
  variants: ProductVariantAdminRequest[];
  images: ProductImageAdminRequest[];
}

export interface ProductAdminUpdateRequest {
  productName?: string;
  slug?: string;
  categoryId?: number;
  description?: string;
  price?: number;
  promotionPrice?: number | null;
  variants?: ProductVariantAdminRequest[];
  images?: ProductImageAdminRequest[];
}

export interface ProductVariantAdminRequest {
  variantId?: number | null;
  sizeValue: string;
  colorName: string;
  sku: string;
  styleColor: string;
  stockQty: number;
  priceOverride?: number | null;
  isActive: number;
}

export interface ProductImageAdminRequest {
  imageId?: number | null;
  imageUrl: string;
  displayOrder: number;
  styleColor?: string;
}

export interface ProductAdminFilters {
  search?: string;
  categoryId?: number;
  isActive?: number;
  page?: number;
  pageSize?: number;
}
