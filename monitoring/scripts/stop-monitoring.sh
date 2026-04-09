#!/bin/bash

###############################################################################
# stop-monitoring.sh
# Detiene y limpia servicios de monitoreo
###############################################################################

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MONITORING_DIR="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo "🛑 Deteniendo servicios de monitoreo"
echo "=========================================="

cd "$MONITORING_DIR"

# Verificar si docker-compose existe
if [ ! -f "docker-compose.yml" ]; then
  echo "❌ Error: docker-compose.yml no encontrado"
  exit 1
fi

# Detener servicios
echo "📦 Deteniendo contenedores..."
docker-compose down

echo ""
echo "✅ Servicios detenidos exitosamente"
echo ""
echo "💡 Nota: Los volúmenes de datos persisten. Para eliminarlos:"
echo "   docker-compose down -v"
echo "=========================================="
