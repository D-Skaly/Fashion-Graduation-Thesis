"use client";

import { ArrowRight } from "lucide-react";
import Link from "next/link";

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
import { useProducts } from "@/hooks/useProducts";

interface DisplayProduct {
    id: string;
    name: string;
    price: number;
    category: string;
    image?: string;
    isNew: boolean;
}

export function FeaturedProducts() {
    const { data: products, isLoading, isError } = useProducts();

    const placeholders = Array.from({ length: 4 });

    return (
        <section className="container mx-auto px-4 py-16 md:py-24">
            <div className="flex flex-col md:flex-row items-end justify-between gap-6 mb-12">
                <div className="space-y-4 max-w-2xl">
                    <div className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 border-transparent bg-secondary text-secondary-foreground">
                        Season Highlights
                    </div>
                    <h2 className="text-4xl md:text-5xl font-black tracking-tight uppercase">Featured Collection</h2>
                    <p className="text-muted-foreground text-lg leading-relaxed font-light">
                        Handpicked essentials for your seasonal wardrobe, curated just for you.
                    </p>
                </div>
                <Button variant="outline" className="gap-2 group rounded-full px-6 h-12" asChild>
                    <Link href="/shop">
                        View All <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
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
                        loop: products && products.length > 4,
                    }}
                    className="w-full"
                >
                    <CarouselContent className="-ml-4">
                        {products?.map((product: DisplayProduct) => (
                            <CarouselItem key={product.id} className="pl-4 md:basis-1/2 lg:basis-1/4">
                                <ProductCard product={product} />
                            </CarouselItem>
                        ))}
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
