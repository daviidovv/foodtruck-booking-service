#!/bin/bash
# ===========================================
# Deployment Script für Production
# Usage: ./scripts/deploy.sh
# ===========================================

set -e

echo "🚀 Starting deployment..."

# Prüfen ob .env existiert
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    echo "   Copy .env.example to .env and configure it."
    exit 1
fi

# Source environment variables
source .env

# Prüfen ob Passwort gesetzt
if [ -z "$POSTGRES_PASSWORD" ] || [ "$POSTGRES_PASSWORD" = "your-secure-password-here" ]; then
    echo "❌ Error: POSTGRES_PASSWORD not set or still default!"
    exit 1
fi

echo "📦 Building Docker image..."
docker-compose -f docker-compose.prod.yml build

echo "🔄 Stopping existing containers..."
docker-compose -f docker-compose.prod.yml down

echo "🚀 Starting containers..."
docker-compose -f docker-compose.prod.yml up -d

echo "⏳ Waiting for application to start..."
sleep 30

# Health check
echo "🏥 Running health check..."
for i in {1..10}; do
    if curl -sf http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
        echo ""
        echo "==================================="
        echo "✅ Deployment successful!"
        echo "   Application: http://localhost:8080"
        echo "==================================="
        exit 0
    fi
    echo "   Attempt $i/10..."
    sleep 5
done

echo "❌ Health check failed!"
echo "   Check logs: docker-compose -f docker-compose.prod.yml logs"
exit 1
