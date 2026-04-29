"use client";

import Link from "next/link";
import Image from "next/image";
import { Heart, ShoppingCart, Star, ImageOff } from "lucide-react";
import { useState } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { WishlistButton } from "@/components/wishlist/WishlistButton";
import { cn } from "@/lib/utils";

export interface Product {
    id: string;
    name: string;
    price: number;
    category: string;
    image?: string;
    hoverImage?: string;
    isNew?: boolean;
    isSale?: boolean;
    salePrice?: number;
    rating?: number;
    reviewCount?: number;
}

interface ProductCardProps {
    product: Product;
}

function RatingStars({ rating = 0, reviewCount }: { rating?: number; reviewCount?: number }) {
    return (
        <div className="flex items-center gap-1">
            <div className="flex items-center">
                {[1, 2, 3, 4, 5].map((star) => (
                    <Star
                        key={star}
                        className={cn(
                            "h-3 w-3",
                            star <= Math.round(rating)
                                ? "fill-amber-400 text-amber-400"
                                : "fill-muted text-muted"
                        )}
                    />
                ))}
            </div>
            {reviewCount !== undefined && reviewCount > 0 && (
                <span className="text-xs text-muted-foreground">({reviewCount})</span>
            )}
        </div>
    );
}

function ProductImage({ image, name, className }: { image?: string; name: string; className?: string }) {
    const [error, setError] = useState(false);
    const isPlaceholder = !image || image.startsWith("bg-") || error;

    if (isPlaceholder) {
        return (
            <div className={cn("flex items-center justify-center bg-gradient-to-br from-secondary/60 to-secondary/30", className)}>
                <div className="text-center">
                    <ImageOff className="h-8 w-8 mx-auto text-muted-foreground/30 mb-2" />
                    <span className="text-[10px] uppercase tracking-widest text-muted-foreground/40 font-medium">{name.slice(0, 20)}</span>
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
    const [isHovered, setIsHovered] = useState(false);

    return (
        <Card
            className="group overflow-hidden border-none shadow-sm hover:shadow-xl transition-all duration-500 bg-card"
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            <CardContent className="p-0 relative aspect-[3/4] overflow-hidden bg-secondary/10">
                {/* Badges */}
                <div className="absolute top-3 left-3 z-20 flex flex-col gap-2">
                    {product.isNew && (
                        <Badge className="bg-blue-600 hover:bg-blue-700 text-[10px] tracking-wider uppercase font-bold px-2.5 py-0.5">
                            NEW
                        </Badge>
                    )}
                    {product.isSale && (
                        <Badge variant="destructive" className="text-[10px] tracking-wider uppercase font-bold px-2.5 py-0.5">
                            SALE
                        </Badge>
                    )}
                </div>

                {/* Wishlist Button */}
                <div className="absolute top-3 right-3 z-20 opacity-0 group-hover:opacity-100 transition-all duration-300 translate-y-1 group-hover:translate-y-0">
                    <WishlistButton
                        productId={product.id}
                        variant="ghost"
                        size="icon"
                        className="h-9 w-9 bg-background/80 backdrop-blur-sm shadow-sm hover:bg-background"
                    />
                </div>

                {/* Product Image */}
                <Link href={`/product/${product.id}`} className="block w-full h-full relative">
                    {/* Main Image */}
                    <div className="absolute inset-0 w-full h-full transition-all duration-700 ease-in-out group-hover:scale-105">
                        <ProductImage image={product.image} name={product.name} className="transition-opacity duration-500 group-hover:opacity-90" />
                    </div>

                    {/* Hover Overlay */}
                    <div className="absolute inset-0 bg-black/0 group-hover:bg-black/5 dark:group-hover:bg-white/5 transition-colors duration-500" />

                    {/* Hover View Label */}
                    <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-500">
                        <span className="bg-background/90 backdrop-blur-sm text-foreground px-6 py-2.5 text-xs font-bold tracking-[0.2em] uppercase rounded-full shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-transform duration-500">
                            View Details
                        </span>
                    </div>
                </Link>

                {/* Quick Add Overlay */}
                <div className="absolute bottom-0 left-0 right-0 p-4 translate-y-full group-hover:translate-y-0 transition-transform duration-500 ease-out">
                    <Button
                        className="w-full gap-2 shadow-xl rounded-full h-12 font-semibold tracking-wide text-sm"
                        size="lg"
                        asChild
                    >
                        <Link href={`/product/${product.id}`}>
                            <ShoppingCart className="h-4 w-4" />
                            Select Options
                        </Link>
                    </Button>
                </div>
            </CardContent>

            <CardFooter className="flex flex-col items-start gap-1.5 p-4">
                <div className="flex items-center justify-between w-full">
                    <p className="text-[11px] text-muted-foreground font-semibold uppercase tracking-widest">
                        {product.category}
                    </p>
                    <RatingStars rating={product.rating} reviewCount={product.reviewCount} />
                </div>

                <Link
                    href={`/product/${product.id}`}
                    className="font-semibold text-base hover:text-primary/80 transition-colors line-clamp-1 w-full"
                >
                    {product.name}
                </Link>

                <div className="flex items-center gap-2.5 mt-0.5">
                    {product.isSale && product.salePrice ? (
                        <>
                            <span className="font-bold text-lg tracking-tight">
                                ${product.salePrice.toFixed(2)}
                            </span>
                            <span className="text-sm text-muted-foreground line-through">
                                ${product.price.toFixed(2)}
                            </span>
                            <Badge variant="outline" className="text-[10px] border-destructive/30 text-destructive font-bold px-1.5 py-0">
                                -{Math.round((1 - product.salePrice / product.price) * 100)}%
                            </Badge>
                        </>
                    ) : (
                        <span className="font-bold text-lg tracking-tight">
                            ${product.price.toFixed(2)}
                        </span>
                    )}
                </div>
            </CardFooter>
        </Card>
    );
}
