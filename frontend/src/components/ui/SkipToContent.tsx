"use client";

import { useState } from "react";

interface SkipToContentProps {
  contentId?: string;
}

export function SkipToContent({ contentId = "main-content" }: SkipToContentProps) {
  const [mounted, setMounted] = useState(() => {
    if (typeof window === 'undefined') return false;
    return true;
  });

  if (!mounted) return null;

  return (
    <a
      href={`#${contentId}`}
      className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-primary-foreground focus:rounded-md focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 transition-all"
      onClick={(e) => {
        e.preventDefault();
        const mainContent = document.getElementById(contentId);
        if (mainContent) {
          mainContent.focus();
          mainContent.scrollIntoView({ behavior: "smooth" });
        }
      }}
    >
      Skip to main content
    </a>
  );
}
