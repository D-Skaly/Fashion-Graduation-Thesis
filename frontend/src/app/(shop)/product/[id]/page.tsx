"use client";

import { useState, useMemo, useEffect } from "react";
import { useParams } from "next/navigation";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, Minus, Plus, ShoppingBag, Truck, ShieldCheck, Heart } from "lucide-react";
import { toast } from "sonner";
import Image from "next/image";

import api from "@/lib/axios";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
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
    variants: Variant[];
}

const fetchProduct = async (id: string): Promise<Product> => {
    const { data } = await api.get(`/products/${id}`);
    return data;
}

export default function ProductDetailPage() {
    const params = useParams();
    const id = params.id as string;
    const queryClient = useQueryClient();

    const [selectedColor, setSelectedColor] = useState<string | null>(null);
    const [selectedSize, setSelectedSize] = useState<string | null>(null);
    const [quantity, setQuantity] = useState(1);

    const { data: product, isLoading, isError } = useQuery({
        queryKey: ["product", id],
        queryFn: () => fetchProduct(id),
    });

    // Derive Unique Colors and Sizes
    const { uniqueColors, uniqueSizes } = useMemo(() => {
        if (!product?.variants) return { uniqueColors: [], uniqueSizes: [] };
        
        const colors = Array.from(new Set(product.variants.map(v => v.color))).filter(Boolean);
        const sizes = Array.from(new Set(product.variants.map(v => v.size))).filter(Boolean);
        
        return { uniqueColors: colors, uniqueSizes: sizes };
    }, [product]);

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
            queryClient.invalidateQueries({ queryKey: ["cart"] });
        },
        onError: (error: any) => {
             toast.error(error.response?.data?.message || "Failed to add to cart");
        }
    });

    if (isLoading) {
        return <div className="container mx-auto px-4 py-8 grid md:grid-cols-2 gap-8">
             <Skeleton className="aspect-square w-full rounded-xl" />
             <div className="space-y-4">
                 <Skeleton className="h-10 w-3/4" />
                 <Skeleton className="h-6 w-1/4" />
                 <Skeleton className="h-32 w-full" />
             </div>
        </div>
    }

    if (isError || !product) {
        return <div className="container mx-auto px-4 py-32 text-center">Product not found</div>
    }

    const currentPrice = product.basePrice + (selectedVariant?.priceAdjustment || 0);

    return (
        <div className="container mx-auto px-4 py-10 md:py-16">
            <div className="grid md:grid-cols-2 gap-12 lg:gap-16">
                
                {/* Gallery (Placeholder for now) */}
                <div className="space-y-4">
                    <div className="aspect-[3/4] bg-secondary/20 rounded-xl overflow-hidden relative group">
                         {/* We don't have real images yet so using a color block or Next Image if allowed */}
                         <div className="w-full h-full bg-stone-100 flex items-center justify-center text-muted-foreground">
                            Product Image
                         </div>
                    </div>
                </div>

                {/* Product Info */}
                <div className="space-y-8">
                    <div>
                        <Badge variant="secondary" className="mb-3">{product.categoryName}</Badge>
                        <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-2">{product.name}</h1>
                        <div className="flex items-end gap-4">
                            <span className="text-2xl font-bold">
                                {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(currentPrice)}
                            </span>
                            {/* Can add sale price logic here later */}
                        </div>
                    </div>

                    <p className="text-muted-foreground leading-relaxed">
                        {product.description}
                    </p>

                    <Separator />

                    {/* Selectors */}
                    <div className="space-y-6">
                        {/* Color */}
                        {uniqueColors.length > 0 && (
                            <div className="space-y-3">
                                <span className="text-sm font-medium">Color: <span className="text-muted-foreground">{selectedColor}</span></span>
                                <div className="flex flex-wrap gap-3">
                                    {uniqueColors.map((color) => (
                                        <button
                                            key={color}
                                            onClick={() => setSelectedColor(color)}
                                            className={cn(
                                                "h-10 px-4 rounded-md border text-sm font-medium transition-all",
                                                selectedColor === color 
                                                ? "border-primary bg-primary/5 ring-1 ring-primary" 
                                                : "border-input hover:border-primary/50"
                                            )}
                                        >
                                            {color}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Size */}
                        {uniqueSizes.length > 0 && (
                            <div className="space-y-3">
                                <div className="flex justify-between">
                                    <span className="text-sm font-medium">Size: <span className="text-muted-foreground">{selectedSize}</span></span>
                                    <button className="text-xs underline text-muted-foreground hover:text-primary">Size Guide</button>
                                </div>
                                <div className="flex flex-wrap gap-3">
                                    {uniqueSizes.map((size) => (
                                        <button
                                            key={size}
                                            onClick={() => setSelectedSize(size)}
                                            className={cn(
                                                "h-10 w-12 rounded-md border text-sm font-medium transition-all flex items-center justify-center",
                                                selectedSize === size
                                                ? "border-primary bg-black text-white" 
                                                : "border-input hover:border-black"
                                            )}
                                        >
                                            {size}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}
                        
                        {/* Quantity */}
                         <div className="space-y-3">
                            <span className="text-sm font-medium">Quantity</span>
                            <div className="flex items-center gap-3 w-32">
                                <Button 
                                    variant="outline" 
                                    size="icon" 
                                    className="h-9 w-9"
                                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                >
                                    <Minus className="h-4 w-4" />
                                </Button>
                                <span className="flex-1 text-center font-medium">{quantity}</span>
                                <Button 
                                    variant="outline" 
                                    size="icon" 
                                    className="h-9 w-9"
                                    onClick={() => setQuantity(quantity + 1)}
                                >
                                    <Plus className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>
                    </div>

                    <Separator />

                    {/* Actions */}
                    <div className="flex gap-4">
                        <Button 
                            size="lg" 
                            className="flex-1 text-base h-12"
                            disabled={!selectedVariant || selectedVariant.stockQuantity < 1 || addToCartMutation.isPending}
                            onClick={() => addToCartMutation.mutate()}
                        >
                            {addToCartMutation.isPending ? (
                                <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                            ) : (
                                <ShoppingBag className="mr-2 h-5 w-5" />
                            )}
                            {selectedVariant?.stockQuantity === 0 ? "Out of Stock" : "Add to Cart"}
                        </Button>
                        <Button variant="outline" size="icon" className="h-12 w-12">
                            <Heart className="h-5 w-5" />
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

                </div>
            </div>
        </div>
    );
}
