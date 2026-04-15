"use client";

import { ProductCard } from "@/components/product/ProductCard";

interface Product {
    id: string;
    name: string;
    price: number;
    category: string;
    image: string;
    isNew?: boolean;
    isSale?: boolean;
    salePrice?: number;
}

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
                    <div
                        key={i}
                        className="aspect-[3/4] bg-secondary/20 rounded-xl animate-pulse"
                    />
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
