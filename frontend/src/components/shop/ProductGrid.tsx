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
    // Explicitly mapping column counts to avoid Tailwind class purging/dynamic issues
    const gridColsSm = columns.sm === 1 ? 'sm:grid-cols-1' : columns.sm === 2 ? 'sm:grid-cols-2' : 'sm:grid-cols-3';
    const gridColsMd = columns.md === 2 ? 'md:grid-cols-2' : columns.md === 3 ? 'md:grid-cols-3' : 'md:grid-cols-4';
    const gridColsLg = columns.lg === 3 ? 'lg:grid-cols-3' : columns.lg === 4 ? 'lg:grid-cols-4' : 'lg:grid-cols-5';

    const gridClasses = `grid grid-cols-1 ${gridColsSm} ${gridColsMd} ${gridColsLg} gap-8 md:gap-10`;

    if (isLoading) {
        return (
            <div className={gridClasses}>
                {Array.from({ length: 8 }).map((_, i) => (
                    <div key={i} className="flex flex-col gap-5">
                        <div className="aspect-[3/4] bg-secondary/20 rounded-3xl animate-pulse relative overflow-hidden">
                            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent -translate-x-full animate-[shimmer_2s_infinite]" />
                        </div>
                        <div className="space-y-3 px-2">
                            <div className="h-2.5 w-1/4 bg-secondary/20 rounded-full animate-pulse" />
                            <div className="h-6 w-3/4 bg-secondary/30 rounded-full animate-pulse" />
                            <div className="h-6 w-1/3 bg-secondary/20 rounded-full animate-pulse mt-2" />
                        </div>
                    </div>
                ))}
            </div>
        );
    }

    if (!products || products.length === 0) {
        return (
            <div className="text-center py-32 border-2 border-dashed border-border/50 rounded-3xl">
                <p className="text-muted-foreground text-xl font-light tracking-wide italic">The collection is currently empty.</p>
                <p className="text-muted-foreground/60 text-sm mt-4 uppercase tracking-[0.2em] font-medium">
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
