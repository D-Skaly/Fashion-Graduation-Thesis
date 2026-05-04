// Structured Data for SEO - JSON-LD
// Use this in pages for better search engine understanding

export interface StructuredData {
  "@context": string;
  "@type": string;
  [key: string]: unknown;
}

export function generateOrganizationLD(): StructuredData {
  return {
    "@context": "https://schema.org",
    "@type": "Organization",
    "name": "Fashion Thesis",
    "url": process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000",
    "logo": `${process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000"}/logo.png`,
    "description": "A premium fashion e-commerce experience blending avant-garde aesthetics with neural intelligence.",
    "sameAs": [
      "https://facebook.com/fashionthesis",
      "https://instagram.com/fashionthesis",
      "https://twitter.com/fashionthesis"
    ]
  };
}

export function generateProductLD(product: {
  id: string;
  name: string;
  description: string;
  price: number;
  image: string[];
  brand?: string;
  category?: string;
  rating?: number;
  reviewCount?: number;
}): StructuredData {
  return {
    "@context": "https://schema.org",
    "@type": "Product",
    "name": product.name,
    "description": product.description,
    "image": product.image,
    "sku": product.id,
    "brand": {
      "@type": "Brand",
      "name": product.brand || "Fashion Thesis"
    },
    "category": product.category,
    "offers": {
      "@type": "Offer",
      "priceCurrency": "USD",
      "price": product.price,
      "availability": "https://schema.org/InStock",
      "url": `${process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000"}/product/${product.id}`
    },
    ...(product.rating && product.reviewCount ? {
      "aggregateRating": {
        "@type": "AggregateRating",
        "ratingValue": product.rating,
        "reviewCount": product.reviewCount
      }
    } : {})
  };
}

export function generateBreadcrumbLD(items: { name: string; url: string }[]): StructuredData {
  return {
    "@context": "https://schema.org",
    "@type": "BreadcrumbList",
    "itemListElement": items.map((item, index) => ({
      "@type": "ListItem",
      "position": index + 1,
      "name": item.name,
      "item": item.url
    }))
  };
}

export function generateWebSiteLD(): StructuredData {
  return {
    "@context": "https://schema.org",
    "@type": "WebSite",
    "name": "Fashion Thesis",
    "url": process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000",
    "potentialAction": {
      "@type": "SearchAction",
      "target": {
        "@type": "EntryPoint",
        "urlTemplate": `${process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000"}/shop?q={search_term_string}`
      },
      "query-input": "required name=search_term_string"
    }
  };
}

// Component to inject structured data
export function StructuredDataScript({ data }: { data: StructuredData }) {
  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(data) }}
    />
  );
}
