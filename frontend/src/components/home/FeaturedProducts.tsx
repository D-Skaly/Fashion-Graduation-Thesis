"use client";

import { useQuery } from "@tanstack/react-query";
import { ArrowRight, Loader2 } from "lucide-react";
import Link from "next/link";

import api from "@/lib/axios";
import { ProductCard } from "@/components/product/ProductCard";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
} from "@/components/ui/carousel";

const fetchProducts = async () => {
    const { data } = await api.get("/products?size=8");
    return data.data.content; // Accessing the content from Page object
};

export function FeaturedProducts() {
    const { data: products, isLoading, isError } = useQuery({
        queryKey: ["featured-products"],
        queryFn: fetchProducts,
    });

    const placeholders = Array.from({ length: 4 });
    const colors = ["bg-slate-200", "bg-blue-100", "bg-stone-200", "bg-gray-100", "bg-orange-100", "bg-amber-100", "bg-emerald-100", "bg-yellow-100"];

    return (
        <section className="container mx-auto px-4 py-16 md:py-24">
            <div className="flex flex-col md:flex-row items-end justify-between gap-4 mb-10">
                <div className="space-y-2">
                    <h2 className="text-3xl md:text-4xl font-bold tracking-tight">Featured Collection</h2>
                    <p className="text-muted-foreground text-lg max-w-[600px]">
                        Handpicked essentials for your seasonal wardrobe.
                    </p>
                </div>
                <Button variant="ghost" className="gap-2 group" asChild>
                    <Link href="/shop">
                        View All Products <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
                    </Link>
                </Button>
            </div>

            {isLoading ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    {placeholders.map((_, i) => (
                        <div key={i} className="space-y-4">
                            <Skeleton className="aspect-[3/4] w-full rounded-xl" />
                            <div className="space-y-2">
                                <Skeleton className="h-4 w-1/4" />
                                <Skeleton className="h-6 w-3/4" />
                                <Skeleton className="h-6 w-1/2" />
                            </div>
                        </div>
                    ))}
                </div>
            ) : isError ? (
                <div className="py-20 text-center border rounded-xl bg-muted/20">
                    <p className="text-muted-foreground">Failed to load collection. Please try again later.</p>
                </div>
            ) : (
                <Carousel
                    opts={{
                        align: "start",
                        loop: products?.length > 4,
                    }}
                    className="w-full"
                >
                    <CarouselContent className="-ml-4">
                        {products?.map((serverProduct: any, index: number) => {
                            const product = {
                                id: serverProduct.id,
                                name: serverProduct.name,
                                price: serverProduct.basePrice,
                                category: serverProduct.categoryName,
                                image: colors[index % colors.length],
                                isNew: index < 2
                            };
                            return (
                                <CarouselItem key={product.id} className="pl-4 md:basis-1/2 lg:basis-1/4">
                                    <ProductCard product={product} />
                                </CarouselItem>
                            );
                        })}
                    </CarouselContent>
                    <div className="flex justify-end gap-2 mt-6">
                        <CarouselPrevious className="relative left-0 top-0 translate-y-0 h-10 w-10 border-primary/20 hover:bg-primary hover:text-primary-foreground" />
                        <CarouselNext className="relative right-0 top-0 translate-y-0 h-10 w-10 border-primary/20 hover:bg-primary hover:text-primary-foreground" />
                    </div>
                </Carousel>
            )}
        </section>
    );
}

