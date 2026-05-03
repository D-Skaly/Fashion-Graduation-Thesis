"use client";

import dynamic from "next/dynamic";

// Lazy load BodyMeasurement with MediaPipe - only loads when actually rendered
export const BodyMeasurement = dynamic(
  () => import("./BodyMeasurement").then((mod) => mod.BodyMeasurement),
  {
    loading: () => (
      <div className="flex items-center justify-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
        <span className="ml-2 text-sm text-muted-foreground">
          Loading measurement tool...
        </span>
      </div>
    ),
    ssr: false, // MediaPipe doesn't work on server-side
  }
);

// Type re-export for convenience
export type { BodyMeasurementProps } from "./BodyMeasurement";