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
        if (!product?.variants || !selectedColor) return {};
        
        const map: Record<string, number> = {};
        product.variants
            .filter(v => v.color === selectedColor)
            .forEach(v => {
                map[v.size] = v.stockQuantity;
            });
        
        return map;
    }, [product, selectedColor]);

    // Find Selected Variant
    const selectedVariant = useMemo(() => {
        if (!product?.variants || !selectedColor || !selectedSize) return null;
        return product.variants.find(v => v.color === selectedColor && v.size === selectedSize);
    }, [product, selectedColor, selectedSize]);

    // Set defaults on load
    useEffect(() => {
        if (product?.variants && product.variants.length > 0) {
            if (!selectedColor && uniqueColors.length > 0) setSelectedColor(uniqueColors[0]);
            if (!selectedSize && uniqueSizes.length > 0) setSelectedSize(uniqueSizes[0]);
        }
    }, [product, uniqueColors, uniqueSizes]);

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
        onError: (error: any) => {
            toast.error(error.response?.data?.message || "Failed to add to cart");
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
        return <div className="container mx-auto px-4 py-32 text-center">Product not found</div>;
    }

    const currentPrice = product.basePrice + (selectedVariant?.priceAdjustment || 0);
    const averageRating = product.ratingAvg || 0;
    const totalReviews = product.reviews?.length || 0;

    return (
        <div className="container mx-auto px-4 py-10 md:py-16">
            <div className="grid md:grid-cols-2 gap-12 lg:gap-16">
                
                {/* Gallery */}
                <div>
                    <ImageGallery 
                        images={product.images || []} 
                        productName={product.name}
                    />
                </div>

                {/* Product Info */}
                <div className="space-y-8">
                    <div>
                        <Badge variant="secondary" className="mb-3">{product.categoryName}</Badge>
                        {product.isFeatured && (
                            <Badge className="mb-3 ml-2 bg-amber-500 hover:bg-amber-600">Featured</Badge>
                        )}
                        <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-2">{product.name}</h1>
                        <div className="flex items-end gap-4">
                            <span className="text-2xl font-bold">
                                {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(currentPrice)}
                            </span>
                            {product.soldCount > 0 && (
                                <span className="text-sm text-muted-foreground">
                                    {product.soldCount} sold
                                </span>
                            )}
                        </div>
                        {product.brand && (
                            <p className="text-sm text-muted-foreground mt-1">Brand: {product.brand}</p>
                        )}
                    </div>

                    <p className="text-muted-foreground leading-relaxed">
                        {product.description}
                    </p>

                    <Separator />

                    {/* Selectors */}
                    <div className="space-y-6">
                        {/* Color */}
                        {uniqueColors.length > 0 && (
                            <ColorSelector
                                colors={uniqueColors}
                                selectedColor={selectedColor}
                                onSelectColor={setSelectedColor}
                            />
                        )}

                        {/* Size */}
                        {uniqueSizes.length > 0 && (
                            <SizeSelector
                                sizes={uniqueSizes}
                                selectedSize={selectedSize}
                                onSelectSize={setSelectedSize}
                                stockMap={stockMap}
                            />
                        )}
                        
                        {/* Quantity */}
                        <QuantitySelector
                            quantity={quantity}
                            onQuantityChange={setQuantity}
                            max={selectedVariant?.stockQuantity || 99}
                        />
                    </div>

                    <Separator />

                    {/* Actions */}
                    <div className="flex gap-4">
                        <AddToCartButton
                            isPending={addToCartMutation.isPending}
                            isSuccess={isAdded}
                            disabled={!selectedVariant || selectedVariant.stockQuantity < 1}
                            outOfStock={selectedVariant?.stockQuantity === 0}
                            onClick={() => addToCartMutation.mutate()}
                        />
                        <Button variant="outline" size="icon" className="h-12 w-12">
                            <Heart className="h-5 w-5" />
                        </Button>
                        <Button variant="outline" size="icon" className="h-12 w-12">
                            <Share2 className="h-5 w-5" />
                        </Button>
                    </div>

                    {/* Features */}
                    <div className="grid grid-cols-2 gap-4 text-sm mt-4">
                        <div className="flex items-center gap-2 text-muted-foreground">
                            <Truck className="h-4 w-4" />
                            <span>Free shipping over $100</span>
                        </div>
                        <div className="flex items-center gap-2 text-muted-foreground">
                            <ShieldCheck className="h-4 w-4" />
                            <span>Lifetime Warranty</span>
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
