import api from "@/lib/axios";

// ==================== TYPES ====================

// Auth Types
export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: UserProfile;
}

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  role: string;
  avatar?: string;
}

// Product Types
export interface ProductVariant {
  id: string;
  size: string;
  color: string;
  stockQuantity: number;
  priceAdjustment: number;
  skuCode: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  basePrice: number;
  categoryName: string;
  images?: string[];
  brand?: string;
  averageRating?: number;
  reviewCount?: number;
  variants: ProductVariant[];
  createdAt?: string;
  updatedAt?: string;
  tags?: string[];
  featured?: boolean;
}

export interface ProductSummary {
  id: string;
  name: string;
  price: number;
  category: string;
  image?: string;
  hoverImage?: string;
  isNew?: boolean;
  isSale?: boolean;
  salePrice?: number;
  rating?: number;
  reviewCount?: number;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

// Cart Types
export interface CartItem {
  id: string;
  productVariantId: string;
  productName: string;
  size: string;
  color: string;
  price: number;
  quantity: number;
  subtotal: number;
  productImage?: string;
}

export interface Cart {
  id: string;
  items: CartItem[];
  totalAmount: number;
}

export interface AddToCartRequest {
  productVariantId: string;
  quantity: number;
}

// Order Types
export interface OrderItem {
  productVariantId: string;
  productName: string;
  size: string;
  color: string;
  price: number;
  quantity: number;
  subtotal: number;
}

export interface ShippingInfo {
  fullName: string;
  phone: string;
  address: string;
  note?: string;
}

export interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  totalAmount: number;
  status: OrderStatus;
  shippingInfo: ShippingInfo;
  createdAt: string;
  updatedAt: string;
}

export enum OrderStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  SHIPPED = "SHIPPED",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
}

// Review Types
export interface Review {
  id: string;
  productId: string;
  userId: string;
  rating: number;
  comment: string;
  images?: string[];
  createdAt: string;
  helpfulCount: number;
}

export interface CreateReviewRequest {
  productId: string;
  rating: number;
  comment: string;
  images?: string[];
}

// ==================== API SERVICES ====================

export const apiService = {
  // Auth
  auth: {
    register: (data: RegisterRequest) => 
      api.post<AuthResponse>("/auth/register", data),
    login: (data: LoginRequest) => 
      api.post<AuthResponse>("/auth/login", data),
    logout: () => 
      api.post("/auth/logout"),
    refreshToken: (refreshToken: string) => 
      api.post<AuthResponse>("/auth/refresh", { refreshToken }),
    getProfile: () => 
      api.get<UserProfile>("/auth/profile"),
    forgotPassword: (email: string) => 
      api.post("/auth/forgot-password", { email }),
    resetPassword: (token: string, newPassword: string) => 
      api.post("/auth/reset-password", { token, newPassword }),
    verifyEmail: (token: string) => 
      api.post("/auth/verify-email", { token }),
    resendVerification: (email: string) => 
      api.post("/auth/resend-verification", { email }),
  },

  // Products
  products: {
    list: (params?: { page?: number; size?: number; category?: string }) => 
      api.get<Page<Product>>("/products", { params }),
    getById: (id: string) => 
      api.get<Product>(`/products/${id}`),
    getFeatured: () => 
      api.get<Product[]>("/products/featured"),
    search: (query: string) => 
      api.get<Product[]>("/products/search", { params: { q: query } }),
  },

  // Cart
  cart: {
    get: () => 
      api.get<Cart>("/cart"),
    addItem: (data: AddToCartRequest) => 
      api.post<Cart>("/cart/add", data),
    updateItem: (cartItemId: string, quantity: number) => 
      api.put<Cart>("/cart/update", { cartItemId, quantity }),
    removeItem: (cartItemId: string) => 
      api.delete<Cart>(`/cart/remove/${cartItemId}`),
    clear: () => 
      api.delete("/cart/clear"),
    applyCoupon: (couponCode: string) => 
      api.post<Cart>("/cart/apply-coupon", { couponCode }),
  },

  // Orders
  orders: {
    place: (shippingInfo: ShippingInfo) => 
      api.post<Order>("/orders", { shippingInfo }),
    list: (params?: { page?: number; size?: number }) => 
      api.get<Page<Order>>("/orders", { params }),
    getById: (id: string) => 
      api.get<Order>(`/orders/${id}`),
    cancel: (id: string) => 
      api.post(`/orders/${id}/cancel`),
    getStatusHistory: (id: string) => 
      api.get(`/orders/${id}/status-history`),
  },

  // Reviews
  reviews: {
    listByProduct: (productId: string) => 
      api.get<Review[]>(`/reviews/product/${productId}`),
    create: (data: CreateReviewRequest) => 
      api.post<Review>("/reviews", data),
    markHelpful: (reviewId: string) => 
      api.post(`/reviews/${reviewId}/helpful`),
  },

  // User
  user: {
    getProfile: () => 
      api.get<UserProfile>("/users/profile"),
    updateProfile: (data: Partial<UserProfile>) => 
      api.put<UserProfile>("/users/profile", data),
    getAddresses: () => 
      api.get("/users/addresses"),
    addAddress: (data: any) => 
      api.post("/users/addresses", data),
  },

  // AI
  ai: {
    chat: (message: string, sessionId?: string) => 
      api.post("/ai/chat", { message, sessionId }),
    getTryOnResult: (jobId: string) => 
      api.get(`/tryon/result/${jobId}`),
    submitTryOn: (data: FormData) => 
      api.post("/tryon/submit", data, {
        headers: { "Content-Type": "multipart/form-data" },
      }),
  },

  // Wishlist
  wishlist: {
    get: () => 
      api.get<ProductSummary[]>("/wishlist"),
    add: (productId: string) => 
      api.post("/wishlist/add", { productId }),
    remove: (productId: string) => 
      api.delete(`/wishlist/remove/${productId}`),
  },
};
