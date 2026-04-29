"use client";

import { useState, useEffect, useMemo } from "react";
import Link from "next/link";
import Image from "next/image";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
    ShoppingBag,
    X,
    Plus,
    Minus,
    ArrowRight,
    Loader2,
    ImageOff,
    Package,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
    SheetFooter,
    SheetDescription,
} from "@/components/ui/sheet";
import { Separator } from "@/components/ui/separator";
import { toast } from "sonner";
import api from "@/lib/axios";
import { cn } from "@/lib/utils";

// Types matching Backend DTOs
interface CartItem {
    id: string;
    productVariantId: string;
    productName: string;
    size: string;
    color: string;
    price: number;
    quantity: number;
    subtotal: number;
    productImage?: string;
}

interface Cart {
    id: string;
    items: CartItem[];
    totalAmount: number;
}

const fetchCart = async (): Promise<Cart> => {
    const { data } = await api.get("/cart");
    return data;
};

function CartItemImage({ image, name }: { image?: string; name: string }) {
    const [error, setError] = useState(false);

    if (!image || error) {
        return (
            <div className="w-20 h-24 rounded-md bg-secondary flex items-center justify-center flex-shrink-0">
                <ImageOff className="h-6 w-6 text-muted-foreground/30" />
            </div>
        );
    }

    return (
        <div className="w-20 h-24 rounded-md overflow-hidden flex-shrink-0 relative bg-secondary">
            <Image
                src={image}
                alt={name}
                fill
                className="object-cover"
                sizes="80px"
                onError={() => setError(true)}
            />
        </div>
    );
}

export function CartDrawer() {
    const [isOpen, setIsOpen] = useState(false);
    const queryClient = useQueryClient();

    const { data: cart, isLoading } = useQuery({
        queryKey: ["cart"],
        queryFn: fetchCart,
        retry: false,
    });

    const items = cart?.items || [];
    const totalAmount = cart?.totalAmount || 0;
    const itemCount = items.reduce((acc, item) => acc + item.quantity, 0);

    const freeShipThreshold = 300;
    const progress = useMemo(
        () => Math.min((totalAmount / freeShipThreshold) * 100, 100),
        [totalAmount]
    );
    const amountLeft = Math.max(freeShipThreshold - totalAmount, 0);

    const updateMutation = useMutation({
        mutationFn: async ({
            itemId,
            quantity,
        }: {
            itemId: string;
            quantity: number;
        }) => {
            await api.put("/cart/update", { cartItemId: itemId, quantity });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["cart"] });
        },
        onError: () => {
            toast.error("Failed to update quantity");
        },
    });

    const removeMutation = useMutation({
        mutationFn: async (itemId: string) => {
            await api.delete(`/cart/remove/${itemId}`);
        },
        onSuccess: () => {
            toast.success("Item removed from cart");
            queryClient.invalidateQueries({ queryKey: ["cart"] });
        },
        onError: () => {
            toast.error("Failed to remove item");
        },
    });

    const handleQuantityChange = (itemId: string, currentQty: number, delta: number) => {
        const newQty = currentQty + delta;
        if (newQty < 1) return;
        updateMutation.mutate({ itemId, quantity: newQty });
    };

    return (
        <Sheet open={isOpen} onOpenChange={setIsOpen}>
            <SheetTrigger asChild>
                <Button
                    variant="ghost"
                    size="icon"
                    className="relative hover:bg-background/20 hover:text-primary/70 rounded-full"
                >
                    <ShoppingBag className="h-5 w-5" />
                    {itemCount > 0 && (
                        <span
                            key={itemCount}
                            className="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-foreground text-[10px] text-background font-bold shadow-sm animate-badge-bounce"
                        >
                            {itemCount > 99 ? "99+" : itemCount}
                        </span>
                    )}
                    <span className="sr-only">Cart</span>
                </Button>
            </SheetTrigger>

            <SheetContent className="w-full sm:max-w-md flex flex-col p-0 border-l border-border bg-background">
                <SheetHeader className="p-6 border-b border-border/50">
                    <div className="flex items-center justify-between">
                        <SheetTitle className="text-xl font-black uppercase tracking-widest">
                            Your Cart
                        </SheetTitle>
                    </div>
                    <SheetDescription className="sr-only">
                        Review and manage items in your shopping cart
                    </SheetDescription>

                    {/* Freeship Progress */}
                    <div className="mt-4 bg-secondary/30 p-4 rounded-xl">
                        {amountLeft > 0 ? (
                            <p className="text-sm text-muted-foreground font-medium mb-3">
                                You&apos;re{" "}
                                <span className="text-foreground font-bold">
                                    ${amountLeft.toFixed(2)}
                                </span>{" "}
                                away from{" "}
                                <span className="font-bold text-foreground">
                                    Free Shipping!
                                </span>
                            </p>
                        ) : (
                            <p className="text-sm font-bold text-foreground mb-3 uppercase tracking-wide">
                                ✨ You&apos;ve unlocked Free Shipping!
                            </p>
                        )}
                        <div className="h-1.5 w-full bg-secondary rounded-full overflow-hidden">
                            <div
                                className="h-full bg-foreground transition-all duration-700 ease-out"
                                style={{ width: `${progress}%` }}
                            />
                        </div>
                    </div>
                </SheetHeader>

                <div className="flex-1 overflow-y-auto p-6 flex flex-col gap-6">
                    {isLoading ? (
                        <div className="flex flex-col items-center justify-center h-full text-center space-y-4">
                            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                            <p className="text-sm text-muted-foreground">Loading your cart...</p>
                        </div>
                    ) : items.length === 0 ? (
                        <div className="flex flex-col items-center justify-center h-full text-center space-y-4">
                            <div className="h-16 w-16 bg-secondary/50 rounded-full flex items-center justify-center">
                                <Package className="h-8 w-8 text-muted-foreground/40" />
                            </div>
                            <div>
                                <p className="font-semibold text-lg">Your cart is empty</p>
                                <p className="text-sm text-muted-foreground mt-1">
                                    Add some items to get started
                                </p>
                            </div>
                            <Button
                                variant="outline"
                                onClick={() => setIsOpen(false)}
                                asChild
                                className="rounded-full px-8"
                            >
                                <Link href="/shop">Continue Shopping</Link>
                            </Button>
                        </div>
                    ) : (
                        items.map((item) => (
                            <div
                                key={item.id}
                                className="flex gap-4 group"
                            >
                                <CartItemImage
                                    image={item.productImage}
                                    name={item.productName}
                                />
                                <div className="flex flex-col flex-1 py-1">
                                    <div className="flex justify-between items-start">
                                        <div className="flex-1 min-w-0">
                                            <Link
                                                href={`/product/${item.productVariantId}`}
                                                onClick={() => setIsOpen(false)}
                                                className="font-semibold text-sm line-clamp-1 hover:underline underline-offset-4 cursor-pointer"
                                            >
                                                {item.productName}
                                            </Link>
                                            <p className="text-xs text-muted-foreground mt-1">
                                                {item.color} / {item.size}
                                            </p>
                                        </div>
                                        <button
                                            className="text-muted-foreground hover:text-destructive transition-colors ml-2"
                                            onClick={() => removeMutation.mutate(item.id)}
                                            disabled={removeMutation.isPending}
                                        >
                                            {removeMutation.isPending ? (
                                                <Loader2 className="h-4 w-4 animate-spin" />
                                            ) : (
                                                <X className="h-4 w-4" />
                                            )}
                                        </button>
                                    </div>
                                    <div className="mt-auto flex items-center justify-between">
                                        <div className="flex items-center border border-border rounded-md h-8">
                                            <button
                                                className="px-2 h-full hover:bg-secondary/50 disabled:opacity-40 transition-colors"
                                                disabled={
                                                    updateMutation.isPending ||
                                                    item.quantity <= 1
                                                }
                                                onClick={() =>
                                                    handleQuantityChange(
                                                        item.id,
                                                        item.quantity,
                                                        -1
                                                    )
                                                }
                                            >
                                                <Minus className="h-3 w-3" />
                                            </button>
                                            <span className="w-8 text-center text-xs font-medium">
                                                {updateMutation.isPending
                                                    ? "..."
                                                    : item.quantity}
                                            </span>
                                            <button
                                                className="px-2 h-full hover:bg-secondary/50 disabled:opacity-40 transition-colors"
                                                disabled={updateMutation.isPending}
                                                onClick={() =>
                                                    handleQuantityChange(
                                                        item.id,
                                                        item.quantity,
                                                        1
                                                    )
                                                }
                                            >
                                                <Plus className="h-3 w-3" />
                                            </button>
                                        </div>
                                        <span className="font-bold text-sm">
                                            ${(item.price * item.quantity).toFixed(2)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {items.length > 0 && (
                    <div className="border-t border-border bg-secondary/10 p-6">
                        <div className="space-y-3 mb-6">
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground">Subtotal</span>
                                <span className="font-medium">
                                    ${totalAmount.toFixed(2)}
                                </span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground">Shipping</span>
                                <span className="font-medium">
                                    {amountLeft <= 0 ? (
                                        <span className="text-green-600 dark:text-green-400 font-semibold">
                                            Free
                                        </span>
                                    ) : (
                                        "Calculated at checkout"
                                    )}
                                </span>
                            </div>
                            <Separator className="my-2" />
                            <div className="flex justify-between text-lg font-bold">
                                <span>Total</span>
                                <span>${totalAmount.toFixed(2)}</span>
                            </div>
                        </div>

                        <Button
                            className="w-full h-14 text-base tracking-widest uppercase font-bold rounded-none group bg-foreground text-background hover:bg-foreground/90"
                            asChild
                        >
                            <Link href="/checkout" onClick={() => setIsOpen(false)}>
                                Checkout
                                <ArrowRight className="ml-2 h-5 w-5 transition-transform group-hover:translate-x-1" />
                            </Link>
                        </Button>
                        <Button
                            variant="ghost"
                            className="w-full mt-3 text-sm text-muted-foreground hover:text-foreground"
                            onClick={() => setIsOpen(false)}
                            asChild
                        >
                            <Link href="/cart">View Full Cart</Link>
                        </Button>
                    </div>
                )}
            </SheetContent>
        </Sheet>
    );
}
