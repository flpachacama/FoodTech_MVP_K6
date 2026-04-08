#!/bin/bash

###############################################################################
# cleanup-monitoring.sh
# Limpia todos los contenedores y volumenes de monitoreo
###############################################################################

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MONITORING_DIR="$(dirname "$SCRIPT_DIR")"

echo ""
echo "=========================================="
echo "🧹 Limpiando servicios de monitoreo"
echo "=========================================="
echo ""

cd "$MONITORING_DIR"

echo "📦 Deteniendo contenedores..."
docker-compose down

echo "📦 Eliminando volúmenes..."
docker-compose down -v

echo "📦 Purgando imágenes..."
docker image prune -f

echo ""
echo "=========================================="
echo "✅ Limpieza completada"
echo "=========================================="
echo ""
echo "Próximos pasos:"
echo "  bash monitoring/scripts/start-monitoring.sh"
echo ""
