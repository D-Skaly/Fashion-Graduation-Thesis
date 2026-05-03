"use client";

import Image, { type ImageProps } from "next/image";
import { useState, type SyntheticEvent } from "react";
import { cn } from "@/lib/utils";
import { Skeleton } from "@/components/ui/skeleton";

interface OptimizedImageProps extends Omit<ImageProps, "src" | "alt"> {
  src?: string;
  alt: string;
  fallback?: string;
  containerClassName?: string;
}

export function OptimizedImage({
  src,
  alt,
  fallback = "/placeholder.png",
  containerClassName,
  className,
  fill,
  sizes = "(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw",
  quality = 85,
  ...props
}: OptimizedImageProps) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const handleLoad = () => {
    setLoading(false);
  };

  const handleError = (e: SyntheticEvent<HTMLImageElement, Event>) => {
    setError(true);
    setLoading(false);
    if (fallback && e.currentTarget.src !== fallback) {
      e.currentTarget.src = fallback;
    }
  };

  const imageSrc = error ? fallback : src || fallback;

  if (fill) {
    return (
      <div className={cn("relative overflow-hidden", containerClassName)}>
        {loading && (
          <Skeleton className="absolute inset-0 z-10" />
        )}
        <Image
          src={imageSrc}
          alt={alt}
          fill
          sizes={sizes}
          quality={quality}
          className={cn("object-cover", className)}
          onLoad={handleLoad}
          onError={handleError}
          {...props}
        />
      </div>
    );
  }

  return (
    <div className={cn("relative overflow-hidden", containerClassName)}>
      {loading && (
        <Skeleton className="absolute inset-0 z-10" />
      )}
      <Image
        src={imageSrc}
        alt={alt}
        quality={quality}
        className={cn("", className)}
        onLoad={handleLoad}
        onError={handleError}
        {...props}
      />
    </div>
  );
}

// Product image with hover effect
interface ProductImageWithHoverProps {
  src?: string;
  hoverSrc?: string;
  alt: string;
  className?: string;
}

export function ProductImageWithHover({
  src,
  hoverSrc,
  alt,
  className,
}: ProductImageWithHoverProps) {
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div
      className="relative overflow-hidden"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <OptimizedImage
        src={isHovered && hoverSrc ? hoverSrc : src}
        alt={alt}
        fill
        className={cn("object-cover transition-transform duration-700", className)}
        sizes="(max-width: 768px) 50vw, (max-width: 1200px) 33vw, 25vw"
      />
    </div>
  );
}
