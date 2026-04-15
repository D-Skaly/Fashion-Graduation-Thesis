"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Heart, HeartOff, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ui/use-toast";
import api from "@/lib/axios";

interface WishlistButtonProps {
    productId: string;
    variant?: "default" | "ghost" | "outline";
    size?: "default" | "sm" | "icon";
}

const checkWishlist = async (productId: string) => {
    try {
        const { data } = await api.get(`/wishlist/check/${productId}`);
        return data.inWishlist;
    } catch {
        return false;
    }
};

const toggleWishlist = async (productId: string) => {
    const { data } = await api.post(`/wishlist/toggle/${productId}`);
    return data;
};

export function WishlistButton({ productId, variant = "ghost", size = "icon" }: WishlistButtonProps) {
    const queryClient = useQueryClient();
    const { toast } = useToast();
    const [optimisticAdded, setOptimisticAdded] = useState(false);

    const { data: isInWishlist = false, isLoading } = useQuery({
        queryKey: ["wishlist", productId],
        queryFn: () => checkWishlist(productId),
    });

    const mutation = useMutation({
        mutationFn: () => toggleWishlist(productId),
        onSuccess: (data) => {
            queryClient.invalidateQueries({ queryKey: ["wishlist"] });
            queryClient.invalidateQueries({ queryKey: ["wishlist", productId] });
            queryClient.invalidateQueries({ queryKey: ["wishlist-count"] });
            
            toast({
                title: data.added ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích",
                description: data.added ? "Sản phẩm đã được lưu" : "Sản phẩm đã bị xóa",
            });
            
            setOptimisticAdded(false);
        },
        onError: () => {
            setOptimisticAdded(false);
            toast({
                variant: "destructive",
                title: "Lỗi",
                description: "Không thể thực hiện thao tác. Vui lòng thử lại.",
            });
        },
    });

    const handleToggle = () => {
        setOptimisticAdded(!isInWishlist);
        mutation.mutate();
    };

    const isAdded = optimisticAdded !== false ? optimisticAdded : isInWishlist;

    return (
        <Button
            variant={variant}
            size={size}
            onClick={handleToggle}
            disabled={isLoading || mutation.isPending}
            className={isAdded ? "text-red-500 hover:text-red-600" : ""}
        >
            {mutation.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
            ) : isAdded ? (
                <Heart className="h-4 w-4 fill-current" />
            ) : (
                <HeartOff className="h-4 w-4" />
            )}
            {size !== "icon" && (
                <span className="ml-2">{isAdded ? "Đã lưu" : "Lưu"}</span>
            )}
        </Button>
    );
}
