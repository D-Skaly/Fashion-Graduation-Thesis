"use client";

import { ShoppingBag, Truck, Tag, Calculator } from "lucide-react";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";

// Types matching Cart DTO
interface CartItem {
  id: string;
  productVariantId: string;
  productName: string;
  size: string;
  color: string;
  price: number;
  quantity: number;
  subtotal: number;
}

interface Cart {
  id: string;
  items: CartItem[];
  totalAmount: number;
}

interface OrderSummaryProps {
  cart: Cart | null | undefined;
  isLoading: boolean;
  shippingCost?: number;
  discountAmount?: number;
  discountCode?: string;
}

export function OrderSummary({
  cart,
  isLoading,
  shippingCost = 0,
  discountAmount = 0,
  discountCode,
}: OrderSummaryProps) {
  if (isLoading) {
    return (
      <div className="bg-card border rounded-lg p-6 space-y-4">
        <Skeleton className="h-6 w-32" />
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-12 w-full" />
          ))}
        </div>
        <Separator />
        <Skeleton className="h-8 w-full" />
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="bg-card border rounded-lg p-6 text-center">
        <ShoppingBag className="h-12 w-12 text-muted-foreground mx-auto mb-3" />
        <p className="text-muted-foreground">Your cart is empty</p>
      </div>
    );
  }

  const subtotal = cart.totalAmount;
  const total = subtotal + shippingCost - discountAmount;

  return (
    <div className="bg-card border rounded-lg p-6 space-y-6">
      <h3 className="text-lg font-semibold flex items-center gap-2">
        <ShoppingBag className="h-5 w-5" />
        Order Summary ({cart.items.length} items)
      </h3>

      {/* Items list */}
      <div className="space-y-3 max-h-60 overflow-y-auto">
        {cart.items.map((item) => (
          <div key={item.id} className="flex gap-3 text-sm">
            <div className="h-16 w-12 bg-secondary rounded flex-shrink-0" />
            <div className="flex-1 min-w-0">
              <p className="font-medium truncate">{item.productName}</p>
              <p className="text-muted-foreground text-xs">
                {item.size} / {item.color}
              </p>
              <p className="text-muted-foreground text-xs">
                x{item.quantity}
              </p>
            </div>
            <div className="text-right">
              <p className="font-medium">
                {new Intl.NumberFormat("en-US", {
                  style: "currency",
                  currency: "USD",
                }).format(item.subtotal)}
              </p>
            </div>
          </div>
        ))}
      </div>

      <Separator />

      {/* Cost breakdown */}
      <div className="space-y-3 text-sm">
        <div className="flex justify-between">
          <span className="text-muted-foreground flex items-center gap-2">
            <Calculator className="h-4 w-4" />
            Subtotal
          </span>
          <span>
            {new Intl.NumberFormat("en-US", {
              style: "currency",
              currency: "USD",
            }).format(subtotal)}
          </span>
        </div>

        <div className="flex justify-between">
          <span className="text-muted-foreground flex items-center gap-2">
            <Truck className="h-4 w-4" />
            Shipping
          </span>
          <span className={shippingCost === 0 ? "text-green-600 font-medium" : ""}>
            {shippingCost === 0
              ? "Free"
              : new Intl.NumberFormat("en-US", {
                  style: "currency",
                  currency: "USD",
                }).format(shippingCost)}
          </span>
        </div>

        {discountAmount > 0 && (
          <div className="flex justify-between text-green-600">
            <span className="flex items-center gap-2">
              <Tag className="h-4 w-4" />
              Discount {discountCode && `(${discountCode})`}
            </span>
            <span>
              -
              {new Intl.NumberFormat("en-US", {
                style: "currency",
                currency: "USD",
              }).format(discountAmount)}
            </span>
          </div>
        )}
      </div>

      <Separator />

      {/* Total */}
      <div className="flex justify-between items-center">
        <span className="text-lg font-semibold">Total</span>
        <span className="text-2xl font-bold">
          {new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
          }).format(total)}
        </span>
      </div>

      {/* Trust badges */}
      <div className="pt-4 space-y-2 text-xs text-muted-foreground">
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 rounded-full bg-green-100 flex items-center justify-center">
            <span className="text-green-600 text-[10px]">✓</span>
          </div>
          Free shipping on orders over $50
        </div>
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 rounded-full bg-green-100 flex items-center justify-center">
            <span className="text-green-600 text-[10px]">✓</span>
          </div>
          30-day return policy
        </div>
        <div className="flex items-center gap-2">
          <div className="h-4 w-4 rounded-full bg-green-100 flex items-center justify-center">
            <span className="text-green-600 text-[10px]">✓</span>
          </div>
          Secure checkout
        </div>
      </div>
    </div>
  );
}
