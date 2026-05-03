"use client";

import { useState, useMemo, useEffect } from "react";
import { useParams } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Truck, ShieldCheck, Heart, Share2 } from "lucide-react";
import { toast } from "sonner";

import api from "@/lib/axios";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { ImageGallery } from "./ImageGallery";
import { SizeSelector } from "./SizeSelector";
import { ColorSelector } from "./ColorSelector";
import { QuantitySelector } from "./QuantitySelector";
import { AddToCartButton } from "./AddToCartButton";
import { ReviewList } from "./ReviewList";
import { VirtualTryOn } from "@/components/ai/VirtualTryOn";
import { cn } from "@/lib/utils";

// Types
type Variant = {
    id: string;
    size: string;
    color: string;
    stockQuantity: number;
    priceAdjustment: number;
    skuCode: string;
}

type Product = {
    id: string;
    name: string;
    description: string;
    basePrice: number;
    categoryName: string;
    sku: string;
    slug: string;
    isActive: boolean;
    isFeatured: boolean;
    tags: string[];
    brand: string;
    material: string;
    careInstructions: string;
    weight: number;
    dimensions: string;
    viewCount: number;
    soldCount: number;
    ratingAvg: number;
    metaTitle: string;
    metaDescription: string;
    variants: Variant[];
    images?: Array<{
        id: string;
        url: string;
        alt: string;
        sortOrder: number;
        isPrimary: boolean;
    }>;
    reviews?: Array<{
        id: string;
        userId: string;
        userName: string;
        rating: number;
        comment: string;
        images?: string[];
        isVerifiedPurchase: boolean;
        isHelpful: number;
        createdAt: string;
    }>;
}

const fetchProduct = async (id: string): Promise<Product> => {
    const { data } = await api.get(`/products/${id}`);
    return data;
};

export function ProductPage() {
    const params = useParams();
    const id = params.id as string;
    const queryClient = useQueryClient();

    const [selectedColor, setSelectedColor] = useState<string | null>(null);
    const [selectedSize, setSelectedSize] = useState<string | null>(null);
    const [quantity, setQuantity] = useState(1);
    const [isAdded, setIsAdded] = useState(false);

    // Compute effective selected values (with defaults)
    const effectiveSelectedColor = selectedColor ?? (uniqueColors.length > 0 ? uniqueColors[0] : null);
    const effectiveSelectedSize = selectedSize ?? (uniqueSizes.length > 0 ? uniqueSizes[0] : null);

    const { data: product, isLoading, isError } = useQuery({
        queryKey: ["product", id],
        queryFn: () => fetchProduct(id),
    });

    // Increment view count
    useEffect(() => {
        if (product?.id) {
            api.post(`/products/${product.id}/view`).catch(console.error);
        }
    }, [product]);

    // Derive Unique Colors and Sizes
    const { uniqueColors, uniqueSizes } = useMemo(() => {
        if (!product?.variants) return { uniqueColors: [], uniqueSizes: [] };
        
        const colors = Array.from(new Set(product.variants.map(v => v.color))).filter(Boolean);
        const sizes = Array.from(new Set(product.variants.map(v => v.size))).filter(Boolean);
        
        return { uniqueColors: colors, uniqueSizes: sizes };
    }, [product]);

    // Stock Map for Sizes
    const stockMap = useMemo(() => {
        if (!product?.variants || !effectiveSelectedColor) return {};
        
        const map: Record<string, number> = {};
        product.variants
            .filter(v => v.color === effectiveSelectedColor)
            .forEach(v => {
                map[v.size] = v.stockQuantity;
            });
        
        return map;
    }, [product, effectiveSelectedColor]);

    // Find Selected Variant
    const selectedVariant = useMemo(() => {
        if (!product?.variants || !effectiveSelectedColor || !effectiveSelectedSize) return null;
        return product.variants.find(v => v.color === effectiveSelectedColor && v.size === effectiveSelectedSize);
    }, [product, effectiveSelectedColor, effectiveSelectedSize]);

    // Add to Cart Mutation
    const addToCartMutation = useMutation({
        mutationFn: async () => {
            if (!selectedVariant) throw new Error("Please select a variant");
            
            await api.post("/cart/add", {
                productVariantId: selectedVariant.id,
                quantity: quantity
            });
        },
        onSuccess: () => {
            toast.success("Added to cart", {
                description: `${quantity} x ${product?.name} (${selectedSize}, ${selectedColor})`
            });
            setIsAdded(true);
            queryClient.invalidateQueries({ queryKey: ["cart"] });
            
            // Reset success state after 2 seconds
            setTimeout(() => setIsAdded(false), 2000);
        },
        onError: (error: unknown) => {
            const axiosError = error as { response?: { data?: { message?: string } } };
            toast.error(axiosError.response?.data?.message || "Failed to add to cart");
        }
    });

    if (isLoading) {
        return (
            <div className="container mx-auto px-4 py-8 grid md:grid-cols-2 gap-8">
                <Skeleton className="aspect-square w-full rounded-xl" />
                <div className="space-y-4">
                    <Skeleton className="h-10 w-3/4" />
                    <Skeleton className="h-6 w-1/4" />
                    <Skeleton className="h-32 w-full" />
                </div>
            </div>
        );
    }

    if (isError || !product) {
        return <div className="container mx-auto px-4 py-32 text-center border rounded-xl bg-muted/20">Product not found</div>;
    }

    const currentPrice = product.basePrice + (selectedVariant?.priceAdjustment || 0);
    const averageRating = product.ratingAvg || 0;
    const totalReviews = product.reviews?.length || 0;

    return (
        <div className="container mx-auto px-4 py-10 md:py-16">
            <div className="grid md:grid-cols-12 gap-12 lg:gap-16">
                
                {/* Gallery */}
                <div className="md:col-span-7 lg:col-span-8">
                    <ImageGallery 
                        images={product.images || []} 
                        productName={product.name}
                    />
                </div>

                {/* Product Info */}
                <div className="md:col-span-5 lg:col-span-4 space-y-8 sticky top-24 self-start">
                    <div>
                        <div className="flex flex-wrap gap-2 mb-4">
                            <Badge variant="secondary" className="bg-secondary/50 hover:bg-secondary text-xs uppercase tracking-wider">{product.categoryName}</Badge>
                            {product.isFeatured && (
                                <Badge className="bg-amber-500/10 text-amber-600 hover:bg-amber-500/20 border-0 text-xs uppercase tracking-wider">Featured</Badge>
                            )}
                        </div>
                        <h1 className="text-3xl md:text-4xl lg:text-5xl font-black tracking-tighter mb-4 leading-tight">{product.name}</h1>
                        <div className="flex items-end gap-4 mb-2">
                            <span className="text-3xl font-light tracking-tight">
                                {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(currentPrice)}
                            </span>
                        </div>
                        <div className="flex items-center gap-3 text-sm text-muted-foreground mt-2">
                            {product.soldCount > 0 && (
                                <span className="flex items-center">
                                    🔥 <span className="ml-1 font-medium text-foreground">{product.soldCount}</span> sold
                                </span>
                            )}
                            {product.brand && (
                                <span>Brand: <span className="font-medium text-foreground">{product.brand}</span></span>
                            )}
                        </div>
                    </div>

                    <p className="text-muted-foreground leading-relaxed font-light text-sm md:text-base">
                        {product.description}
                    </p>

                    <Separator className="bg-border/50" />

                    {/* Selectors */}
                    <div className="space-y-6">
                        {/* Color */}
                        {uniqueColors.length > 0 && (
                            <div className="space-y-3">
                                <ColorSelector
                                    colors={uniqueColors}
                                    selectedColor={effectiveSelectedColor}
                                    onSelectColor={setSelectedColor}
                                />
                            </div>
                        )}

                        {/* Size */}
                        {uniqueSizes.length > 0 && (
                            <div className="space-y-3">
                                <SizeSelector
                                    sizes={uniqueSizes}
                                    selectedSize={effectiveSelectedSize}
                                    onSelectSize={setSelectedSize}
                                    stockMap={stockMap}
                                />
                            </div>
                        )}
                        
                        {/* Quantity */}
                        <div className="space-y-3">
                            <QuantitySelector
                                quantity={quantity}
                                onQuantityChange={setQuantity}
                                max={selectedVariant?.stockQuantity || 99}
                            />
                        </div>
                    </div>

                    <Separator className="bg-border/50" />

                    {/* Actions */}
                    <div className="flex flex-col gap-3">
                        <div className="flex gap-3">
                            <div className="flex-1">
                                <AddToCartButton
                                    isPending={addToCartMutation.isPending}
                                    isSuccess={isAdded}
                                    disabled={!selectedVariant || selectedVariant.stockQuantity < 1}
                                    outOfStock={selectedVariant?.stockQuantity === 0}
                                    onClick={() => addToCartMutation.mutate()}
                                />
                            </div>
                            <Button variant="outline" size="icon" className="h-12 w-12 rounded-xl border-border/50 hover:bg-secondary/50">
                                <Heart className="h-5 w-5" />
                            </Button>
                            <Button variant="outline" size="icon" className="h-12 w-12 rounded-xl border-border/50 hover:bg-secondary/50">
                                <Share2 className="h-5 w-5" />
                            </Button>
                        </div>
                        
                        {/* AI Virtual Try-on Component */}
                        <VirtualTryOn 
                            productId={product.id}
                            productName={product.name}
                            productImage={product.images?.[0]?.url || "/placeholder.jpg"}
                        />
                    </div>

                    {/* Features */}
                    <div className="grid grid-cols-2 gap-4 py-4 rounded-xl bg-secondary/20 px-4 mt-6">
                        <div className="flex items-center gap-3 text-sm font-medium">
                            <div className="bg-background p-2 rounded-lg shadow-sm">
                                <Truck className="h-4 w-4 text-primary" />
                            </div>
                            <span>Free shipping<br/><span className="text-xs text-muted-foreground font-normal">over $100</span></span>
                        </div>
                        <div className="flex items-center gap-3 text-sm font-medium">
                            <div className="bg-background p-2 rounded-lg shadow-sm">
                                <ShieldCheck className="h-4 w-4 text-primary" />
                            </div>
                            <span>Lifetime<br/><span className="text-xs text-muted-foreground font-normal">Warranty</span></span>
                        </div>
                    </div>

                    {/* Product Details */}
                        <div className="space-y-3 text-sm">
                            {product.material && (
                                <div className="flex justify-between">
                                    <span className="text-muted-foreground">Material:</span>
                                    <span className="font-medium">{product.material}</span>
                                </div>
                            )}
                            {product.careInstructions && (
                                <div className="flex justify-between">
                                    <span className="text-muted-foreground">Care:</span>
                                    <span className="font-medium">{product.careInstructions}</span>
                                </div>
                            )}
                            {product.dimensions && (
                                <div className="flex justify-between">
                                    <span className="text-muted-foreground">Dimensions:</span>
                                    <span className="font-medium">{product.dimensions}</span>
                                </div>
                            )}
                            {product.weight && (
                                <div className="flex justify-between">
                                    <span className="text-muted-foreground">Weight:</span>
                                    <span className="font-medium">{product.weight} kg</span>
                                </div>
                            )}
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">SKU:</span>
                                <span className="font-medium">{selectedVariant?.skuCode || product.sku}</span>
                            </div>
                        </div>

                    {/* Tags */}
                    {product.tags && product.tags.length > 0 && (
                        <div className="flex flex-wrap gap-2">
                            {product.tags.map((tag) => (
                                <Badge key={tag} variant="outline" className="text-xs">
                                    {tag}
                                </Badge>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Reviews Section */}
            {product.reviews && product.reviews.length > 0 && (
                <div className="mt-16">
                    <Separator className="mb-8" />
                    <ReviewList
                        reviews={product.reviews}
                        averageRating={averageRating}
                        totalReviews={totalReviews}
                        onHelpfulVote={(reviewId) => {
                            // Implement helpful vote logic
                            console.log("Helpful vote for:", reviewId);
                        }}
                    />
                </div>
            )}
        </div>
    );
}
