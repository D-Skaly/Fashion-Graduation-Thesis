"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Heart, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import api from "@/lib/axios";
import { cn } from "@/lib/utils";

interface WishlistButtonProps {
    productId: string;
    variant?: "default" | "ghost" | "outline";
    size?: "default" | "sm" | "icon";
    className?: string;
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

export function WishlistButton({ productId, variant = "ghost", size = "icon", className }: WishlistButtonProps) {
    const queryClient = useQueryClient();
    const [optimisticAdded, setOptimisticAdded] = useState(false);

    const { data: isInWishlist = false, isLoading } = useQuery({
        queryKey: ["wishlist", productId],
        queryFn: () => checkWishlist(productId),
        retry: false,
    });

    const mutation = useMutation({
        mutationFn: () => toggleWishlist(productId),
        onSuccess: (data) => {
            queryClient.invalidateQueries({ queryKey: ["wishlist"] });
            queryClient.invalidateQueries({ queryKey: ["wishlist", productId] });
            queryClient.invalidateQueries({ queryKey: ["wishlist-count"] });
            
            if (data.added) {
                toast.success("Added to wishlist", { description: "Product has been saved" });
            } else {
                toast.info("Removed from wishlist", { description: "Product has been removed" });
            }
            
            setOptimisticAdded(false);
        },
        onError: () => {
            setOptimisticAdded(false);
            toast.error("Error", { description: "Unable to perform action. Please try again." });
        },
    });

    const handleToggle = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();
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
            className={cn(
                "transition-all duration-300 rounded-full",
                isAdded ? "text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950/30" : "hover:bg-secondary",
                className
            )}
        >
            {mutation.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
                <Heart className={cn("h-4 w-4 transition-all duration-300", isAdded && "fill-current scale-110")} />
            )}
            <span className="sr-only">
                {isAdded ? "Remove from wishlist" : "Add to wishlist"}
            </span>
        </Button>
    );
}
