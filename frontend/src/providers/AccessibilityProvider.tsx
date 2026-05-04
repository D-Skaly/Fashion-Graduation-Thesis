"use client";

import { createContext, useContext, useState, type ReactNode } from "react";

interface AccessibilityContextType {
  announce: (message: string, priority?: "polite" | "assertive") => void;
  reducedMotion: boolean;
}

const AccessibilityContext = createContext<AccessibilityContextType | null>(null);

export function AccessibilityProvider({ children }: { children: ReactNode }) {
  const [reducedMotion, setReducedMotion] = useState(() => {
    if (typeof window === 'undefined') return false;
    return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  });

  useEffect(() => {
    if (typeof window === 'undefined') return;
    
    const mediaQuery = window.matchMedia("(prefers-reduced-motion: reduce)");
    
    const handleChange = (e: MediaQueryListEvent) => {
      setReducedMotion(e.matches);
    };
    
    mediaQuery.addEventListener("change", handleChange);
    return () => mediaQuery.removeEventListener("change", handleChange);
  }, []);

  const announce = (message: string, priority: "polite" | "assertive" = "polite") => {
    const ariaLive = document.getElementById(`aria-live-${priority}`);
    if (ariaLive) {
      ariaLive.textContent = message;
      // Clear after announcement
      setTimeout(() => {
        ariaLive.textContent = "";
      }, 1000);
    }
  };

  return (
    <AccessibilityContext.Provider value={{ announce, reducedMotion }}>
      {children}
      {/* ARIA Live Regions for screen reader announcements */}
      <div
        id="aria-live-polite"
        aria-live="polite"
        aria-atomic="true"
        className="sr-only"
      />
      <div
        id="aria-live-assertive"
        aria-live="assertive"
        aria-atomic="true"
        className="sr-only"
      />
    </AccessibilityContext.Provider>
  );
}

export function useAccessibility() {
  const context = useContext(AccessibilityContext);
  if (!context) {
    throw new Error("useAccessibility must be used within an AccessibilityProvider");
  }
  return context;
}
