"use client";

import Link from "next/link";
import Image from "next/image";
import { Heart, ShoppingCart, Star, ImageOff, ArrowUpRight } from "lucide-react";
import { useState } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { WishlistButton } from "@/components/wishlist/WishlistButton";
import { cn } from "@/lib/utils";
import { ProductSummary } from "@/types/product";

interface ProductCardProps {
    product: ProductSummary;
}

function RatingStars({ rating = 0, reviewCount }: { rating?: number; reviewCount?: number }) {
    return (
        <div className="flex items-center gap-1.5">
            <div className="flex items-center">
                {[1, 2, 3, 4, 5].map((star) => (
                    <Star
                        key={star}
                        className={cn(
                            "h-2.5 w-2.5",
                            star <= Math.round(rating)
                                ? "fill-amber-400 text-amber-400"
                                : "fill-muted text-muted border-none"
                        )}
                    />
                ))}
            </div>
            {reviewCount !== undefined && reviewCount > 0 && (
                <span className="text-[10px] font-bold text-muted-foreground/60 tracking-tighter">({reviewCount})</span>
            )}
        </div>
    );
}

function ProductImage({ image, name, className }: { image?: string; name: string; className?: string }) {
    const [error, setError] = useState(false);
    const isPlaceholder = !image || image.startsWith("bg-") || error;

    if (isPlaceholder) {
        return (
            <div className={cn("flex items-center justify-center bg-secondary/20", className)}>
                <div className="text-center p-6">
                    <ImageOff className="h-10 w-10 mx-auto text-muted-foreground/20 mb-3" />
                    <span className="text-[10px] uppercase tracking-[0.3em] text-muted-foreground/30 font-black leading-tight block">{name.slice(0, 30)}</span>
                </div>
            </div>
        );
    }

    return (
        <Image
            src={image}
            alt={name}
            fill
            className={cn("object-cover", className)}
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 25vw"
            onError={() => setError(true)}
        />
    );
}

export function ProductCard({ product }: ProductCardProps) {
    return (
        <Card
            className="group overflow-hidden border-none shadow-none bg-transparent transition-all duration-500"
        >
            <CardContent className="p-0 relative aspect-[3/4] overflow-hidden rounded-[2rem] bg-secondary/10">
                {/* Badges */}
                <div className="absolute top-5 left-5 z-20 flex flex-col gap-2">
                    {product.isNew && (
                        <Badge className="bg-white text-black hover:bg-white text-[9px] tracking-[0.2em] uppercase font-black px-3 py-1 rounded-full shadow-lg border-none">
                            NEW
                        </Badge>
                    )}
                    {product.isSale && (
                        <Badge variant="destructive" className="text-[9px] tracking-[0.2em] uppercase font-black px-3 py-1 rounded-full shadow-lg border-none">
                            -{Math.round((1 - (product.salePrice || 0) / product.price) * 100)}%
                        </Badge>
                    )}
                </div>

                {/* Wishlist Button */}
                <div className="absolute top-5 right-5 z-20 opacity-0 group-hover:opacity-100 transition-all duration-500 translate-x-2 group-hover:translate-x-0">
                    <WishlistButton
                        productId={product.id}
                        variant="ghost"
                        size="icon"
                        className="h-10 w-10 bg-white/90 backdrop-blur-md shadow-xl hover:bg-white rounded-full text-black border-none"
                    />
                </div>

                {/* Product Image */}
                <Link href={`/product/${product.id}`} className="block w-full h-full relative">
                    <div className="absolute inset-0 w-full h-full transition-all duration-1000 ease-[cubic-bezier(0.23,1,0.32,1)] group-hover:scale-110">
                        <ProductImage image={product.image} name={product.name} className="transition-opacity duration-700 group-hover:opacity-90" />
                    </div>

                    {/* Hover Overlay */}
                    <div className="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-700" />

                    {/* Hover View Label */}
                    <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-700">
                        <div className="bg-white text-black p-4 rounded-full shadow-2xl transform scale-75 group-hover:scale-100 transition-transform duration-700 ease-out">
                            <ArrowUpRight className="h-6 w-6" />
                        </div>
                    </div>
                </Link>

                {/* Quick Add Overlay */}
                <div className="absolute bottom-5 left-5 right-5 translate-y-20 group-hover:translate-y-0 transition-transform duration-700 ease-[cubic-bezier(0.23,1,0.32,1)]">
                    <Button
                        className="w-full gap-2 shadow-2xl rounded-2xl h-14 font-black uppercase tracking-[0.15em] text-[11px] bg-black text-white hover:bg-zinc-800 border-none"
                        size="lg"
                        asChild
                    >
                        <Link href={`/product/${product.id}`}>
                            <ShoppingCart className="h-4 w-4" />
                            Pre-Order Now
                        </Link>
                    </Button>
                </div>
            </CardContent>

            <CardFooter className="flex flex-col items-start gap-2 p-5 pb-2">
                <div className="flex items-center justify-between w-full">
                    <span className="text-[10px] text-muted-foreground font-black uppercase tracking-[0.2em] bg-secondary/40 px-2 py-0.5 rounded-md">
                        {product.category}
                    </span>
                    <RatingStars rating={product.rating} reviewCount={product.reviewCount} />
                </div>

                <Link
                    href={`/product/${product.id}`}
                    className="font-bold text-lg tracking-tight hover:text-primary transition-colors line-clamp-1 w-full text-foreground"
                >
                    {product.name}
                </Link>

                <div className="flex items-center gap-3 mt-1">
                    {product.isSale && product.salePrice ? (
                        <>
                            <span className="font-black text-xl tracking-tighter text-foreground">
                                ${product.salePrice.toLocaleString()}
                            </span>
                            <span className="text-sm text-muted-foreground line-through font-medium opacity-50">
                                ${product.price.toLocaleString()}
                            </span>
                        </>
                    ) : (
                        <span className="font-black text-xl tracking-tighter text-foreground">
                            ${product.price.toLocaleString()}
                        </span>
                    )}
                </div>
            </CardFooter>
        </Card>
    );
}
