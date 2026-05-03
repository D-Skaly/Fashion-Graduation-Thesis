"use client";

import { Loader2, ShoppingBag, Heart } from "lucide-react";
import { Button } from "@/components/ui/button";
import { VirtualTryOnModal } from "@/components/product/VirtualTryOnModal";
import { Product } from "@/types/product";

interface ProductActionsProps {
  product: Product;
  selectedVariant: Product["variants"][0] | null;
  quantity: number;
  isPending: boolean;
  onAddToCart: () => void;
}

export function ProductActions({
  product,
  selectedVariant,
  quantity,
  isPending,
  onAddToCart,
}: ProductActionsProps) {
  const isOutOfStock = selectedVariant?.stockQuantity === 0;
  const isDisabled = !selectedVariant || isOutOfStock || isPending;

  return (
    <div className="flex flex-col gap-3">
      <VirtualTryOnModal
        productId={product.id}
        productName={product.name}
      />

      <Button
        size="lg"
        className="w-full text-sm font-semibold tracking-widest uppercase h-14 rounded-none bg-foreground text-background hover:bg-foreground/90 transition-all"
        disabled={isDisabled}
        onClick={onAddToCart}
      >
        {isPending ? (
          <Loader2 className="mr-2 h-5 w-5 animate-spin" />
        ) : (
          <ShoppingBag className="mr-2 h-5 w-5" />
        )}
        {isOutOfStock ? "Out of Stock" : "Add to Cart"}
      </Button>

      <Button
        variant="outline"
        className="w-full text-sm font-semibold tracking-widest uppercase h-14 rounded-none border-border hover:bg-secondary/20"
      >
        <Heart className="mr-2 h-4 w-4" /> Add to Wishlist
      </Button>
    </div>
  );
}