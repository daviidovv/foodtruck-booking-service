#!/bin/bash
# ===========================================
# Lokales Development starten
# ===========================================

set -e

echo "🚀 Starting local development environment..."

# Prüfen ob PostgreSQL läuft
if ! docker ps | grep -q postgres; then
    echo "📦 Starting PostgreSQL..."
    docker-compose up -d db
    sleep 5
fi

# Backend starten (im Hintergrund)
echo "☕ Starting Spring Boot backend..."
cd "$(dirname "$0")/.."
mvn spring-boot:run -Dspring-boot.run.profiles=local &
BACKEND_PID=$!

# Warten bis Backend bereit ist
echo "⏳ Waiting for backend to start..."
for i in {1..30}; do
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    sleep 2
done

# Frontend starten
echo "⚛️  Starting React frontend..."
cd frontend
npm run dev &
FRONTEND_PID=$!

echo ""
echo "==================================="
echo "✅ Development environment ready!"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo "==================================="
echo ""
echo "Press Ctrl+C to stop all services"

# Cleanup bei Beendigung
trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit" SIGINT SIGTERM

# Warten
wait
