"use client";

import { useState, useMemo, useEffect } from "react";
import { useParams } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, Truck, ShieldCheck } from "lucide-react";
import { toast } from "sonner";
import { Separator } from "@/components/ui/separator";

import api from "@/lib/axios";
import { Product, ProductVariant } from "@/types/product";
import { queryKeys } from "@/lib/queryKeys";
import { ProductImageGallery } from "@/components/product/ProductImageGallery";
import { ProductInfo } from "@/components/product/ProductInfo";
import { ProductVariantSelector } from "@/components/product/ProductVariantSelector";
import { QuantitySelector } from "@/components/product/QuantitySelector";
import { ProductActions } from "@/components/product/ProductActions";
import { AiProductRecommendation } from "@/components/ai/AiProductRecommendation";

const fetchProduct = async (id: string): Promise<Product> => {
  const { data } = await api.get(`/products/${id}`);
  return data;
};

export default function ProductDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const queryClient = useQueryClient();

  const [selectedColor, setSelectedColor] = useState<string | null>(null);
  const [selectedSize, setSelectedSize] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

  // Compute effective selected values (with defaults)
  const effectiveSelectedColor = selectedColor ?? (uniqueColors.length > 0 ? uniqueColors[0] : null);
  const effectiveSelectedSize = selectedSize ?? (uniqueSizes.length > 0 ? uniqueSizes[0] : null);

  const { data: product, isLoading, isError } = useQuery({
    queryKey: queryKeys.products.detail(id),
    queryFn: () => fetchProduct(id),
  });

  // Derive Unique Colors and Sizes
  const { uniqueColors, uniqueSizes } = useMemo(() => {
    if (!product?.variants) return { uniqueColors: [], uniqueSizes: [] };

    const colors = Array.from(new Set(product.variants.map(v => v.color))).filter(Boolean);
    const sizes = Array.from(new Set(product.variants.map(v => v.size))).filter(Boolean);

    return { uniqueColors: colors as string[], uniqueSizes: sizes as string[] };
  }, [product]);

  // Find Selected Variant
  const selectedVariant = useMemo(() => {
    if (!product?.variants || !effectiveSelectedColor || !effectiveSelectedSize) return null;
    return product.variants.find(
      v => v.color === effectiveSelectedColor && v.size === effectiveSelectedSize
    ) as ProductVariant | null;
  }, [product, effectiveSelectedColor, effectiveSelectedSize]);

  // Add to Cart Mutation
  const addToCartMutation = useMutation({
    mutationFn: async () => {
      if (!selectedVariant) throw new Error("Please select a variant");

      await api.post("/cart/add", {
        productVariantId: selectedVariant.id,
        quantity: quantity,
      });
    },
    onSuccess: () => {
      toast.success("Added to cart", {
        description: `${quantity} x ${product?.name} (${selectedSize}, ${selectedColor})`,
      });
      queryClient.invalidateQueries({ queryKey: queryKeys.cart.all });
    },
    onError: (error: unknown) => {
      const axiosError = error as { response?: { data?: { message?: string } } };
      toast.error(axiosError.response?.data?.message || "Failed to add to cart");
    },
  });

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 grid md:grid-cols-2 gap-8">
        <div className="aspect-square w-full rounded-xl bg-secondary/20 animate-pulse" />
        <div className="space-y-4">
          <div className="h-10 w-3/4 bg-secondary/20 rounded animate-pulse" />
          <div className="h-6 w-1/4 bg-secondary/20 rounded animate-pulse" />
          <div className="h-32 w-full bg-secondary/20 rounded animate-pulse" />
        </div>
      </div>
    );
  }

  if (isError || !product) {
    return (
      <div className="container mx-auto px-4 py-32 text-center">
        Product not found
      </div>
    );
  }

  const currentPrice = product.basePrice + (selectedVariant?.priceAdjustment || 0);

  return (
    <div className="container mx-auto px-4 py-8 md:py-12">
      <div className="flex flex-col md:flex-row gap-12 lg:gap-20">
        {/* Gallery (Left Column) */}
        <div className="w-full md:w-3/5">
          <ProductImageGallery
            images={product.images}
            productName={product.name}
            selectedImageIndex={selectedImageIndex}
            onSelectImage={setSelectedImageIndex}
          />
        </div>

        {/* Product Info (Right Column) */}
        <div className="w-full md:w-2/5">
          <div className="sticky top-28 space-y-8">
            <ProductInfo
              categoryName={product.categoryName}
              name={product.name}
              price={currentPrice}
              description={product.description}
            />

            <Separator />

            {/* Selectors */}
            <div className="space-y-6">
              <ProductVariantSelector
                type="color"
                label="Color"
                selectedValue={effectiveSelectedColor}
                options={uniqueColors}
                onSelect={setSelectedColor}
              />

              <ProductVariantSelector
                type="size"
                label="Size"
                selectedValue={effectiveSelectedSize}
                options={uniqueSizes}
                onSelect={setSelectedSize}
                selectedSize={effectiveSelectedSize}
                stockQuantity={selectedVariant?.stockQuantity}
              />

              <QuantitySelector
                quantity={quantity}
                onQuantityChange={setQuantity}
              />
            </div>

            <Separator />

            {/* Actions */}
            <ProductActions
              product={product}
              selectedVariant={selectedVariant}
              quantity={quantity}
              isPending={addToCartMutation.isPending}
              onAddToCart={() => addToCartMutation.mutate()}
            />

            {/* Features */}
            <div className="space-y-3 text-sm mt-8 border border-border p-4 bg-secondary/10">
              <div className="flex items-center gap-3 text-foreground">
                <Truck className="h-4 w-4 text-muted-foreground" />
                <span className="font-light tracking-wide">
                  Free standard shipping on orders over $150
                </span>
              </div>
              <div className="flex items-center gap-3 text-foreground">
                <ShieldCheck className="h-4 w-4 text-muted-foreground" />
                <span className="font-light tracking-wide">
                  30-day free returns. Lifetime authenticity guarantee.
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* AI Complete The Look Section */}
      <AiProductRecommendation />
    </div>
  );
}