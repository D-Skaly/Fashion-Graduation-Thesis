"use client";

import { useState } from "react";
import { Ruler } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { cn } from "@/lib/utils";

interface SizeSelectorProps {
    sizes: string[];
    selectedSize: string | null;
    onSelectSize: (size: string) => void;
    stockMap?: Record<string, number>; // size -> stock quantity
}

export function SizeSelector({ sizes, selectedSize, onSelectSize, stockMap }: SizeSelectorProps) {
    const [showSizeGuide, setShowSizeGuide] = useState(false);

    const getStockStatus = (size: string) => {
        if (!stockMap) return "available";
        const stock = stockMap[size];
        if (stock === 0) return "out";
        if (stock < 5) return "low";
        return "available";
    };

    const isSizeAvailable = (size: string) => {
        return getStockStatus(size) !== "out";
    };

    return (
        <div className="space-y-3">
            <div className="flex justify-between items-center">
                <span className="text-sm font-medium">
                    Size: <span className="text-muted-foreground">{selectedSize || "Select a size"}</span>
                </span>
                <Dialog open={showSizeGuide} onOpenChange={setShowSizeGuide}>
                    <DialogTrigger asChild>
                        <button className="text-xs underline text-muted-foreground hover:text-primary flex items-center gap-1">
                            <Ruler className="h-3 w-3" />
                            Size Guide
                        </button>
                    </DialogTrigger>
                    <DialogContent className="max-w-2xl">
                        <DialogHeader>
                            <DialogTitle>Size Guide</DialogTitle>
                        </DialogHeader>
                        <div className="space-y-4">
                            <p className="text-sm text-muted-foreground">
                                Use the chart below to find your perfect fit.
                            </p>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm">
                                    <thead>
                                        <tr className="border-b">
                                            <th className="text-left py-2 px-3">Size</th>
                                            <th className="text-left py-2 px-3">Chest (in)</th>
                                            <th className="text-left py-2 px-3">Waist (in)</th>
                                            <th className="text-left py-2 px-3">Hip (in)</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr className="border-b">
                                            <td className="py-2 px-3 font-medium">XS</td>
                                            <td className="py-2 px-3">34-36</td>
                                            <td className="py-2 px-3">28-30</td>
                                            <td className="py-2 px-3">34-36</td>
                                        </tr>
                                        <tr className="border-b">
                                            <td className="py-2 px-3 font-medium">S</td>
                                            <td className="py-2 px-3">36-38</td>
                                            <td className="py-2 px-3">30-32</td>
                                            <td className="py-2 px-3">36-38</td>
                                        </tr>
                                        <tr className="border-b">
                                            <td className="py-2 px-3 font-medium">M</td>
                                            <td className="py-2 px-3">38-40</td>
                                            <td className="py-2 px-3">32-34</td>
                                            <td className="py-2 px-3">38-40</td>
                                        </tr>
                                        <tr className="border-b">
                                            <td className="py-2 px-3 font-medium">L</td>
                                            <td className="py-2 px-3">40-42</td>
                                            <td className="py-2 px-3">34-36</td>
                                            <td className="py-2 px-3">40-42</td>
                                        </tr>
                                        <tr>
                                            <td className="py-2 px-3 font-medium">XL</td>
                                            <td className="py-2 px-3">42-44</td>
                                            <td className="py-2 px-3">36-38</td>
                                            <td className="py-2 px-3">42-44</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <p className="text-xs text-muted-foreground">
                                * Measurements are in inches. For accurate results, measure yourself directly on skin.
                            </p>
                        </div>
                    </DialogContent>
                </Dialog>
            </div>
            
            <div className="flex flex-wrap gap-3">
                {sizes.map((size) => {
                    const stockStatus = getStockStatus(size);
                    const isAvailable = isSizeAvailable(size);
                    
                    return (
                        <button
                            key={size}
                            onClick={() => isAvailable && onSelectSize(size)}
                            disabled={!isAvailable}
                            className={cn(
                                "h-10 w-12 rounded-md border text-sm font-medium transition-all flex items-center justify-center relative",
                                selectedSize === size
                                    ? "border-primary bg-black text-white"
                                    : isAvailable
                                        ? "border-input hover:border-black"
                                        : "border-border bg-muted opacity-50 cursor-not-allowed"
                            )}
                            title={stockStatus === "out" ? "Out of stock" : stockStatus === "low" ? "Low stock" : "In stock"}
                        >
                            {size}
                            {stockStatus === "low" && (
                                <span className="absolute -top-1 -right-1 h-2 w-2 bg-orange-500 rounded-full" />
                            )}
                        </button>
                    );
                })}
            </div>

            {/* Stock indicator */}
            {selectedSize && stockMap && (
                <p className="text-xs text-muted-foreground">
                    {getStockStatus(selectedSize) === "out" 
                        ? "This size is currently out of stock."
                        : getStockStatus(selectedSize) === "low"
                            ? `Only ${stockMap[selectedSize]} left in stock.`
                            : "In stock and ready to ship."
                    }
                </p>
            )}
        </div>
    );
}
