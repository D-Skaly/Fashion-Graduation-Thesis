"use client";

import dynamic from "next/dynamic";

// Lazy load VirtualTryOnModal - only loads when user clicks to open
export const VirtualTryOnModal = dynamic(
  () => import("./VirtualTryOnModal").then((mod) => mod.VirtualTryOnModal),
  {
    loading: () => null, // Don't show loading state for modal trigger
    ssr: false,
  }
);

// Type re-export
export type { VirtualTryOnModalProps } from "./VirtualTryOnModal";