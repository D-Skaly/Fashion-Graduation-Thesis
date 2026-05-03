// Centralized query key factory for TanStack React Query
// Follows the pattern from: https://tanstack.com/query/latest/docs/framework/react/guides/query-keys

export const queryKeys = {
  // Products
  products: {
    all: ['products'] as const,
    lists: () => ['products', 'list'] as const,
    list: (filters: Record<string, unknown>) => ['products', 'list', { filters }] as const,
    details: () => ['products', 'detail'] as const,
    detail: (id: string) => ['products', 'detail', id] as const,
  },
  
  // Cart
  cart: {
    all: ['cart'] as const,
    detail: () => ['cart', 'detail'] as const,
  },
  
  // Orders
  orders: {
    all: ['orders'] as const,
    lists: () => ['orders', 'list'] as const,
    list: (filters: Record<string, unknown>) => ['orders', 'list', { filters }] as const,
    details: () => ['orders', 'detail'] as const,
    detail: (id: string) => ['orders', 'detail', id] as const,
  },
  
  // User
  user: {
    all: ['user'] as const,
    profile: () => ['user', 'profile'] as const,
    orders: () => ['user', 'orders'] as const,
  },
  
  // Wishlist
  wishlist: {
    all: ['wishlist'] as const,
    items: () => ['wishlist', 'items'] as const,
  },
  
  // Categories
  categories: {
    all: ['categories'] as const,
    tree: () => ['categories', 'tree'] as const,
  },
  
  // Coupons
  coupons: {
    all: ['coupons'] as const,
    lists: () => ['coupons', 'list'] as const,
    list: (filters: Record<string, unknown>) => ['coupons', 'list', { filters }] as const,
  },
  
  // Admin
  admin: {
    stats: () => ['admin', 'stats'] as const,
    revenue: (period: string) => ['admin', 'revenue', period] as const,
    recentOrders: () => ['admin', 'recentOrders'] as const,
    popularProducts: () => ['admin', 'popularProducts'] as const,
  },
} as const;

// Helper type for query keys (useful for TypeScript inference)
export type QueryKeys = typeof queryKeys;