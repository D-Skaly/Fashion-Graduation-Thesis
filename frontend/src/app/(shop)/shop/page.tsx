"use client";

import { useState } from "react";
import { useProducts } from "@/hooks/useProducts";
import { FilterSidebar } from "@/components/shop/FilterSidebar";
import { SortDropdown, SortOption } from "@/components/shop/SortDropdown";
import { ProductGrid } from "@/components/shop/ProductGrid";
import { Pagination } from "@/components/shop/Pagination";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { SlidersHorizontal } from "lucide-react";

function ProductSkeletonGrid() {
    return (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
            {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="space-y-3">
                    <Skeleton className="aspect-[3/4] w-full rounded-xl" />
                    <div className="space-y-2 px-1">
                        <Skeleton className="h-3 w-1/3" />
                        <Skeleton className="h-5 w-3/4" />
                        <Skeleton className="h-5 w-1/3" />
                    </div>
                </div>
            ))}
        </div>
    );
}

export default function ShopPage() {
    const { data: products, isLoading, isError } = useProducts();
    const [showFilters, setShowFilters] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [selectedSort, setSelectedSort] = useState<SortOption>("featured");
    const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [priceRange, setPriceRange] = useState<[number, number]>([0, 1000]);

    const itemsPerPage = 12;
    const totalPages = products ? Math.ceil(products.length / itemsPerPage) : 1;

    const filteredProducts = products ? products.filter(product => {
        if (selectedCategories.length > 0 && !selectedCategories.includes(product.category)) {
            return false;
        }
        if (product.price < priceRange[0] || product.price > priceRange[1]) {
            return false;
        }
        return true;
    }) : [];

    const sortedProducts = [...filteredProducts].sort((a, b) => {
        switch (selectedSort) {
            case "price-low":
                return a.price - b.price;
            case "price-high":
                return b.price - a.price;
            case "newest":
                return (b.isNew ? 1 : 0) - (a.isNew ? 1 : 0);
            default:
                return 0;
        }
    });

    const paginatedProducts = sortedProducts.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    const handleClearFilters = () => {
        setSelectedCategories([]);
        setSelectedBrands([]);
        setPriceRange([0, 1000]);
    };

    return (
        <div className="flex flex-col min-h-screen">
            {/* Premium Shop Banner */}
            <div className="relative h-[25vh] min-h-[200px] w-full flex items-center justify-center bg-secondary/30 dark:bg-background overflow-hidden border-b">
                <div className="absolute inset-0 z-0">
                    <div className="absolute top-0 right-1/4 w-[400px] h-[400px] bg-primary/5 rounded-full blur-[80px]" />
                    <div className="absolute bottom-0 left-1/4 w-[300px] h-[300px] bg-secondary/50 rounded-full blur-[80px]" />
                </div>
                <div className="relative z-10 text-center space-y-3 animate-reveal">
                    <span className="text-sm font-semibold tracking-widest text-muted-foreground uppercase">Discover</span>
                    <h1 className="text-4xl md:text-6xl font-black tracking-widest uppercase drop-shadow-sm">The Collection</h1>
                    <p className="text-muted-foreground text-base md:text-lg max-w-[600px] mx-auto font-light text-balance px-4">
                        Explore our latest curation of premium fashion essentials, designed for the modern aesthetic.
                    </p>
                </div>
            </div>

            <div className="container mx-auto px-4 py-12 md:py-16">
                <div className="flex flex-col lg:flex-row gap-10">
                    {/* Filters Sidebar */}
                    <aside className="lg:w-72 flex-shrink-0">
                        <div className="lg:hidden mb-6 sticky top-20 z-40 bg-background/80 backdrop-blur-md pb-4 pt-2">
                            <Button
                                variant="outline"
                                onClick={() => setShowFilters(!showFilters)}
                                className="w-full rounded-full h-12 border-border/50 shadow-sm gap-2"
                            >
                                <SlidersHorizontal className="h-4 w-4" />
                                {showFilters ? "Hide Filters" : "Show Filters"}
                            </Button>
                        </div>

                        <div className={`${showFilters ? "block" : "hidden"} lg:block sticky top-24`}>
                            <FilterSidebar
                                categories={{
                                    id: "categories",
                                    name: "Categories",
                                    options: Array.from(new Set(products?.map(p => p.category) || [])).map(cat => ({
                                        id: cat,
                                        name: cat,
                                        count: products?.filter(p => p.category === cat).length || 0
                                    }))
                                }}
                                priceRange={{
                                    min: 0,
                                    max: 1000,
                                    currentMin: priceRange[0],
                                    currentMax: priceRange[1]
                                }}
                                onPriceChange={(min, max) => setPriceRange([min, max])}
                                onCategoryChange={setSelectedCategories}
                                onClearAll={handleClearFilters}
                            />
                        </div>
                    </aside>

                    {/* Products Grid Area */}
                    <div className="flex-1">
                        {/* Toolbar */}
                        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-8 pb-4 border-b border-border/50 gap-4">
                            <p className="text-sm font-medium tracking-wide text-muted-foreground">
                                SHOWING <span className="text-foreground font-bold">{paginatedProducts.length}</span> OF <span className="text-foreground font-bold">{filteredProducts.length}</span> PRODUCTS
                            </p>
                            <SortDropdown
                                selectedSort={selectedSort}
                                onSortChange={setSelectedSort}
                            />
                        </div>

                        {/* Active Filters */}
                        {(selectedCategories.length > 0 || selectedBrands.length > 0) && (
                            <div className="flex flex-wrap gap-2 mb-6">
                                {selectedCategories.map(cat => (
                                    <Button
                                        key={cat}
                                        variant="secondary"
                                        size="sm"
                                        className="rounded-full h-8 text-xs font-medium tracking-wide uppercase px-4"
                                        onClick={() => setSelectedCategories(selectedCategories.filter(c => c !== cat))}
                                    >
                                        {cat}
                                        <span className="ml-2 text-muted-foreground">×</span>
                                    </Button>
                                ))}
                            </div>
                        )}

                        {/* Product Grid or Skeleton */}
                        {isLoading ? (
                            <ProductSkeletonGrid />
                        ) : isError ? (
                            <div className="py-20 text-center border rounded-xl bg-muted/20">
                                <p className="text-muted-foreground">Failed to load collection. Please try again later.</p>
                                <Button variant="outline" className="mt-4 rounded-full" onClick={() => window.location.reload()}>
                                    Retry
                                </Button>
                            </div>
                        ) : (
                            <>
                                <ProductGrid products={paginatedProducts} isLoading={isLoading} />

                                {/* Pagination */}
                                {totalPages > 1 && (
                                    <div className="mt-16 pt-8 border-t border-border/50">
                                        <Pagination
                                            currentPage={currentPage}
                                            totalPages={totalPages}
                                            onPageChange={setCurrentPage}
                                        />
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
