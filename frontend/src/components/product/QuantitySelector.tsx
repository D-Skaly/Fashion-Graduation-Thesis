"use client";

import { Button } from "@/components/ui/button";
import { Minus, Plus } from "lucide-react";

interface QuantitySelectorProps {
    quantity: number;
    onQuantityChange: (quantity: number) => void;
    min?: number;
    max?: number;
}

export function QuantitySelector({ quantity, onQuantityChange, min = 1, max = 99 }: QuantitySelectorProps) {
    const handleDecrement = () => {
        if (quantity > min) {
            onQuantityChange(quantity - 1);
        }
    };

    const handleIncrement = () => {
        if (quantity < max) {
            onQuantityChange(quantity + 1);
        }
    };

    const handleDirectInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = parseInt(e.target.value);
        if (!isNaN(value) && value >= min && value <= max) {
            onQuantityChange(value);
        }
    };

    return (
        <div className="space-y-3">
            <span className="text-sm font-medium">Quantity</span>
            <div className="flex items-center gap-1 w-32">
                <Button 
                    variant="outline" 
                    size="icon" 
                    className="h-10 w-10"
                    disabled={quantity <= min}
                    onClick={handleDecrement}
                >
                    <Minus className="h-4 w-4" />
                </Button>
                <input
                    type="number"
                    min={min}
                    max={max}
                    value={quantity}
                    onChange={handleDirectInput}
                    className="flex-1 h-10 text-center border border-input rounded-md font-medium focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
                <Button 
                    variant="outline" 
                    size="icon" 
                    className="h-10 w-10"
                    disabled={quantity >= max}
                    onClick={handleIncrement}
                >
                    <Plus className="h-4 w-4" />
                </Button>
            </div>
        </div>
    );
}
