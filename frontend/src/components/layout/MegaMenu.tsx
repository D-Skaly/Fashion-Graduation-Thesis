"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronDown, Shirt, Shoe, Watch, Sparkles } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Card } from "@/components/ui/card";

interface Category {
  id: string;
  name: string;
  slug: string;
  image?: string;
  subcategories?: string[];
}

const categories: Category[] = [
  {
    id: "1",
    name: "Mens",
    slug: "mens",
    subcategories: ["Shirts", "Pants", "Jackets", "Accessories"],
  },
  {
    id: "2",
    name: "Womens",
    slug: "womens",
    subcategories: ["Dresses", "Tops", "Bottoms", "Accessories"],
  },
  {
    id: "3",
    name: "Shoes",
    slug: "shoes",
    subcategories: ["Sneakers", "Boots", "Sandals", "Formal"],
  },
  {
    id: "4",
    name: "Accessories",
    slug: "accessories",
    subcategories: ["Watches", "Bags", "Jewelry", "Hats"],
  },
];

export function MegaMenu() {
  const pathname = usePathname();
  const [activeCategory, setActiveCategory] = useState<string | null>(null);

  const isActive = (slug: string) => pathname.startsWith(`/shop/${slug}`);

  return (
    <div className="hidden md:block">
      <div className="flex items-center gap-1">
        {categories.map((category) => (
          <DropdownMenu
            key={category.id}
            open={activeCategory === category.id}
            onOpenChange={(open) => setActiveCategory(open ? category.id : null)}
          >
            <DropdownMenuTrigger asChild>
              <Button
                variant={isActive(category.slug) ? "default" : "ghost"}
                className="h-9"
              >
                {category.name}
                <ChevronDown className="ml-1 h-3 w-3" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-screen max-w-4xl p-6" sideOffset={8}>
              <div className="grid md:grid-cols-4 gap-6">
                {/* Categories */}
                <div className="md:col-span-1">
                  <DropdownMenuLabel className="text-sm font-bold uppercase tracking-wider mb-2">
                    Categories
                  </DropdownMenuLabel>
                  {category.subcategories?.map((sub) => (
                    <DropdownMenuItem key={sub} asChild>
                      <Link
                        href={`/shop/${category.slug}?category=${sub.toLowerCase()}`}
                        className="w-full cursor-pointer"
                      >
                        {sub}
                      </Link>
                    </DropdownMenuItem>
                  ))}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link
                      href={`/shop/${category.slug}`}
                      className="w-full font-medium text-primary cursor-pointer"
                    >
                      View All {category.name}
                    </Link>
                  </DropdownMenuItem>
                </div>

                {/* Featured Products Placeholder */}
                <div className="md:col-span-3">
                  <DropdownMenuLabel className="text-sm font-bold uppercase tracking-wider mb-2">
                    Featured
                  </DropdownMenuLabel>
                  <div className="grid grid-cols-3 gap-4">
                    {[1, 2, 3].map((i) => (
                      <Link key={i} href={`/product/featured-${i}`}>
                        <Card className="overflow-hidden hover:shadow-lg transition-shadow">
                          <div className="aspect-square bg-muted flex items-center justify-center">
                            <Sparkles className="h-8 w-8 text-muted-foreground" />
                          </div>
                          <div className="p-3">
                            <p className="font-medium text-sm">Featured Product {i}</p>
                            <p className="text-sm text-muted-foreground">$99.99</p>
                          </div>
                        </Card>
                      </Link>
                    ))}
                  </div>
                </div>
              </div>
            </DropdownMenuContent>
          </DropdownMenu>
        ))}
      </div>
    </div>
  );
}

// Mobile version (accordion-style)
export function MobileMenu() {
  const [openCategories, setOpenCategories] = useState<string[]>([]);

  const toggleCategory = (id: string) => {
    setOpenCategories((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    );
  };

  return (
    <div className="md:hidden">
      {categories.map((category) => (
        <div key={category.id} className="border-b">
          <button
            onClick={() => toggleCategory(category.id)}
            className="w-full flex items-center justify-between p-4 hover:bg-secondary/20 transition-colors"
          >
            <span className="font-medium">{category.name}</span>
            <ChevronDown
              className={`h-4 w-4 transition-transform ${
                openCategories.includes(category.id) ? "rotate-180" : ""
              }`}
            />
          </button>
          {openCategories.includes(category.id) && (
            <div className="px-4 pb-4 space-y-2">
              {category.subcategories?.map((sub) => (
                <Link
                  key={sub}
                  href={`/shop/${category.slug}?category=${sub.toLowerCase()}`}
                  className="block py-2 text-sm text-muted-foreground hover:text-foreground transition-colors"
                >
                  {sub}
                </Link>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
