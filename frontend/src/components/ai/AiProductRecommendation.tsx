"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Sparkles, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import Link from "next/link";
import Image from "next/image";
import api from "@/lib/axios";

interface RecommendedProduct {
    id: string;
    name: string;
    basePrice: number;
    images: string[];
    categoryName: string;
    reason: string;
}

const fetchRecommendations = async (): Promise<RecommendedProduct[]> => {
    const { data } = await api.get("/ai/recommendations");
    return data.data || data; // handle api response format
};

export function AiProductRecommendation() {
    const [isRefreshing, setIsRefreshing] = useState(false);

    const { data: products, isLoading, refetch } = useQuery({
        queryKey: ["ai-recommendations"],
        queryFn: fetchRecommendations,
    });

    const handleRefresh = async () => {
        setIsRefreshing(true);
        try {
            await refetch();
            toast.success("AI has updated your recommendations.");
        } catch {
            toast.error("Failed to update recommendations.");
        } finally {
            setIsRefreshing(false);
        }
    };

    if (isLoading) {
        return (
            <div className="mt-32 pt-16 border-t border-border">
                <div className="flex items-center gap-4 mb-10">
                    <Loader2 className="h-8 w-8 animate-spin text-primary" />
                    <h2 className="text-2xl md:text-3xl font-black tracking-tight uppercase">Curating Your Style...</h2>
                </div>
            </div>
        );
    }

    if (!products || products.length === 0) {
        return null;
    }

    return (
        <div className="mt-32 pt-16 border-t border-border">
            <div className="flex flex-col md:flex-row justify-between items-end mb-10 gap-4">
                <div>
                    <div className="inline-flex items-center gap-2 px-3 py-1 mb-4 rounded-full bg-primary/5 border border-primary/10">
                        <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                        <span className="text-xs font-bold tracking-widest uppercase text-primary">AI Stylist</span>
                    </div>
                    <h2 className="text-3xl md:text-4xl font-black tracking-tight uppercase">Complete The Look</h2>
                </div>
                <Button 
                    variant="ghost" 
                    className="tracking-widest uppercase text-xs font-bold"
                    onClick={handleRefresh}
                    disabled={isRefreshing}
                >
                    {isRefreshing ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <Sparkles className="h-4 w-4 mr-2" />}
                    Refresh
                </Button>
            </div>
            
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-8">
                {products.slice(0, 4).map((product) => (
                    <Link key={product.id} href={`/product/${product.id}`} className="group cursor-pointer block">
                        <div className="aspect-[3/4] bg-secondary/20 mb-4 overflow-hidden relative border border-white/5 rounded-sm">
                            {product.images && product.images[0] ? (
                                <Image
                                    src={product.images[0]}
                                    alt={product.name}
                                    fill
                                    className="object-cover transition-transform duration-700 group-hover:scale-105"
                                />
                            ) : (
                                <div className="absolute inset-0 bg-stone-100 dark:bg-stone-900 transition-transform duration-700 group-hover:scale-105 flex items-center justify-center text-muted-foreground text-xs uppercase tracking-widest">
                                    No Image
                                </div>
                            )}
                            <div className="absolute inset-x-0 bottom-0 p-3 bg-gradient-to-t from-black/80 to-transparent translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                                <p className="text-xs text-white/90 line-clamp-2">{product.reason}</p>
                            </div>
                        </div>
                        <h3 className="text-sm font-bold uppercase tracking-wider mb-1 line-clamp-1 group-hover:underline underline-offset-4">
                            {product.name}
                        </h3>
                        <p className="text-sm text-muted-foreground">
                            ${product.basePrice.toLocaleString()}
                        </p>
                    </Link>
                ))}
            </div>
        </div>
    );
}
