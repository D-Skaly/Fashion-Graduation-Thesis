"use client";

import { useState, useEffect } from "react";
import { Star, StarHalf } from "lucide-react";
import { cn } from "@/lib/utils";

interface StarRatingProps {
  value?: number;
  onChange?: (rating: number) => void;
  readonly?: boolean;
  allowHalf?: boolean;
  size?: "sm" | "md" | "lg";
  className?: string;
}

export function StarRating({
  value = 0,
  onChange,
  readonly = false,
  allowHalf = true,
  size = "md",
  className,
}: StarRatingProps) {
  const [rating, setRating] = useState(value);
  const [hoverRating, setHoverRating] = useState(0);

  useEffect(() => {
    setRating(value);
  }, [value]);

  const sizeClasses = {
    sm: "h-3 w-3",
    md: "h-4 w-4",
    lg: "h-6 w-6",
  };

  const handleClick = (selectedRating: number) => {
    if (readonly) return;

    let newRating = selectedRating;
    // If clicking the same star, toggle between full and zero (or half if allowed)
    if (rating === selectedRating) {
      newRating = 0;
    }

    setRating(newRating);
    onChange?.(newRating);
  };

  const handleMouseEnter = (hoveredRating: number) => {
    if (readonly) return;
    setHoverRating(hoveredRating);
  };

  const handleMouseLeave = () => {
    if (readonly) return;
    setHoverRating(0);
  };

  const renderStar = (starIndex: number) => {
    const starValue = starIndex + 1;
    const displayRating = hoverRating || rating;
    
    // Determine if this star should be filled, half-filled, or empty
    const isFull = displayRating >= starValue;
    const isHalf = allowHalf && displayRating >= starValue - 0.5 && displayRating < starValue;

    const starClass = cn(
      sizeClasses[size],
      "transition-colors cursor-pointer",
      isFull || isHalf ? "text-yellow-400" : "text-gray-300",
      !readonly && "hover:text-yellow-300"
    );

    return (
      <div
        key={starIndex}
        className="relative inline-block"
        onClick={() => handleClick(starValue)}
        onMouseEnter={() => handleMouseEnter(starValue)}
        onMouseLeave={handleMouseLeave}
      >
        {isHalf ? (
          <div className="relative">
            <Star className={cn(starClass, "text-gray-300")} />
            <div className="absolute top-0 left-0 overflow-hidden w-1/2">
              <StarHalf className={cn(starClass, "text-yellow-400")} />
            </div>
          </div>
        ) : (
          <Star
            className={starClass}
            fill={isFull ? "currentColor" : "none"}
          />
        )}
      </div>
    );
  };

  return (
    <div className={cn("flex items-center gap-0.5", className)}>
      {[0, 1, 2, 3, 4].map((starIndex) => renderStar(starIndex))}
      {!readonly && (
        <span className="ml-2 text-sm text-muted-foreground">
          {hoverRating || rating}/5
        </span>
      )}
    </div>
  );
}
