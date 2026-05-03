"use client";

import { Search, ShoppingBag, Heart, Package, AlertCircle, FileX, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface EmptyStateProps {
  variant?: "no-results" | "no-orders" | "no-wishlist" | "no-cart" | "error" | "loading";
  title?: string;
  description?: string;
  actionLabel?: string;
  actionHref?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}

const variantConfig = {
  "no-results": {
    defaultTitle: "No results found",
    defaultDescription: "Try adjusting your search or filter criteria",
    defaultActionLabel: "Clear Filters",
    Icon: Search,
  },
  "no-orders": {
    defaultTitle: "No orders yet",
    defaultDescription: "When you place orders, they'll appear here",
    defaultActionLabel: "Start Shopping",
    Icon: Package,
  },
  "no-wishlist": {
    defaultTitle: "Wishlist is empty",
    defaultDescription: "Save items you love to your wishlist",
    defaultActionLabel: "Explore Products",
    Icon: Heart,
  },
  "no-cart": {
    defaultTitle: "Your cart is empty",
    defaultDescription: "Add some items to get started",
    defaultActionLabel: "Shop Now",
    Icon: ShoppingBag,
  },
  "error": {
    defaultTitle: "Something went wrong",
    defaultDescription: "An error occurred. Please try again.",
    defaultActionLabel: "Retry",
    Icon: AlertCircle,
  },
  "loading": {
    defaultTitle: "Loading...",
    defaultDescription: "Please wait while we fetch the data",
    defaultActionLabel: "",
    Icon: Loader2,
  },
};

export function EmptyState({
  variant = "no-results",
  title,
  description,
  actionLabel,
  actionHref,
  onAction,
  icon,
}: EmptyStateProps) {
  const config = variantConfig[variant];
  const Icon = config.Icon;

  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      {/* Icon */}
      <div className={`mb-6 ${
        variant === "error" ? "text-destructive" :
        variant === "loading" ? "text-primary animate-spin" :
        "text-muted-foreground"
      }`}>
        {icon || <Icon className="h-12 w-12" />}
      </div>

      {/* Title */}
      <h3 className="text-lg font-semibold mb-2">
        {title || config.defaultTitle}
      </h3>

      {/* Description */}
      <p className="text-sm text-muted-foreground max-w-sm mb-6">
        {description || config.defaultDescription}
      </p>

      {/* Action Button */}
      {actionLabel || config.defaultActionLabel ? (
        onAction ? (
          <Button onClick={onAction}>
            {variant === "loading" && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {actionLabel || config.defaultActionLabel}
          </Button>
        ) : actionHref ? (
          <Button asChild>
            <Link href={actionHref}>
              {actionLabel || config.defaultActionLabel}
            </Link>
          </Button>
        ) : null
      ) : null}
    </div>
  );
}
