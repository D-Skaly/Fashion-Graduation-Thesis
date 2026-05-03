import type { NextConfig } from "next";
import analyze from "@next/bundle-analyzer";

const withBundleAnalyzer = analyze({
  enabled: process.env.ANALYZE === "true",
});

const nextConfig: NextConfig = {
  output: 'standalone',
  compress: true,
  poweredByHeader: false,
  images: {
    unoptimized: false,
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'localhost',
      },
      {
        protocol: 'http',
        hostname: 'localhost',
      },
      // Add your production CDN domains here
      // {
      //   protocol: 'https',
      //   hostname: 'cdn.yourdomain.com',
      // },
    ],
    formats: ['image/avif', 'image/webp'],
    // WARNING: Only enable dangerouslyAllowSVG if you implement SVG sanitization
    // Recommended: Use DOMPurify or similar library to sanitize SVGs from untrusted sources
    dangerouslyAllowSVG: false,
    contentSecurityPolicy: `default-src 'self'; script-src 'self' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' ${process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'} ws: wss:;`,
  },
  experimental: {
    optimizePackageImports: ['@radix-ui/react-*'],
  },
  async headers() {
    return [
      {
        source: '/:path*',
        headers: [
          {
            key: 'X-Frame-Options',
            value: 'DENY',
          },
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'Referrer-Policy',
            value: 'strict-origin-when-cross-origin',
          },
        ],
      },
    ];
  },
});

export default nextConfig;
