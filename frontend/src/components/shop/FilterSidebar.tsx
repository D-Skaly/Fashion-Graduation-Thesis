"use client";

import { useState } from "react";
import { Slider } from "@/components/ui/slider";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { X } from "lucide-react";

interface FilterOption {
    id: string;
    name: string;
    count: number;
}

interface FilterGroup {
    id: string;
    name: string;
    options: FilterOption[];
}

interface FilterSidebarProps {
    categories?: FilterGroup;
    brands?: FilterGroup;
    priceRange?: {
        min: number;
        max: number;
        currentMin: number;
        currentMax: number;
    };
    onPriceChange?: (min: number, max: number) => void;
    onCategoryChange?: (categoryIds: string[]) => void;
    onBrandChange?: (brandIds: string[]) => void;
    onClearAll?: () => void;
    className?: string;
}

export function FilterSidebar({
    categories,
    brands,
    priceRange,
    onPriceChange,
    onCategoryChange,
    onBrandChange,
    onClearAll,
    className
}: FilterSidebarProps) {
    const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
    const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
    const [currentPrice, setCurrentPrice] = useState<[number, number]>(
        priceRange ? [priceRange.currentMin, priceRange.currentMax] : [0, 1000]
    );

    const handleCategoryToggle = (categoryId: string) => {
        const newSelected = selectedCategories.includes(categoryId)
            ? selectedCategories.filter(id => id !== categoryId)
            : [...selectedCategories, categoryId];
        setSelectedCategories(newSelected);
        onCategoryChange?.(newSelected);
    };

    const handleBrandToggle = (brandId: string) => {
        const newSelected = selectedBrands.includes(brandId)
            ? selectedBrands.filter(id => id !== brandId)
            : [...selectedBrands, brandId];
        setSelectedBrands(newSelected);
        onBrandChange?.(newSelected);
    };

    const handlePriceChange = (value: number[]) => {
        setCurrentPrice([value[0], value[1]]);
        onPriceChange?.(value[0], value[1]);
    };

    const handleClearAll = () => {
        setSelectedCategories([]);
        setSelectedBrands([]);
        setCurrentPrice(priceRange ? [priceRange.min, priceRange.max] : [0, 1000]);
        onClearAll?.();
    };

    const hasActiveFilters = selectedCategories.length > 0 || selectedBrands.length > 0;

    return (
        <div className={className}>
            <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold text-lg">Filters</h3>
                {hasActiveFilters && (
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={handleClearAll}
                        className="text-muted-foreground"
                    >
                        <X className="h-4 w-4 mr-1" />
                        Clear All
                    </Button>
                )}
            </div>

            {/* Price Range */}
            {priceRange && (
                <div className="space-y-4 mb-6">
                    <h4 className="font-medium">Price Range</h4>
                    <div className="px-2">
                        <Slider
                            min={priceRange.min}
                            max={priceRange.max}
                            step={10}
                            value={currentPrice}
                            onValueChange={handlePriceChange}
                            className="my-4"
                        />
                    </div>
                    <div className="flex items-center justify-between text-sm">
                        <span className="text-muted-foreground">${currentPrice[0]}</span>
                        <span className="text-muted-foreground">${currentPrice[1]}</span>
                    </div>
                </div>
            )}

            <Separator className="mb-6" />

            {/* Categories */}
            {categories && categories.options.length > 0 && (
                <div className="space-y-3 mb-6">
                    <h4 className="font-medium">{categories.name}</h4>
                    <div className="space-y-2">
                        {categories.options.map((option) => (
                            <div key={option.id} className="flex items-center space-x-2">
                                <Checkbox
                                    id={`category-${option.id}`}
                                    checked={selectedCategories.includes(option.id)}
                                    onCheckedChange={() => handleCategoryToggle(option.id)}
                                />
                                <label
                                    htmlFor={`category-${option.id}`}
                                    className="text-sm cursor-pointer flex-1 flex items-center justify-between"
                                >
                                    <span>{option.name}</span>
                                    <span className="text-muted-foreground text-xs">
                                        ({option.count})
                                    </span>
                                </label>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {categories && brands && <Separator className="mb-6" />}

            {/* Brands */}
            {brands && brands.options.length > 0 && (
                <div className="space-y-3">
                    <h4 className="font-medium">{brands.name}</h4>
                    <div className="space-y-2">
                        {brands.options.map((option) => (
                            <div key={option.id} className="flex items-center space-x-2">
                                <Checkbox
                                    id={`brand-${option.id}`}
                                    checked={selectedBrands.includes(option.id)}
                                    onCheckedChange={() => handleBrandToggle(option.id)}
                                />
                                <label
                                    htmlFor={`brand-${option.id}`}
                                    className="text-sm cursor-pointer flex-1 flex items-center justify-between"
                                >
                                    <span>{option.name}</span>
                                    <span className="text-muted-foreground text-xs">
                                        ({option.count})
                                    </span>
                                </label>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}
