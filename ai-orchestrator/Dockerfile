# ============================================
# Multi-stage Dockerfile for AI Orchestrator (NestJS)
# ============================================

# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./
COPY tsconfig.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY src ./src

# Build the application
RUN npm run build

# Stage 2: Runtime
FROM node:20-alpine

# Add labels
LABEL maintainer="DSkaly"
LABEL description="Fashion Intelligence AI Orchestrator"

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S nodejs && adduser -S appuser -G nodejs

WORKDIR /app

# Copy built files from builder
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./

USER appuser

# Expose port
EXPOSE 3001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:3001/api/v1/orchestrator/health || exit 1

# Run the application
CMD ["node", "dist/main.js"]
