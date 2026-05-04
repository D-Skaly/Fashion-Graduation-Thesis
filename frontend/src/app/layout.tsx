import type { Metadata, Viewport } from "next";
import { Outfit } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";
import { Toaster } from "@/components/ui/sonner";
import { AiStylistFAB } from "@/components/ui/AiStylistFAB";
import QueryProvider from "@/providers/QueryProvider";
import { ThemeProvider } from "@/providers/ThemeProvider";
import { SkipToContent } from "@/components/ui/SkipToContent";
import { AccessibilityProvider } from "@/providers/AccessibilityProvider";

const outfit = Outfit({
  subsets: ["latin"],
  variable: "--font-outfit",
  display: "swap",
  fallback: ["system-ui", "arial"],
});

export const metadata: Metadata = {
  title: {
    default: "Fashion Thesis | Premium Fashion",
    template: "%s | Fashion Thesis",
  },
  description:
    "A premium fashion e-commerce experience blending avant-garde aesthetics with neural intelligence.",
  keywords: [
    "fashion",
    "luxury",
    "sustainable",
    "modern",
    "e-commerce",
  ],
  authors: [{ name: "Fashion Thesis Team" }],
  creator: "Fashion Thesis",
  publisher: "Fashion Thesis",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  metadataBase: new URL(
    process.env.NEXT_PUBLIC_SITE_URL || "http://localhost:3000"
  ),
  openGraph: {
    type: "website",
    locale: "en_US",
    url: "/",
    title: "Fashion Thesis | Premium Fashion",
    description:
      "A premium fashion e-commerce experience blending avant-garde aesthetics with neural intelligence.",
    siteName: "Fashion Thesis",
    images: [
      {
        url: "/og-image.jpg",
        width: 1200,
        height: 630,
        alt: "Fashion Thesis",
      },
    ],
  },
  twitter: {
    card: "summary_large_image",
    title: "Fashion Thesis | Premium Fashion",
    description:
      "A premium fashion e-commerce experience blending avant-garde aesthetics with neural intelligence.",
    images: ["/twitter-image.jpg"],
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-video-preview": -1,
      "max-image-preview": "large",
      "max-snippet": -1,
    },
  },
};

export const viewport: Viewport = {
  themeColor: [
    { media: "(prefers-color-scheme: light)", color: "#ffffff" },
    { media: "(prefers-color-scheme: dark)", color: "#000000" },
  ],
  width: "device-width",
  initialScale: 1,
  maximumScale: 5,
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <link rel="manifest" href="/manifest.json" />
        <link rel="icon" href="/favicon.ico" sizes="any" />
        <link rel="icon" href="/icon.svg" type="image/svg+xml" />
        <link rel="apple-touch-icon" href="/apple-touch-icon.png" />
      </head>
      <body
        className={cn(
          "min-h-screen bg-background font-sans antialiased",
          outfit.variable
        )}
      >
        <SkipToContent />
        <ThemeProvider
          attribute="class"
          defaultTheme="light"
          enableSystem
          disableTransitionOnChange={false}
        >
          <AccessibilityProvider>
            <QueryProvider>
              {children}
              <AiStylistFAB />
              <Toaster
                position="top-right"
                richColors
                closeButton
                toastOptions={{
                  style: {
                    fontFamily: "var(--font-sans)",
                  },
                }}
              />
            </QueryProvider>
          </AccessibilityProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
