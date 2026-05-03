import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'node',
    globals: true,
    include: ['frontend/src/**/*.{test,spec}.{ts,tsx,js}'],
    exclude: [
      '**/node_modules/**',
      '**/dist/**',
      '**/.next/**',
      '**/ai-orchestrator/**',
      '**/backend/**',
      '**/ai-service/**',
      '**/nginx/**',
      '**/docs/**',
    ],
    esbuild: {
      tsconfig: 'frontend/tsconfig.json',
    },
  },
});