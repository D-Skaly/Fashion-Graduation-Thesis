"use client";

import { Button } from "@/components/ui/button";
import { ShoppingBag, Loader2, Check } from "lucide-react";
import { cn } from "@/lib/utils";

interface AddToCartButtonProps {
    isPending?: boolean;
    isSuccess?: boolean;
    disabled?: boolean;
    outOfStock?: boolean;
    onClick?: () => void;
    className?: string;
}

export function AddToCartButton({
    isPending = false,
    isSuccess = false,
    disabled = false,
    outOfStock = false,
    onClick,
    className
}: AddToCartButtonProps) {
    return (
        <Button
            size="lg"
            className={cn(
                "flex-1 text-base h-12 transition-all duration-300",
                isSuccess && "bg-green-600 hover:bg-green-700",
                className
            )}
            disabled={disabled || isPending || outOfStock}
            onClick={onClick}
        >
            {isPending ? (
                <>
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                    Adding...
                </>
            ) : isSuccess ? (
                <>
                    <Check className="mr-2 h-5 w-5" />
                    Added to Cart
                </>
            ) : outOfStock ? (
                "Out of Stock"
            ) : (
                <>
                    <ShoppingBag className="mr-2 h-5 w-5" />
                    Add to Cart
                </>
            )}
        </Button>
    );
}
