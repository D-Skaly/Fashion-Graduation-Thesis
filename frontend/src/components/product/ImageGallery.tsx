"use client";

import { useState } from "react";
import { ChevronLeft, ChevronRight, ZoomIn } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface ProductImage {
    id: string;
    url: string;
    alt: string;
    sortOrder: number;
    isPrimary: boolean;
}

interface ImageGalleryProps {
    images: ProductImage[];
    productName: string;
}

export function ImageGallery({ images, productName }: ImageGalleryProps) {
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [isZoomed, setIsZoomed] = useState(false);

    if (!images || images.length === 0) {
        return (
            <div className="aspect-[3/4] bg-secondary/20 rounded-xl overflow-hidden flex items-center justify-center">
                <p className="text-muted-foreground">No images available</p>
            </div>
        );
    }

    const selectedImage = images[selectedIndex];

    const handlePrevious = () => {
        setSelectedIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
    };

    const handleNext = () => {
        setSelectedIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
    };

    return (
        <div className="space-y-4">
            {/* Main Image */}
            <div
                className="aspect-[3/4] bg-secondary/20 rounded-xl overflow-hidden relative group cursor-zoom-in"
                onClick={() => setIsZoomed(!isZoomed)}
            >
                <div className="w-full h-full relative">
                    <div className="w-full h-full bg-stone-100 flex items-center justify-center">
                        {/* Placeholder for actual image */}
                        <div className="text-center">
                            <p className="text-muted-foreground text-sm">{selectedImage.alt || productName}</p>
                            <p className="text-xs text-muted-foreground mt-1">Image {selectedIndex + 1} of {images.length}</p>
                        </div>
                    </div>
                </div>

                {/* Navigation Arrows */}
                {images.length > 1 && (
                    <>
                        <Button
                            variant="secondary"
                            size="icon"
                            className="absolute left-2 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity"
                            onClick={(e) => {
                                e.stopPropagation();
                                handlePrevious();
                            }}
                        >
                            <ChevronLeft className="h-4 w-4" />
                        </Button>
                        <Button
                            variant="secondary"
                            size="icon"
                            className="absolute right-2 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity"
                            onClick={(e) => {
                                e.stopPropagation();
                                handleNext();
                            }}
                        >
                            <ChevronRight className="h-4 w-4" />
                        </Button>
                    </>
                )}

                {/* Zoom Indicator */}
                <Button
                    variant="ghost"
                    size="icon"
                    className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity bg-background/80 backdrop-blur-sm"
                    onClick={(e) => {
                        e.stopPropagation();
                        setIsZoomed(!isZoomed);
                    }}
                >
                    <ZoomIn className="h-4 w-4" />
                </Button>

                {/* Zoom Overlay */}
                {isZoomed && (
                    <div className="absolute inset-0 bg-black/50 flex items-center justify-center z-10" onClick={() => setIsZoomed(false)}>
                        <div className="w-full h-full bg-stone-100 flex items-center justify-center">
                            <p className="text-foreground font-medium">Zoomed View</p>
                        </div>
                    </div>
                )}
            </div>

            {/* Thumbnails */}
            {images.length > 1 && (
                <div className="flex gap-2 overflow-x-auto pb-2">
                    {images.map((image, index) => (
                        <button
                            key={image.id}
                            onClick={() => setSelectedIndex(index)}
                            className={cn(
                                "flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all",
                                selectedIndex === index
                                    ? "border-primary"
                                    : "border-transparent hover:border-primary/50"
                            )}
                        >
                            <div className="w-full h-full bg-stone-100 flex items-center justify-center">
                                <span className="text-xs text-muted-foreground">{index + 1}</span>
                            </div>
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
