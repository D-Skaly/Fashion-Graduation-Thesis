"use client";

import { useState, useEffect, useCallback } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter, useSearchParams } from "next/navigation";
import { Search, SlidersHorizontal, Grid3x3, List, Loader2, X } from "lucide-react";
import { useDebouncedCallback } from "use-debounce";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ProductCard } from "@/components/product/ProductCard";
import { ProductSummary } from "@/lib/apiService";
import api from "@/lib/axios";

interface SearchResults {
  content: ProductSummary[];
  totalElements: number;
  totalPages: number;
}

const searchProducts = async (query: string): Promise<ProductSummary[]> => {
  if (!query) return [];
  const { data } = await api.get(`/products/search`, { params: { q: query } });
  return Array.isArray(data) ? data : data.content || [];
};

export default function SearchPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const query = searchParams.get("q") || "";
  const [searchInput, setSearchInput] = useState(query);
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const [sortBy, setSortBy] = useState("relevance");

  const { data: results, isLoading } = useQuery({
    queryKey: ["search", query],
    queryFn: () => searchProducts(query),
    enabled: query.length > 0,
  });

  const debouncedSearch = useDebouncedCallback((value: string) => {
    if (value.trim()) {
      router.push(`/search?q=${encodeURIComponent(value.trim())}`);
    }
  }, 300);

  const handleSearch = (value: string) => {
    setSearchInput(value);
    debouncedSearch(value);
  };

  const clearSearch = () => {
    setSearchInput("");
    router.push("/search");
  };

  const sortedResults = results ? [...results].sort((a, b) => {
    switch (sortBy) {
      case "price-asc": return (a.price || 0) - (b.price || 0);
      case "price-desc": return (b.price || 0) - (a.price || 0);
      case "name": return a.name.localeCompare(b.name);
      default: return 0; // relevance
    }
  }) : [];

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      {/* Search Header */}
      <div className="mb-8 space-y-4">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search products..."
              value={searchInput}
              onChange={(e) => handleSearch(e.target.value)}
              className="pl-10 h-12"
              autoFocus
            />
            {searchInput && (
              <button
                onClick={clearSearch}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                <X className="h-4 w-4" />
              </button>
            )}
          </div>
        </div>

        {/* Filters & Sort */}
        {query && (
          <div className="flex items-center justify-between gap-4">
            <p className="text-sm text-muted-foreground">
              {results?.length || 0} result(s) for "{query}"
            </p>
            <div className="flex items-center gap-2">
              <Select value={sortBy} onValueChange={setSortBy}>
                <SelectTrigger className="w-44 h-9">
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="relevance">Relevance</SelectItem>
                  <SelectItem value="price-asc">Price: Low to High</SelectItem>
                  <SelectItem value="price-desc">Price: High to Low</SelectItem>
                  <SelectItem value="name">Name</SelectItem>
                </SelectContent>
              </Select>

              <div className="border rounded-lg flex">
                <Button
                  variant={viewMode === "grid" ? "default" : "ghost"}
                  size="sm"
                  onClick={() => setViewMode("grid")}
                  className="rounded-r-none"
                >
                  <Grid3x3 className="h-4 w-4" />
                </Button>
                <Button
                  variant={viewMode === "list" ? "default" : "ghost"}
                  size="sm"
                  onClick={() => setViewMode("list")}
                  className="rounded-l-none"
                >
                  <List className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Loading */}
      {isLoading && (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      )}

      {/* Empty State */}
      {!isLoading && query && (!results || results.length === 0) && (
        <div className="text-center py-12">
          <Search className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
          <p className="text-lg font-medium">No products found</p>
          <p className="text-muted-foreground mt-2">
            Try a different search term
          </p>
        </div>
      )}

      {/* Results */}
      {!isLoading && sortedResults.length > 0 && (
        <div
          className={
            viewMode === "grid"
              ? "grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
              : "space-y-4"
          }
        >
          {sortedResults.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}

      {/* Initial State */}
      {!query && (
        <div className="text-center py-12">
          <Search className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
          <p className="text-lg font-medium">Search for products</p>
          <p className="text-muted-foreground mt-2">
            Enter a product name, category, or keyword
          </p>
        </div>
      )}
    </div>
  );
}
