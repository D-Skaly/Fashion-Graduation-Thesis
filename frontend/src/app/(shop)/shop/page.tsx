"use client";

import { useState } from "react";
import { useProducts } from "@/hooks/useProducts";
import { FilterSidebar } from "@/components/shop/FilterSidebar";
import { SortDropdown, SortOption } from "@/components/shop/SortDropdown";
import { ProductGrid } from "@/components/shop/ProductGrid";
import { Pagination } from "@/components/shop/Pagination";
import { Button } from "@/components/ui/button";
import { Filter, X } from "lucide-react";

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

    // Filter and sort products
    const filteredProducts = products ? products.filter(product => {
        if (selectedCategories.length > 0 && !selectedCategories.includes(product.category)) {
            return false;
        }
        if (selectedBrands.length > 0) {
            // Assuming brand is in product data
            // if (!selectedBrands.includes(product.brand)) return false;
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
            case "rating":
                // Assuming rating is in product data
                return 0;
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

    if (isLoading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex h-[50vh] w-full items-center justify-center">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
                </div>
            </div>
        );
    }

    if (isError) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex h-[50vh] w-full items-center justify-center text-destructive">
                    Failed to load products. Please try again later.
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <header className="mb-8">
                <h1 className="text-3xl font-bold tracking-tight mb-2">Shop All</h1>
                <p className="text-muted-foreground">
                    Explore our latest collection of premium fashion essentials.
                </p>
            </header>

            <div className="flex flex-col lg:flex-row gap-8">
                {/* Filters Sidebar */}
                <aside className="lg:w-64 flex-shrink-0">
                    <div className="lg:hidden mb-4">
                        <Button
                            variant="outline"
                            onClick={() => setShowFilters(!showFilters)}
                            className="w-full"
                        >
                            <Filter className="h-4 w-4 mr-2" />
                            {showFilters ? "Hide Filters" : "Show Filters"}
                        </Button>
                    </div>

                    <div className={`${showFilters ? "block" : "hidden"} lg:block`}>
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

                {/* Products Grid */}
                <div className="flex-1">
                    {/* Toolbar */}
                    <div className="flex items-center justify-between mb-6">
                        <p className="text-sm text-muted-foreground">
                            Showing {paginatedProducts.length} of {filteredProducts.length} products
                        </p>
                        <SortDropdown
                            selectedSort={selectedSort}
                            onSortChange={setSelectedSort}
                        />
                    </div>

                    {/* Active Filters */}
                    {(selectedCategories.length > 0 || selectedBrands.length > 0) && (
                        <div className="flex flex-wrap gap-2 mb-4">
                            {selectedCategories.map(cat => (
                                <Button
                                    key={cat}
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => setSelectedCategories(selectedCategories.filter(c => c !== cat))}
                                >
                                    {cat} <X className="h-3 w-3 ml-1" />
                                </Button>
                            ))}
                        </div>
                    )}

                    {/* Product Grid */}
                    <ProductGrid products={paginatedProducts} isLoading={isLoading} />

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="mt-8">
                            <Pagination
                                currentPage={currentPage}
                                totalPages={totalPages}
                                onPageChange={setCurrentPage}
                            />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
