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
import { VirtualTryOnModal } from "@/components/product/VirtualTryOnModal";

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
        <div className="container mx-auto px-4 py-8 md:py-12">
            <div className="flex flex-col md:flex-row gap-12 lg:gap-20">
                
                {/* Gallery (Scrollable Left Column) */}
                <div className="w-full md:w-3/5 space-y-4">
                    {/* Fake multiple images for Zara-like premium feel */}
                    {[1, 2, 3].map((imgIndex) => (
                        <div key={imgIndex} className="aspect-[3/4] w-full bg-secondary/20 overflow-hidden relative group">
                            <div className="w-full h-full bg-stone-100 dark:bg-stone-900 flex items-center justify-center text-muted-foreground/30 font-light tracking-widest uppercase">
                                View {imgIndex}
                            </div>
                        </div>
                    ))}
                </div>

                {/* Product Info (Sticky Right Column) */}
                <div className="w-full md:w-2/5">
                    <div className="sticky top-28 space-y-8">
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
                                <div className="flex justify-between items-end">
                                    <span className="text-sm font-medium uppercase tracking-widest text-muted-foreground">Select Size <span className="text-foreground ml-2">{selectedSize}</span></span>
                                    <button className="text-xs underline decoration-muted-foreground underline-offset-4 text-muted-foreground hover:text-primary transition-colors">Size Guide</button>
                                </div>
                                <div className="grid grid-cols-4 gap-2">
                                    {uniqueSizes.map((size) => (
                                        <button
                                            key={size}
                                            onClick={() => setSelectedSize(size)}
                                            className={cn(
                                                "h-12 border text-sm font-medium transition-all flex items-center justify-center uppercase tracking-wider",
                                                selectedSize === size
                                                ? "border-foreground bg-foreground text-background" 
                                                : "border-border hover:border-foreground/50"
                                            )}
                                        >
                                            {size}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}
                        
                        {/* Quantity (Minimal) */}
                         <div className="space-y-3 pt-2">
                            <div className="flex items-center border border-border w-max h-12">
                                <button 
                                    className="px-4 h-full hover:bg-secondary/50 transition-colors"
                                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                >
                                    <Minus className="h-4 w-4" />
                                </button>
                                <span className="w-12 text-center font-medium">{quantity}</span>
                                <button 
                                    className="px-4 h-full hover:bg-secondary/50 transition-colors"
                                    onClick={() => setQuantity(quantity + 1)}
                                >
                                    <Plus className="h-4 w-4" />
                                </button>
                            </div>
                        </div>
                    </div>

                    <Separator />

                    {/* Actions */}
                    <div className="flex flex-col gap-3">
                        <VirtualTryOnModal productId={product.id} productName={product.name} />
                        
                        <Button 
                            size="lg" 
                            className="w-full text-sm font-semibold tracking-widest uppercase h-14 rounded-none bg-foreground text-background hover:bg-foreground/90 transition-all"
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
                        <Button variant="outline" className="w-full text-sm font-semibold tracking-widest uppercase h-14 rounded-none border-border hover:bg-secondary/20">
                            <Heart className="mr-2 h-4 w-4" /> Add to Wishlist
                        </Button>
                    </div>

                    {/* Features */}
                    <div className="space-y-3 text-sm mt-8 border border-border p-4 bg-secondary/10">
                        <div className="flex items-center gap-3 text-foreground">
                            <Truck className="h-4 w-4 text-muted-foreground" />
                            <span className="font-light tracking-wide">Free standard shipping on orders over $150</span>
                        </div>
                        <div className="flex items-center gap-3 text-foreground">
                            <ShieldCheck className="h-4 w-4 text-muted-foreground" />
                            <span className="font-light tracking-wide">30-day free returns. Lifetime authenticity guarantee.</span>
                        </div>
                    </div>
                </div>
                </div>
            </div>

            {/* AI Complete The Look Section */}
            <div className="mt-32 pt-16 border-t border-border">
                <div className="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
                    <div>
                        <div className="inline-flex items-center gap-2 px-3 py-1 mb-4 rounded-full bg-primary/5 border border-primary/10">
                            <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                            <span className="text-xs font-bold tracking-widest uppercase text-primary">AI Stylist</span>
                        </div>
                        <h2 className="text-3xl md:text-4xl font-black tracking-tight uppercase">Complete The Look</h2>
                    </div>
                    <Button variant="ghost" className="tracking-widest uppercase text-xs font-bold">View All Recommendations</Button>
                </div>
                
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-8">
                    {[1, 2, 3, 4].map((item) => (
                        <div key={item} className="group cursor-pointer">
                            <div className="aspect-[3/4] bg-secondary/20 mb-4 overflow-hidden relative">
                                <div className="absolute inset-0 bg-stone-100 dark:bg-stone-900 transition-transform duration-700 group-hover:scale-105" />
                            </div>
                            <h3 className="text-sm font-bold uppercase tracking-wider mb-1 line-clamp-1 group-hover:underline underline-offset-4">Suggested Item {item}</h3>
                            <p className="text-sm text-muted-foreground">$89.00</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
