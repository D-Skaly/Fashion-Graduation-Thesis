"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Sparkles, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ProductCard } from "@/components/product/ProductCard";
import api from "@/lib/axios";

interface Product {
  id: string;
  name: string;
  basePrice: number;
  images?: string[];
  createdAt: string;
  featured?: boolean;
  category?: string;
}

const fetchNewArrivals = async (days: number): Promise<Product[]> => {
  const { data } = await api.get("/products", {
    params: { size: 100, sort: "createdAt,desc" },
  });
  const products = Array.isArray(data) ? data : data.content || [];

  // Filter by date
  const cutoffDate = new Date();
  cutoffDate.setDate(cutoffDate.getDate() - days);

  return products.filter((p: Product) => {
    const createdAt = new Date(p.createdAt);
    return createdAt >= cutoffDate;
  });
};

export default function NewArrivalsPage() {
  const [dateRange, setDateRange] = useState("7");

  const { data: products, isLoading } = useQuery({
    queryKey: ["new-arrivals", dateRange],
    queryFn: () => fetchNewArrivals(parseInt(dateRange)),
  });

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-bold flex items-center gap-2">
            <Sparkles className="h-6 w-6 text-primary" />
            New Arrivals
          </h1>
          <p className="text-muted-foreground mt-1">
            Discover our latest fashion collections
          </p>
        </div>

        <Select value={dateRange} onValueChange={setDateRange}>
          <SelectTrigger className="w-48">
            <SelectValue placeholder="Filter by date" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="7">Last 7 Days</SelectItem>
            <SelectItem value="14">Last 14 Days</SelectItem>
            <SelectItem value="30">Last 30 Days</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <Card key={i} className="overflow-hidden">
              <div className="aspect-square bg-muted animate-pulse" />
              <CardContent className="p-4 space-y-2">
                <div className="h-4 w-3/4 bg-muted animate-pulse rounded" />
                <div className="h-4 w-1/4 bg-muted animate-pulse rounded" />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : products && products.length > 0 ? (
        <>
          <p className="text-sm text-muted-foreground mb-4">
            {products.length} product(s) found
          </p>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {products.map((product) => (
              <div key={product.id} className="relative">
                <ProductCard
                  product={{
                    id: product.id,
                    name: product.name,
                    price: product.basePrice,
                    image: product.images?.[0],
                    category: product.category || "uncategorized",
                    isNew: true,
                  }}
                />
                <span className="absolute top-2 left-2 bg-primary text-primary-foreground text-xs font-bold px-2 py-1 rounded">
                  NEW
                </span>
              </div>
            ))}
          </div>
        </>
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <Sparkles className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
            <p className="text-muted-foreground">No new arrivals in this period</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
