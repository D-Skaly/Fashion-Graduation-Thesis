"use client";

import dynamic from 'next/dynamic';

// Lazy load AI components
const VirtualTryOn = dynamic(
  () => import('@/components/ai/VirtualTryOn'),
  {
    loading: () => (
      <div className="h-32 flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    ),
    ssr: false, // AI features don't need SSR
  }
);

const BodyMeasurement = dynamic(
  () => import('@/components/ai/body/BodyMeasurement'),
  {
    loading: () => (
      <div className="h-32 flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    ),
    ssr: false,
  }
);

export { VirtualTryOn, BodyMeasurement };
