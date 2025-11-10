# syntax=docker/dockerfile:1.4

# ============================================
# Stage 1: Build
# ============================================
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build application
RUN npm run build

# ============================================
# Stage 2: Runtime
# ============================================
FROM nginx:1.27-alpine

# Create non-root user and set up nginx directories
RUN addgroup -g 1001 -S appuser && \
    adduser -S -u 1001 -G appuser appuser && \
    mkdir -p /var/cache/nginx /var/run /var/log/nginx /etc/nginx/conf.d /tmp && \
    chown -R appuser:appuser /var/cache/nginx /var/run /var/log/nginx /etc/nginx/conf.d /tmp && \
    chmod -R 755 /var/cache/nginx /var/run /var/log/nginx /etc/nginx/conf.d /tmp

WORKDIR /usr/share/nginx/html

# Copy built files
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Set ownership
RUN chown -R appuser:appuser /usr/share/nginx/html

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO- http://localhost/ || exit 1

# Expose port
EXPOSE 80

# Don't switch to non-root user - nginx needs to bind to port 80
# USER appuser

# Start nginx
CMD ["nginx", "-g", "daemon off;"]