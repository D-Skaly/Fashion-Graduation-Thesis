"use client";

import { ProductCard, Product } from "@/components/product/ProductCard";

interface ProductGridProps {
    products: Product[];
    isLoading?: boolean;
    columns?: {
        sm?: number;
        md?: number;
        lg?: number;
    };
}

export function ProductGrid({ 
    products, 
    isLoading = false,
    columns = { sm: 2, md: 3, lg: 4 }
}: ProductGridProps) {
    const gridClasses = `grid grid-cols-1 sm:grid-cols-${columns.sm} md:grid-cols-${columns.md} lg:grid-cols-${columns.lg} gap-6`;

    if (isLoading) {
        return (
            <div className={gridClasses}>
                {Array.from({ length: 8 }).map((_, i) => (
                    <div key={i} className="flex flex-col gap-4">
                        <div className="aspect-[3/4] bg-secondary/30 rounded-2xl animate-pulse relative overflow-hidden">
                            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent -translate-x-full animate-[shimmer_1.5s_infinite]" />
                        </div>
                        <div className="space-y-2">
                            <div className="h-3 w-1/3 bg-secondary/30 rounded-full animate-pulse" />
                            <div className="h-5 w-3/4 bg-secondary/40 rounded-full animate-pulse" />
                            <div className="h-5 w-1/4 bg-secondary/30 rounded-full animate-pulse mt-2" />
                        </div>
                    </div>
                ))}
            </div>
        );
    }

    if (!products || products.length === 0) {
        return (
            <div className="text-center py-20">
                <p className="text-muted-foreground text-lg">No products found</p>
                <p className="text-muted-foreground text-sm mt-2">
                    Try adjusting your filters or search terms
                </p>
            </div>
        );
    }

    return (
        <div className={gridClasses}>
            {products.map((product) => (
                <ProductCard key={product.id} product={product} />
            ))}
        </div>
    );
}
