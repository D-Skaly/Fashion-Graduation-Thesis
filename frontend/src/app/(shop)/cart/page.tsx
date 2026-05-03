"use client";

import { useState } from "react";
import Image from "next/image";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, Minus, Plus, Trash2, ImageOff, Ticket, ArrowRight, Package } from "lucide-react";
import Link from "next/link";
import { toast } from "sonner";

import api from "@/lib/axios";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Separator } from "@/components/ui/separator";
import { Input } from "@/components/ui/input";

// Types matching Backend DTOs
type CartItem = {
  id: string;
  productVariantId: string;
  productName: string;
  size: string;
  color: string;
  price: number;
  quantity: number;
  subtotal: number;
  productImage?: string;
};

type Cart = {
  id: string;
  items: CartItem[];
  totalAmount: number;
};

const fetchCart = async (): Promise<Cart> => {
  const { data } = await api.get("/cart");
  return data;
};

function CartItemImage({ image, name }: { image?: string; name: string }) {
  const [error, setError] = useState(false);

  if (!image || error) {
    return (
      <div className="h-24 w-20 rounded-lg bg-secondary flex items-center justify-center flex-shrink-0">
        <ImageOff className="h-6 w-6 text-muted-foreground/30" />
      </div>
    );
  }

  return (
    <div className="h-24 w-20 rounded-lg overflow-hidden flex-shrink-0 relative bg-secondary">
      <Image
        src={image}
        alt={name}
        fill
        className="object-cover"
        sizes="80px"
        onError={() => setError(true)}
      />
    </div>
  );
}

export default function CartPage() {
  const queryClient = useQueryClient();
  const [couponCode, setCouponCode] = useState("");
  const [couponApplied, setCouponApplied] = useState(false);

  const { data: cart, isLoading, isError } = useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart,
    retry: false,
  });

  const updateMutation = useMutation({
    mutationFn: async ({ itemId, quantity }: { itemId: string; quantity: number }) => {
      await api.put("/cart/update", { cartItemId: itemId, quantity });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: () => {
      toast.error("Failed to update cart");
    },
  });

  const removeMutation = useMutation({
    mutationFn: async (itemId: string) => {
      await api.delete(`/cart/remove/${itemId}`);
    },
    onSuccess: () => {
      toast.success("Item removed");
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: () => {
      toast.error("Failed to remove item");
    },
  });

  const handleQuantityChange = (itemId: string, currentQty: number, delta: number) => {
    const newQty = currentQty + delta;
    if (newQty < 1) return;
    updateMutation.mutate({ itemId, quantity: newQty });
  };

  const handleApplyCoupon = () => {
    if (!couponCode.trim()) return;
    // Mock coupon logic - in real app, call API
    if (couponCode.toUpperCase() === "WELCOME10") {
      setCouponApplied(true);
      toast.success("Coupon applied! 10% off");
    } else {
      toast.error("Invalid coupon code");
    }
  };

  const discount = couponApplied ? (cart?.totalAmount || 0) * 0.1 : 0;
  const finalTotal = (cart?.totalAmount || 0) - discount;

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-16">
        <Skeleton className="h-10 w-64 mb-8" />
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="flex gap-4 p-4 border rounded-lg">
              <Skeleton className="h-24 w-20 rounded-lg" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-5 w-1/3" />
                <Skeleton className="h-4 w-1/4" />
                <Skeleton className="h-8 w-24" />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (isError || !cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-32 flex flex-col items-center justify-center text-center space-y-6">
        <div className="h-24 w-24 bg-secondary/30 rounded-full flex items-center justify-center">
          <Package className="h-10 w-10 text-muted-foreground" />
        </div>
        <h1 className="text-3xl font-bold">Your bag is empty</h1>
        <p className="text-muted-foreground max-w-md">
          Looks like you haven&apos;t added anything to your bag yet. Start browsing our collection to find something you love.
        </p>
        <Button asChild size="lg" className="mt-4 rounded-full px-8">
          <Link href="/shop">Start Shopping</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-16">
      <h1 className="text-3xl font-bold mb-8 tracking-tight">
        Shopping Bag ({cart.items.length})
      </h1>

      <div className="lg:grid lg:grid-cols-12 lg:gap-12">
        {/* Cart Items List */}
        <div className="lg:col-span-8">
          <div className="space-y-6">
            {cart.items.map((item) => (
              <div
                key={item.id}
                className="flex gap-4 p-4 border rounded-xl bg-card group transition-all hover:border-primary/20 hover:shadow-sm"
              >
                <CartItemImage image={item.productImage} name={item.productName} />

                <div className="flex-1 flex flex-col justify-between">
                  <div className="flex justify-between items-start">
                    <div className="flex-1 min-w-0">
                      <Link
                        href={`/product/${item.productVariantId}`}
                        className="font-semibold text-lg hover:text-primary transition-colors line-clamp-1"
                      >
                        {item.productName}
                      </Link>
                      <p className="text-sm text-muted-foreground">
                        Size: {item.size} | Color: {item.color}
                      </p>
                    </div>
                    <p className="font-bold text-lg ml-4">
                      {new Intl.NumberFormat("en-US", {
                        style: "currency",
                        currency: "USD",
                      }).format(item.price)}
                    </p>
                  </div>

                  <div className="flex justify-between items-center mt-4">
                    <div className="flex items-center gap-2 border rounded-lg p-1 bg-secondary/30">
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 rounded-md"
                        disabled={updateMutation.isPending || item.quantity <= 1}
                        onClick={() =>
                          handleQuantityChange(item.id, item.quantity, -1)
                        }
                      >
                        <Minus className="h-3 w-3" />
                      </Button>
                      <span className="text-sm w-6 text-center font-medium">
                        {updateMutation.isPending ? "..." : item.quantity}
                      </span>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 rounded-md"
                        disabled={updateMutation.isPending}
                        onClick={() =>
                          handleQuantityChange(item.id, item.quantity, 1)
                        }
                      >
                        <Plus className="h-3 w-3" />
                      </Button>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors"
                      onClick={() => removeMutation.mutate(item.id)}
                      disabled={removeMutation.isPending}
                    >
                      {removeMutation.isPending ? (
                        <Loader2 className="h-4 w-4 animate-spin" />
                      ) : (
                        <Trash2 className="h-4 w-4 mr-2" />
                      )}
                      Remove
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Order Summary */}
        <div className="lg:col-span-4 mt-8 lg:mt-0">
          <div className="bg-card border rounded-xl p-6 sticky top-24 shadow-sm space-y-6">
            <h2 className="text-xl font-bold">Order Summary</h2>

            {/* Coupon */}
            <div className="space-y-2">
              <label className="text-sm font-medium flex items-center gap-2">
                <Ticket className="h-4 w-4" /> Coupon Code
              </label>
              <div className="flex gap-2">
                <Input
                  placeholder="Enter code"
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value)}
                  className="h-10"
                  onKeyDown={(e) => e.key === "Enter" && handleApplyCoupon()}
                />
                <Button
                  variant="outline"
                  size="sm"
                  className="h-10 px-4"
                  onClick={handleApplyCoupon}
                >
                  Apply
                </Button>
              </div>
              {couponApplied && (
                <p className="text-xs text-green-600 dark:text-green-400 font-medium">
                  WELCOME10 applied — 10% off
                </p>
              )}
            </div>

            <Separator />

            <div className="space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Subtotal</span>
                <span>
                  {new Intl.NumberFormat("en-US", {
                    style: "currency",
                    currency: "USD",
                  }).format(cart.totalAmount)}
                </span>
              </div>
              {couponApplied && (
                <div className="flex justify-between text-green-600 dark:text-green-400">
                  <span>Discount (10%)</span>
                  <span>-{new Intl.NumberFormat("en-US", {
                    style: "currency",
                    currency: "USD",
                  }).format(discount)}</span>
                </div>
              )}
              <div className="flex justify-between">
                <span className="text-muted-foreground">Shipping</span>
                <span className="text-green-600 dark:text-green-400 font-medium">
                  Free
                </span>
              </div>
              <Separator />
              <div className="flex justify-between text-lg font-bold">
                <span>Total</span>
                <span>
                  {new Intl.NumberFormat("en-US", {
                    style: "currency",
                    currency: "USD",
                  }).format(finalTotal)}
                </span>
              </div>
            </div>

            <Button className="w-full h-14 text-base tracking-widest uppercase font-bold rounded-full group" size="lg" asChild>
              <Link href="/checkout">
                Proceed to Checkout
                <ArrowRight className="ml-2 h-5 w-5 transition-transform group-hover:translate-x-1" />
              </Link>
            </Button>

            <div className="flex items-center justify-center gap-4 text-xs text-muted-foreground">
              <span className="flex items-center gap-1">
                <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                  <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                </svg>
                Secure
              </span>
              <span className="flex items-center gap-1">
                <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                </svg>
                Protected
              </span>
              <span className="flex items-center gap-1">
                <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
                Verified
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
