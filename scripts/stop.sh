#!/bin/bash
# ===========================================
# Lokale Entwicklungsumgebung stoppen
# ===========================================

echo "🛑 Stopping development environment..."

# Frontend stoppen (Vite)
if pgrep -f "vite" > /dev/null; then
    pkill -f "vite"
    echo "   ✓ Frontend gestoppt"
else
    echo "   - Frontend war nicht aktiv"
fi

# Backend stoppen (Spring Boot)
if pgrep -f "spring-boot:run\|FoodtruckBookingServiceApplication" > /dev/null; then
    pkill -f "spring-boot:run"
    pkill -f "FoodtruckBookingServiceApplication"
    echo "   ✓ Backend gestoppt"
else
    echo "   - Backend war nicht aktiv"
fi

# Optional: PostgreSQL Container stoppen
read -p "PostgreSQL auch stoppen? (j/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Jj]$ ]]; then
    cd "$(dirname "$0")/.."
    docker-compose down
    echo "   ✓ PostgreSQL gestoppt"
fi

echo ""
echo "✅ Entwicklungsumgebung gestoppt"
