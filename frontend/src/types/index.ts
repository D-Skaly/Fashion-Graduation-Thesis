// Centralized TypeScript Interfaces for the entire frontend

// ==================== PRODUCT TYPES ====================
export interface ProductVariant {
  id: string;
  size: string;
  color: string;
  stockQuantity: number;
  priceAdjustment: number;
  skuCode: string;
}

export interface ProductImage {
  id: string;
  url: string;
  alt: string;
  sortOrder: number;
  isPrimary: boolean;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  basePrice: number;
  categoryName: string;
  images?: ProductImage[];
  brand?: string;
  averageRating?: number;
  reviewCount?: number;
  variants: ProductVariant[];
  createdAt?: string;
  updatedAt?: string;
  tags?: string[];
  featured?: boolean;
  material?: string;
  careInstructions?: string;
  dimensions?: string;
  weight?: number;
  viewCount?: number;
  soldCount?: number;
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

// ==================== CART TYPES ====================
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

// ==================== ORDER TYPES ====================
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

// ==================== REVIEW TYPES ====================
export interface Review {
  id: string;
  productId: string;
  userId: string;
  rating: number;
  comment: string;
  images?: string[];
  createdAt: string;
  helpfulCount: number;
  userName?: string;
  userAvatar?: string;
  isVerifiedPurchase?: boolean;
}

// ==================== USER TYPES ====================
export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  role: string;
  avatar?: string;
  firstname?: string;
  lastname?: string;
}

// ==================== API RESPONSE ====================
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export interface ApiResponse<T> {
  status: string;
  message: string;
  data: T;
  timestamp: string;
}
