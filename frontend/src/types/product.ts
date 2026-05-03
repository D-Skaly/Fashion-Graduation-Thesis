// Centralized product-related type definitions
// Used across components, hooks, and pages

export type ProductVariant = {
  id: string;
  size: string;
  color: string;
  stockQuantity: number;
  priceAdjustment: number;
  skuCode: string;
};

export type Product = {
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
};

export type ProductResponse = {
  id: string;
  name: string;
  basePrice: number;
  description: string;
  categoryName: string;
  images?: string[];
  brand?: string;
  tags?: string[];
  featured?: boolean;
  variants?: ProductVariantResponse[];
  averageRating?: number;
  reviewCount?: number;
  createdAt?: string;
  updatedAt?: string;
};

export type ProductVariantResponse = {
  id: string;
  size: string;
  color: string;
  stockQuantity: number;
  priceAdjustment: number;
  skuCode: string;
};

export type Page<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
};

// Simplified product for display (e.g., product cards)
export type ProductSummary = {
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
};
