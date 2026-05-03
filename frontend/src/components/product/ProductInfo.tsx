"use client";

import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";

interface ProductInfoProps {
  categoryName: string;
  name: string;
  price: number;
  description: string;
}

export function ProductInfo({
  categoryName,
  name,
  price,
  description,
}: ProductInfoProps) {
  const formattedPrice = new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(price);

  return (
    <div>
      <Badge variant="secondary" className="mb-3">
        {categoryName}
      </Badge>
      <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-2">
        {name}
      </h1>
      <div className="flex items-end gap-4">
        <span className="text-2xl font-bold">{formattedPrice}</span>
      </div>
      <Separator className="my-4" />
      <p className="text-muted-foreground leading-relaxed">{description}</p>
    </div>
  );
}