"use client";

import Image from "next/image";
import { cn } from "@/lib/utils";

interface ProductImageGalleryProps {
  images?: string[];
  productName: string;
  selectedImageIndex: number;
  onSelectImage: (index: number) => void;
}

export function ProductImageGallery({
  images,
  productName,
  selectedImageIndex,
  onSelectImage,
}: ProductImageGalleryProps) {
  if (!images || images.length === 0) {
    return (
      <div className="aspect-[3/4] w-full bg-stone-100 dark:bg-stone-900 flex items-center justify-center text-muted-foreground/30 font-light tracking-widest uppercase">
        No Image Available
      </div>
    );
  }

  return (
    <div className="w-full space-y-4">
      {/* Main Image */}
      <div className="aspect-[3/4] w-full bg-secondary/20 overflow-hidden relative group">
        <Image
          src={images[selectedImageIndex] || images[0]}
          alt={`${productName} - View ${selectedImageIndex + 1}`}
          fill
          className="object-cover transition-transform duration-700 group-hover:scale-105"
          sizes="(max-width: 768px) 100vw, 60vw"
          priority
        />
      </div>

      {/* Thumbnail Strip */}
      {images.length > 1 && (
        <div className="flex gap-3 overflow-x-auto pb-2">
          {images.map((img, index) => (
            <button
              key={index}
              onClick={() => onSelectImage(index)}
              className={cn(
                "relative w-20 h-24 flex-shrink-0 overflow-hidden border-2 transition-all",
                selectedImageIndex === index
                  ? "border-foreground"
                  : "border-transparent opacity-60 hover:opacity-100"
              )}
            >
              <Image
                src={img}
                alt={`Thumbnail ${index + 1}`}
                fill
                className="object-cover"
                sizes="80px"
              />
            </button>
          ))}
        </div>
      )}

      {/* Additional Gallery Images */}
      {images.length > 1 &&
        images.slice(1).map((img, index) => (
          <div
            key={index}
            className="aspect-[3/4] w-full bg-secondary/20 overflow-hidden relative group"
          >
            <Image
              src={img}
              alt={`${productName} - View ${index + 2}`}
              fill
              className="object-cover transition-transform duration-700 group-hover:scale-105"
              sizes="(max-width: 768px) 100vw, 60vw"
            />
          </div>
        ))}
    </div>
  );
}