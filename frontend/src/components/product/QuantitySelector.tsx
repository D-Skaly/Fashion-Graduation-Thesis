"use client";

import { Minus, Plus } from "lucide-react";

interface QuantitySelectorProps {
  quantity: number;
  onQuantityChange: (newQuantity: number) => void;
  min?: number;
  max?: number;
}

export function QuantitySelector({
  quantity,
  onQuantityChange,
  min = 1,
  max = 99,
}: QuantitySelectorProps) {
  const decrement = () => {
    if (quantity > min) {
      onQuantityChange(quantity - 1);
    }
  };

  const increment = () => {
    if (quantity < max) {
      onQuantityChange(quantity + 1);
    }
  };

  return (
    <div className="space-y-3 pt-2">
      <div className="flex items-center border border-border w-max h-12">
        <button
          className="px-4 h-full hover:bg-secondary/50 transition-colors disabled:opacity-50"
          onClick={decrement}
          disabled={quantity <= min}
          aria-label="Decrease quantity"
        >
          <Minus className="h-4 w-4" />
        </button>
        <span className="w-12 text-center font-medium">{quantity}</span>
        <button
          className="px-4 h-full hover:bg-secondary/50 transition-colors disabled:opacity-50"
          onClick={increment}
          disabled={quantity >= max}
          aria-label="Increase quantity"
        >
          <Plus className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
}