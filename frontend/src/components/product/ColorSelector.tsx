"use client";

import { cn } from "@/lib/utils";

interface ColorSelectorProps {
    colors: string[];
    selectedColor: string | null;
    onSelectColor: (color: string) => void;
}

// Color mapping for common color names to hex values
const colorMap: Record<string, string> = {
    black: "#000000",
    white: "#ffffff",
    red: "#ef4444",
    blue: "#3b82f6",
    green: "#22c55e",
    yellow: "#eab308",
    orange: "#f97316",
    purple: "#a855f7",
    pink: "#ec4899",
    brown: "#92400e",
    gray: "#6b7280",
    grey: "#6b7280",
    navy: "#1e3a8a",
    beige: "#f5f5dc",
    cream: "#fffdd0",
    gold: "#ffd700",
    silver: "#c0c0c0",
    bronze: "#cd7f32",
    tan: "#d2b48c",
    charcoal: "#36454f",
};

export function ColorSelector({ colors, selectedColor, onSelectColor }: ColorSelectorProps) {
    const getColorHex = (colorName: string): string => {
        return colorMap[colorName.toLowerCase()] || "#cccccc";
    };

    return (
        <div className="space-y-3">
            <span className="text-sm font-medium">
                Color: <span className="text-muted-foreground">{selectedColor || "Select a color"}</span>
            </span>
            <div className="flex flex-wrap gap-3">
                {colors.map((color) => (
                    <button
                        key={color}
                        onClick={() => onSelectColor(color)}
                        className={cn(
                            "h-10 w-10 rounded-full border-2 transition-all relative",
                            selectedColor === color
                                ? "border-primary ring-2 ring-primary ring-offset-2"
                                : "border-input hover:border-primary/50"
                        )}
                        style={{ backgroundColor: getColorHex(color) }}
                        title={color}
                    >
                        {/* Show color name on hover */}
                        <span className="sr-only">{color}</span>
                    </button>
                ))}
            </div>
        </div>
    );
}
