"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Home, ShoppingBag, Heart, ShoppingCart, User } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/axios";

interface CartCount {
  count: number;
}

interface WishlistCount {
  count: number;
}

const fetchCartCount = async (): Promise<number> => {
  try {
    const { data } = await api.get("/cart");
    return data.items?.length || 0;
  } catch {
    return 0;
  }
};

const fetchWishlistCount = async (): Promise<number> => {
  try {
    const { data } = await api.get("/wishlist");
    return Array.isArray(data) ? data.length : 0;
  } catch {
    return 0;
  }
};

export function MobileBottomNav() {
  const pathname = usePathname();

  const { data: cartCount = 0 } = useQuery({
    queryKey: ["cart-count"],
    queryFn: fetchCartCount,
  });

  const { data: wishlistCount = 0 } = useQuery({
    queryKey: ["wishlist-count"],
    queryFn: fetchWishlistCount,
  });

  const navItems = [
    { href: "/", label: "Home", icon: Home },
    { href: "/shop", label: "Shop", icon: ShoppingBag },
    { href: "/account/wishlist", label: "Wishlist", icon: Heart, count: wishlistCount },
    { href: "/cart", label: "Cart", icon: ShoppingCart, count: cartCount },
    { href: "/account", label: "Account", icon: User },
  ];

  // Only show on mobile
  const isMobile = typeof window !== "undefined" && window.innerWidth < 768;
  if (!isMobile && typeof window !== "undefined") return null;

  return (
    <div className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-background border-t">
      <div className="flex items-center justify-around px-2 py-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href || 
            (item.href !== "/" && pathname.startsWith(item.href));

          return (
            <Link
              key={item.href}
              href={item.href}
              className={`relative flex flex-col items-center gap-0.5 py-2 px-3 rounded-lg transition-colors ${
                isActive
                  ? "text-primary"
                  : "text-muted-foreground hover:text-foreground"
              }`}
            >
              <div className="relative">
                <Icon className="h-5 w-5" />
                {item.count !== undefined && item.count > 0 && (
                  <span className="absolute -top-2 -right-2 h-4 min-w-[16px] px-1 flex items-center justify-center bg-primary text-primary-foreground text-[10px] font-bold rounded-full">
                    {item.count > 99 ? "99+" : item.count}
                  </span>
                )}
              </div>
              <span className="text-[10px] font-medium">{item.label}</span>
            </Link>
          );
        })}
      </div>
    </div>
  );
}

// Also export a wrapper that adds padding to the bottom of the page
export function WithMobileBottomNav({ children }: { children: React.ReactNode }) {
  return (
    <>
      {children}
      <MobileBottomNav />
      <div className="md:hidden h-16" /> {/* Spacer */}
    </>
  );
}
