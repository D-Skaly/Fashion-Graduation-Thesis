"use client";

import { useQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Heart, ShoppingBag } from "lucide-react";
import Link from "next/link";
import Image from "next/image";
import api from "@/lib/axios";
import { WishlistButton } from "@/components/wishlist/WishlistButton";
import { Skeleton } from "@/components/ui/skeleton";

interface WishlistItem {
    id: string;
    product: {
        id: string;
        name: string;
        basePrice: number;
        images: string[];
        categoryName: string;
    };
    addedAt: string;
}

const fetchWishlist = async (): Promise<WishlistItem[]> => {
    const { data } = await api.get("/wishlist");
    return data;
};

export default function WishlistPage() {
    const { data: wishlist, isLoading } = useQuery({
        queryKey: ["wishlist"],
        queryFn: fetchWishlist,
    });

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight">Danh sách yêu thích</h1>
                    <p className="text-muted-foreground">
                        {wishlist?.length || 0} sản phẩm
                    </p>
                </div>
            </div>

            {isLoading ? (
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                    {Array.from({ length: 6 }).map((_, i) => (
                        <Card key={i}>
                            <CardContent className="p-4">
                                <Skeleton className="h-48 w-full mb-4" />
                                <Skeleton className="h-4 w-3/4 mb-2" />
                                <Skeleton className="h-4 w-1/2" />
                            </CardContent>
                        </Card>
                    ))}
                </div>
            ) : wishlist && wishlist.length > 0 ? (
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                    {wishlist.map((item) => (
                        <Card key={item.id}>
                            <CardContent className="p-4">
                                <div className="relative aspect-square mb-4 rounded-lg overflow-hidden bg-muted">
                                    {item.product.images[0] ? (
                                        <Image
                                            src={item.product.images[0]}
                                            alt={item.product.name}
                                            fill
                                            className="object-cover"
                                        />
                                    ) : (
                                        <div className="flex items-center justify-center h-full text-muted-foreground">
                                            Không có hình ảnh
                                        </div>
                                    )}
                                </div>
                                
                                <div className="space-y-2">
                                    <div className="text-xs text-muted-foreground">
                                        {item.product.categoryName}
                                    </div>
                                    <Link 
                                        href={`/product/${item.product.id}`}
                                        className="font-medium hover:underline line-clamp-2"
                                    >
                                        {item.product.name}
                                    </Link>
                                    <div className="text-lg font-bold">
                                        ${item.product.basePrice.toLocaleString()}
                                    </div>
                                </div>

                                <div className="flex gap-2 mt-4">
                                    <Button className="flex-1" asChild>
                                        <Link href={`/product/${item.product.id}`}>
                                            <ShoppingBag className="h-4 w-4 mr-2" />
                                            Thêm vào giỏ
                                        </Link>
                                    </Button>
                                    <WishlistButton productId={item.product.id} size="icon" />
                                </div>

                                <div className="text-xs text-muted-foreground mt-3">
                                    Đã lưu: {new Date(item.addedAt).toLocaleDateString()}
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            ) : (
                <Card>
                    <CardContent className="flex flex-col items-center justify-center py-16">
                        <Heart className="h-16 w-16 text-muted-foreground mb-4" />
                        <h3 className="text-lg font-semibold mb-2">Danh sách yêu thích trống</h3>
                        <p className="text-muted-foreground mb-4 text-center">
                            Bạn chưa có sản phẩm nào trong danh sách yêu thích.
                        </p>
                        <Button asChild>
                            <Link href="/shop">Khám phá sản phẩm</Link>
                        </Button>
                    </CardContent>
                </Card>
            )}
        </div>
    );
}
