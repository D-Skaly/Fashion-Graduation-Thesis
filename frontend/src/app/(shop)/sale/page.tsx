"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Tag, Percent, Gift, Loader2 } from "lucide-react";
import Image from "next/image";
import Link from "next/link";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import api from "@/lib/axios";

interface Promotion {
  id: string;
  code: string;
  discountType: "PERCENTAGE" | "FIXED_AMOUNT";
  discountValue: number;
  minOrderAmount?: number;
  expiryDate: string;
}

interface Product {
  id: string;
  name: string;
  basePrice: number;
  images?: string[];
  salePrice?: number;
  discountPercent?: number;
}

const fetchSaleProducts = async (): Promise<Product[]> => {
  const { data } = await api.get("/products", {
    params: { onSale: true, size: 20 },
  });
  return Array.isArray(data) ? data : data.content || [];
};

const fetchCoupons = async (): Promise<Promotion[]> => {
  const { data } = await api.get("/coupons/active");
  return Array.isArray(data) ? data : [];
};

export default function SalePage() {
  const [activeTab, setActiveTab] = useState("products");

  const { data: products, isLoading: productsLoading } = useQuery({
    queryKey: ["sale-products"],
    queryFn: fetchSaleProducts,
  });

  const { data: coupons, isLoading: couponsLoading } = useQuery({
    queryKey: ["active-coupons"],
    queryFn: fetchCoupons,
  });

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      {/* Header */}
      <div className="text-center mb-12">
        <h1 className="text-4xl md:text-5xl font-bold mb-4 flex items-center justify-center gap-3">
          <Percent className="h-8 w-8 text-primary" />
          Sale & Offers
        </h1>
        <p className="text-muted-foreground max-w-2xl mx-auto">
          Discover amazing deals on your favorite fashion items. Limited time offers available!
        </p>
      </div>

      {/* CTA Banner */}
      <Card className="mb-12 bg-primary text-primary-foreground border-0">
        <CardContent className="p-8 md:p-12">
          <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            <div>
              <h2 className="text-2xl md:text-3xl font-bold mb-2">Flash Sale!</h2>
              <p className="text-primary-foreground/80">
                Up to 50% off on select items. Use code FLASH50 at checkout.
              </p>
            </div>
            <Button size="lg" variant="secondary" asChild>
              <Link href="/shop?filter=sale">Shop Now</Link>
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Tabs */}
      <div className="flex gap-2 mb-8">
        <Button
          variant={activeTab === "products" ? "default" : "outline"}
          onClick={() => setActiveTab("products")}
        >
          <Tag className="mr-2 h-4 w-4" />
          Sale Products
        </Button>
        <Button
          variant={activeTab === "coupons" ? "default" : "outline"}
          onClick={() => setActiveTab("coupons")}
        >
          <Gift className="mr-2 h-4 w-4" />
          Coupon Codes
        </Button>
      </div>

      {/* Sale Products */}
      {activeTab === "products" && (
        <>
          {productsLoading ? (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="space-y-2">
                  <div className="aspect-square bg-muted animate-pulse rounded-xl" />
                  <div className="h-4 w-3/4 bg-muted animate-pulse rounded" />
                  <div className="h-4 w-1/2 bg-muted animate-pulse rounded" />
                </div>
              ))}
            </div>
          ) : products && products.length > 0 ? (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {products.map((product) => (
                <div key={product.id} className="relative group">
                  <Link href={`/product/${product.id}`}>
                    <div className="relative aspect-square overflow-hidden rounded-xl">
                      {product.images?.[0] ? (
                        <Image
                          src={product.images[0]}
                          alt={product.name}
                          fill
                          className="object-cover group-hover:scale-105 transition-transform duration-300"
                          sizes="(max-width: 768px) 50vw, (max-width: 1024px) 33vw, 25vw"
                        />
                      ) : (
                        <div className="w-full h-full bg-muted flex items-center justify-center">
                          <Tag className="h-8 w-8 text-muted-foreground" />
                        </div>
                      )}
                      {product.discountPercent && (
                        <Badge className="absolute top-2 right-2 bg-red-500 hover:bg-red-600">
                          -{product.discountPercent}%
                        </Badge>
                      )}
                    </div>
                    <div className="pt-3 space-y-1">
                      <h3 className="font-medium text-sm line-clamp-2">{product.name}</h3>
                      <div className="flex items-center gap-2">
                        {product.salePrice ? (
                          <>
                            <span className="font-bold">${product.salePrice.toFixed(2)}</span>
                            <span className="text-muted-foreground line-through text-sm">
                              ${product.basePrice.toFixed(2)}
                            </span>
                          </>
                        ) : (
                          <span className="font-bold">${product.basePrice.toFixed(2)}</span>
                        )}
                      </div>
                    </div>
                  </Link>
                </div>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <Tag className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">No sale products at the moment.</p>
              </CardContent>
            </Card>
          )}
        </>
      )}

      {/* Coupon Codes */}
      {activeTab === "coupons" && (
        <>
          {couponsLoading ? (
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {Array.from({ length: 3 }).map((_, i) => (
                <div key={i} className="h-32 bg-muted animate-pulse rounded-xl" />
              ))}
            </div>
          ) : coupons && coupons.length > 0 ? (
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {coupons.map((coupon) => (
                <Card key={coupon.id} className="overflow-hidden">
                  <CardContent className="p-6">
                    <div className="flex items-start justify-between mb-4">
                      <Badge variant="secondary" className="text-lg font-mono font-bold px-3 py-1">
                        {coupon.code}
                      </Badge>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => {
                          navigator.clipboard.writeText(coupon.code);
                        }}
                      >
                        Copy
                      </Button>
                    </div>
                    <p className="font-medium mb-2">
                      {coupon.discountType === "PERCENTAGE"
                        ? `${coupon.discountValue}% OFF`
                        : `$${coupon.discountValue} OFF`}
                    </p>
                    {coupon.minOrderAmount && (
                      <p className="text-sm text-muted-foreground">
                        Min. order: ${coupon.minOrderAmount}
                      </p>
                    )}
                    <p className="text-xs text-muted-foreground mt-2">
                      Expires: {new Date(coupon.expiryDate).toLocaleDateString()}
                    </p>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <Gift className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">No active coupon codes at the moment.</p>
              </CardContent>
            </Card>
          )}
        </>
      )}

      {/* Info */}
      <Card className="mt-12 bg-secondary/20">
        <CardContent className="pt-6">
          <h3 className="font-bold mb-2">Sale Terms</h3>
          <ul className="space-y-1 text-sm text-muted-foreground">
            <li>• Sale prices are valid while supplies last</li>
            <li>• Cannot be combined with other offers</li>
            <li>• Final sale items cannot be returned</li>
            <li>• Fashion Store reserves the right to modify or cancel promotions</li>
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
