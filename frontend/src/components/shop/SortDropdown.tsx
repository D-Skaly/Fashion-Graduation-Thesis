"use client";

import { useState } from "react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { ArrowUpDown } from "lucide-react";

export type SortOption = "featured" | "newest" | "price-low" | "price-high" | "rating";

interface SortOptionConfig {
    id: SortOption;
    label: string;
    description: string;
}

const sortOptions: SortOptionConfig[] = [
    { id: "featured", label: "Featured", description: "Best selling products" },
    { id: "newest", label: "Newest", description: "Latest arrivals" },
    { id: "price-low", label: "Price: Low to High", description: "Sort by price ascending" },
    { id: "price-high", label: "Price: High to Low", description: "Sort by price descending" },
    { id: "rating", label: "Top Rated", description: "Highest rated products" },
];

interface SortDropdownProps {
    selectedSort?: SortOption;
    onSortChange?: (sort: SortOption) => void;
    className?: string;
}

export function SortDropdown({ selectedSort = "featured", onSortChange, className }: SortDropdownProps) {
    const selectedOption = sortOptions.find(opt => opt.id === selectedSort);

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant="outline" className={className}>
                    <ArrowUpDown className="h-4 w-4 mr-2" />
                    Sort: {selectedOption?.label || "Featured"}
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel>Sort by</DropdownMenuLabel>
                <DropdownMenuSeparator />
                {sortOptions.map((option) => (
                    <DropdownMenuItem
                        key={option.id}
                        onClick={() => onSortChange?.(option.id)}
                        className="flex flex-col items-start py-2"
                    >
                        <span className="font-medium">{option.label}</span>
                        <span className="text-xs text-muted-foreground">{option.description}</span>
                    </DropdownMenuItem>
                ))}
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
