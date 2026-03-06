# ============================================================
# MEDI.WAY Frontend Dockerfile
# Multi-stage build with Nginx
# ============================================================

# Stage 1: Build Stage
FROM node:18-alpine AS builder

LABEL maintainer="MEDI.WAY Team"
LABEL description="MEDI.WAY Healthcare Frontend"

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies (verbose output for debugging)
RUN npm ci

# Copy source code
COPY . .

# Build for production
RUN npm run build

# Stage 2: Production Stage with Nginx
FROM nginx:alpine

LABEL maintainer="MEDI.WAY Team"
LABEL description="MEDI.WAY Healthcare Frontend - Production"

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built assets from builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create non-root user for security
RUN addgroup -S mediway && adduser -S mediway -G mediway \
    && chown -R mediway:mediway /usr/share/nginx/html \
    && chown -R mediway:mediway /var/cache/nginx \
    && chown -R mediway:mediway /var/log/nginx \
    && touch /var/run/nginx.pid \
    && chown -R mediway:mediway /var/run/nginx.pid

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
