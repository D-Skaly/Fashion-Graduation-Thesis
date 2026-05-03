"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronRight, Home } from "lucide-react";
import { cn } from "@/lib/utils";

interface BreadcrumbItem {
  label: string;
  href?: string;
}

export function Breadcrumb({
  className,
  customItems,
}: {
  className?: string;
  customItems?: BreadcrumbItem[];
}) {
  const pathname = usePathname();

  // Auto-generate from pathname if no custom items
  const items = customItems || generateBreadcrumbs(pathname);

  // Generate structured data for SEO
  const structuredData = {
    "@context": "https://schema.org",
    "@type": "BreadcrumList",
    "itemListElement": items.map((item, index) => ({
      "@type": "ListItem",
      "position": index + 1,
      "name": item.label,
      ...(item.href && { "item": { "@id": `${window.location.origin}${item.href}` } }),
    })),
  };

  return (
    <>
      <nav aria-label="Breadcrumb" className={cn("flex items-center gap-2 text-sm", className)}>
        {items.map((item, index) => (
          <div key={index} className="flex items-center gap-2">
            {index > 0 && (
              <ChevronRight className="h-3 w-3 text-muted-foreground" />
            )}
            {item.href ? (
              <Link
                href={item.href}
                className={`hover:text-foreground transition-colors ${
                  index === items.length - 1
                    ? "text-foreground font-medium"
                    : "text-muted-foreground"
                }`}
              >
                {index === 0 ? (
                  <Home className="h-3 w-3" />
                ) : (
                  item.label
                )}
              </Link>
            ) : (
              <span
                className={`${
                  index === items.length - 1
                    ? "text-foreground font-medium"
                    : "text-muted-foreground"
                }`}
              >
                {index === 0 ? <Home className="h-3 w-3" /> : item.label}
              </span>
            )}
          </div>
        ))}
      </nav>

      {/* Structured Data */}
      <script
        type="application/ld+json"
        dangerouslySetInnerHTML={{ __html: JSON.stringify(structuredData) }}
      />
    </>
  );
}

function generateBreadcrumbs(pathname: string): BreadcrumbItem[] {
  const segments = pathname.split("/").filter(Boolean);
  const items: BreadcrumbItem[] = [{ label: "Home", href: "/" }];

  let currentPath = "";
  segments.forEach((segment) => {
    currentPath += `/${segment}`;

    // Skip dynamic segments (like [id])
    if (segment.startsWith("[")) return;

    // Format label (capitalize, replace hyphens)
    const label = segment
      .split("-")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");

    items.push({
      label,
      href: items.length === segments.length ? undefined : currentPath,
    });
  });

  return items;
}
