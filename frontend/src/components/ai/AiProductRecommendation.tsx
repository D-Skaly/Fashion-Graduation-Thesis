"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Sparkles, Loader2, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/components/ui/use-toast";
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
    return data;
};

export function AiProductRecommendation() {
    const { toast } = useToast();
    const [isRefreshing, setIsRefreshing] = useState(false);

    const { data: products, isLoading, refetch } = useQuery({
        queryKey: ["ai-recommendations"],
        queryFn: fetchRecommendations,
    });

    const handleRefresh = async () => {
        setIsRefreshing(true);
        try {
            await refetch();
            toast({
                title: "Đã cập nhật gợi ý",
                description: "Đề xuất sản phẩm mới dựa trên sở thích của bạn.",
            });
        } catch {
            toast({
                variant: "destructive",
                title: "Lỗi",
                description: "Không thể cập nhật gợi ý.",
            });
        } finally {
            setIsRefreshing(false);
        }
    };

    if (isLoading) {
        return (
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Sparkles className="h-5 w-5 text-primary" />
                        Gợi ý cho bạn
                    </CardTitle>
                </CardHeader>
                <CardContent className="flex items-center justify-center py-8">
                    <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                </CardContent>
            </Card>
        );
    }

    if (!products || products.length === 0) {
        return null;
    }

    return (
        <Card>
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle className="flex items-center gap-2">
                        <Sparkles className="h-5 w-5 text-primary" />
                        Gợi ý AI cho bạn
                    </CardTitle>
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={handleRefresh}
                        disabled={isRefreshing}
                    >
                        {isRefreshing ? (
                            <Loader2 className="h-4 w-4 animate-spin" />
                        ) : (
                            <Sparkles className="h-4 w-4" />
                        )}
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {products.slice(0, 3).map((product) => (
                        <Link
                            key={product.id}
                            href={`/product/${product.id}`}
                            className="block group"
                        >
                            <div className="flex gap-4 p-3 rounded-lg hover:bg-secondary/50 transition-colors">
                                <div className="relative w-20 h-20 flex-shrink-0 rounded-md overflow-hidden bg-muted">
                                    {product.images[0] ? (
                                        <Image
                                            src={product.images[0]}
                                            alt={product.name}
                                            fill
                                            className="object-cover"
                                        />
                                    ) : (
                                        <div className="flex items-center justify-center h-full text-muted-foreground text-xs">
                                            Không có ảnh
                                        </div>
                                    )}
                                </div>
                                
                                <div className="flex-1 min-w-0">
                                    <Badge variant="outline" className="mb-1 text-xs">
                                        {product.categoryName}
                                    </Badge>
                                    <h4 className="font-medium line-clamp-1 group-hover:underline">
                                        {product.name}
                                    </h4>
                                    <p className="text-sm text-muted-foreground line-clamp-1 mt-1">
                                        {product.reason}
                                    </p>
                                    <div className="flex items-center justify-between mt-2">
                                        <span className="font-bold">
                                            ${product.basePrice.toLocaleString()}
                                        </span>
                                        <ArrowRight className="h-4 w-4 text-muted-foreground group-hover:text-foreground transition-colors" />
                                    </div>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>
            </CardContent>
        </Card>
    );
}
