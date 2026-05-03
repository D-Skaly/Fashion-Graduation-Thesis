"use client";

import { useEffect } from "react";
import { usePathname } from "next/navigation";

// Simple performance monitoring without external dependencies
// Can be extended to use Web Vitals library if needed

interface PerformanceMetrics {
  FCP?: number; // First Contentful Paint
  LCP?: number; // Largest Contentful Paint
  CLS?: number; // Cumulative Layout Shift
  FID?: number; // First Input Delay
  TTFB?: number; // Time to First Byte
}

export function PerformanceMonitor() {
  const pathname = usePathname();

  useEffect(() => {
    // Report page view
    reportPageView(pathname);

    // Measure Core Web Vitals
    measureWebVitals();

    // Measure navigation timing
    measureNavigationTiming();
  }, [pathname]);

  return null; // This component doesn't render anything
}

function reportPageView(path: string) {
  // Send to your analytics service
  if (typeof window !== "undefined" && "gtag" in window) {
    (window as any).gtag("config", process.env.NEXT_PUBLIC_GA_ID, {
      page_path: path,
    });
  }

  // Log for development
  if (process.env.NODE_ENV === "development") {
    console.log(`[Analytics] Page view: ${path}`);
  }
}

function measureWebVitals() {
  if (typeof window === "undefined") return;

  // Measure LCP
  new PerformanceObserver((entryList) => {
    const entries = entryList.getEntries();
    const lastEntry = entries[entries.length - 1] as any;
    if (lastEntry) {
      reportMetric("LCP", lastEntry.startTime);
    }
  }).observe({ type: "largest-contentful-paint", buffered: true });

  // Measure FCP
  new PerformanceObserver((entryList) => {
    const entries = entryList.getEntries();
    entries.forEach((entry: any) => {
      reportMetric("FCP", entry.startTime);
    });
  }).observe({ type: "paint", buffered: true });

  // Measure CLS
  let clsValue = 0;
  new PerformanceObserver((entryList) => {
    const entries = entryList.getEntries() as any[];
    entries.forEach((entry) => {
      if (!entry.hadRecentInput) {
        clsValue += entry.value;
        reportMetric("CLS", clsValue);
      }
    });
  }).observe({ type: "layout-shift", buffered: true });

  // Measure FID
  new PerformanceObserver((entryList) => {
    const entries = entryList.getEntries();
    entries.forEach((entry: any) => {
      reportMetric("FID", entry.processingStart - entry.startTime);
    });
  }).observe({ type: "first-input", buffered: true });
}

function measureNavigationTiming() {
  if (typeof window === "undefined") return;

  window.addEventListener("load", () => {
    setTimeout(() => {
      const perfData = performance.timing;
      const ttfb = perfData.responseStart - perfData.requestStart;
      reportMetric("TTFB", ttfb);

      const domLoad = perfData.domComplete - perfData.domLoading;
      reportMetric("DOM_LOAD", domLoad);

      const windowLoad = perfData.loadEventEnd - perfData.navigationStart;
      reportMetric("WINDOW_LOAD", windowLoad);
    }, 0);
  });
}

function reportMetric(name: string, value: number) {
  // Log to console in development
  if (process.env.NODE_ENV === "development") {
    console.log(`[Performance] ${name}: ${Math.round(value)}ms`);
  }

  // Send to analytics service
  if (typeof window !== "undefined" && "gtag" in window) {
    (window as any).gtag("event", "web_vitals", {
      metric_name: name,
      metric_value: Math.round(value),
    });
  }
}

// Hook for measuring component performance
export function useComponentPerformance(componentName: string) {
  useEffect(() => {
    const startTime = performance.now();

    return () => {
      const endTime = performance.now();
      const duration = endTime - startTime;
      if (process.env.NODE_ENV === "development") {
        console.log(`[Performance] ${componentName} render time: ${Math.round(duration)}ms`);
      }
    };
  }, [componentName]);
}
