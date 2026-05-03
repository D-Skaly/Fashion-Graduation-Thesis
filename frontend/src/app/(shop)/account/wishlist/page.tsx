"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, Grid3x3, List, Trash2, ShoppingCart, Share2, Heart } from "lucide-react";
import Image from "next/image";
import Link from "next/link";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import api from "@/lib/axios";
import { ProductSummary } from "@/lib/apiService";

interface WishlistItem extends ProductSummary {
  addedAt?: string;
}

const fetchWishlist = async (): Promise<WishlistItem[]> => {
  const { data } = await api.get("/wishlist");
  return Array.isArray(data) ? data : [];
};

export default function WishlistPage() {
  const queryClient = useQueryClient();
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");

  const { data: wishlist, isLoading } = useQuery({
    queryKey: ["wishlist"],
    queryFn: fetchWishlist,
  });

  const removeMutation = useMutation({
    mutationFn: async (productId: string) => {
      await api.delete(`/wishlist/remove/${productId}`);
    },
    onSuccess: () => {
      toast.success("Removed from wishlist");
      queryClient.invalidateQueries({ queryKey: ["wishlist"] });
    },
    onError: () => toast.error("Failed to remove item"),
  });

  const addToCartMutation = useMutation({
    mutationFn: async (productId: string) => {
      await api.post("/cart/add", { productId, quantity: 1 });
    },
    onSuccess: () => {
      toast.success("Added to cart!");
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
    onError: () => toast.error("Failed to add to cart"),
  });

  const handleShare = () => {
    const shareData = {
      title: "My Wishlist",
      text: "Check out my wishlist!",
      url: window.location.href,
    };

    if (navigator.share) {
      navigator.share(shareData).catch(() => {});
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast.success("Link copied to clipboard!");
    }
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-6xl">
        <div className="flex items-center justify-between mb-8">
          <Skeleton className="h-8 w-48" />
          <Skeleton className="h-10 w-32" />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <Skeleton key={i} className="h-80 rounded-xl" />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold">My Wishlist</h1>
          <p className="text-muted-foreground text-sm mt-1">
            {wishlist?.length || 0} item(s) saved
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={handleShare}
            className="text-muted-foreground hover:text-foreground"
          >
            <Share2 className="h-4 w-4" />
          </Button>
          <div className="border rounded-lg flex">
            <Button
              variant={viewMode === "grid" ? "default" : "ghost"}
              size="icon"
              onClick={() => setViewMode("grid")}
              className="rounded-r-none"
            >
              <Grid3x3 className="h-4 w-4" />
            </Button>
            <Button
              variant={viewMode === "list" ? "default" : "ghost"}
              size="icon"
              onClick={() => setViewMode("list")}
              className="rounded-l-none"
            >
              <List className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>

      {wishlist && wishlist.length > 0 ? (
        viewMode === "grid" ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {wishlist.map((item) => (
              <Card key={item.id} className="overflow-hidden group">
                <div className="relative aspect-square">
                  {item.image ? (
                    <Link href={`/product/${item.id}`}>
                      <Image
                        src={item.image}
                        alt={item.name}
                        fill
                        className="object-cover group-hover:scale-105 transition-transform duration-300"
                        sizes="(max-width: 768px) 50vw, (max-width: 1024px) 33vw, 25vw"
                      />
                    </Link>
                  ) : (
                    <div className="w-full h-full bg-muted flex items-center justify-center">
                      <Heart className="h-8 w-8 text-muted-foreground" />
                    </div>
                  )}
                  <Button
                    variant="destructive"
                    size="icon"
                    className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity h-8 w-8"
                    onClick={() => removeMutation.mutate(item.id)}
                    disabled={removeMutation.isPending}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
                <CardContent className="p-4">
                  <Link href={`/product/${item.id}`}>
                    <h3 className="font-medium text-sm line-clamp-2 hover:underline">
                      {item.name}
                    </h3>
                  </Link>
                  <div className="flex items-center justify-between mt-2">
                    <span className="font-bold">
                      ${item.price?.toFixed(2)}
                      {item.salePrice && (
                        <span className="text-muted-foreground line-through text-xs ml-2">
                          ${item.salePrice.toFixed(2)}
                        </span>
                      )}
                    </span>
                    <Button
                      size="sm"
                      onClick={() => addToCartMutation.mutate(item.id)}
                      disabled={addToCartMutation.isPending}
                    >
                      <ShoppingCart className="h-4 w-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {wishlist.map((item) => (
              <Card key={item.id}>
                <CardContent className="p-4 flex gap-4">
                  <div className="relative h-24 w-24 shrink-0">
                    {item.image ? (
                      <Link href={`/product/${item.id}`}>
                        <Image
                          src={item.image}
                          alt={item.name}
                          fill
                          className="object-cover rounded-lg"
                          sizes="96px"
                        />
                      </Link>
                    ) : (
                      <div className="w-full h-full bg-muted rounded-lg flex items-center justify-center">
                        <Heart className="h-6 w-6 text-muted-foreground" />
                      </div>
                    )}
                  </div>
                  <div className="flex-1 flex items-center justify-between">
                    <div>
                      <Link href={`/product/${item.id}`}>
                        <h3 className="font-medium hover:underline">{item.name}</h3>
                      </Link>
                      <p className="text-sm text-muted-foreground">
                        Added {item.addedAt ? new Date(item.addedAt).toLocaleDateString() : ""}
                      </p>
                      <div className="flex items-center gap-2 mt-1">
                        <span className="font-bold">
                          ${item.price?.toFixed(2)}
                        </span>
                        {item.salePrice && (
                          <span className="text-muted-foreground line-through text-sm">
                            ${item.salePrice.toFixed(2)}
                          </span>
                        )}
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button
                        onClick={() => addToCartMutation.mutate(item.id)}
                        disabled={addToCartMutation.isPending}
                      >
                        <ShoppingCart className="h-4 w-4 mr-2" />
                        Add to Cart
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => removeMutation.mutate(item.id)}
                        disabled={removeMutation.isPending}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )
      ) : (
        <Card>
          <CardContent className="py-12 text-center">
            <Heart className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
            <p className="text-muted-foreground">Your wishlist is empty</p>
            <Button asChild className="mt-4">
              <Link href="/shop">Explore Products</Link>
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
