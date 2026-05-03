"use client";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";

interface ProductVariantSelectorProps {
  type: "color" | "size";
  label: string;
  selectedValue: string | null;
  options: string[];
  onSelect: (value: string) => void;
  selectedSize?: string | null;
  stockQuantity?: number;
}

export function ProductVariantSelector({
  type,
  label,
  selectedValue,
  options,
  onSelect,
  selectedSize,
  stockQuantity,
}: ProductVariantSelectorProps) {
  if (options.length === 0) return null;

  return (
    <div className="space-y-3">
      <div className="flex justify-between items-end">
        <span className="text-sm font-medium uppercase tracking-widest text-muted-foreground">
          Select {label} <span className="text-foreground ml-2">{selectedValue}</span>
        </span>
        {type === "size" && (
          <Button
            variant="link"
            className="text-xs underline decoration-muted-foreground underline-offset-4 text-muted-foreground hover:text-primary transition-colors p-0"
          >
            Size Guide
          </Button>
        )}
      </div>
      <div
        className={cn(
          "flex flex-wrap gap-3",
          type === "size" && "grid grid-cols-4 gap-2"
        )}
      >
        {options.map((option) => {
          const isSelected = selectedValue === option;
          const isSizeOutOfStock =
            type === "size" &&
            selectedSize === option &&
            stockQuantity !== undefined &&
            stockQuantity < 1;

          return (
            <button
              key={option}
              onClick={() => onSelect(option)}
              className={cn(
                "text-sm font-medium transition-all flex items-center justify-center uppercase tracking-wider",
                type === "color"
                  ? "h-10 px-4 border rounded-md"
                  : "h-12 border",
                isSelected
                  ? type === "color"
                    ? "border-primary bg-primary/5 ring-1 ring-primary"
                    : "border-foreground bg-foreground text-background"
                  : "border-border hover:border-foreground/50",
                isSizeOutOfStock && "opacity-50 cursor-not-allowed"
              )}
              disabled={isSizeOutOfStock}
            >
              {option}
            </button>
          );
        })}
      </div>
    </div>
  );
}