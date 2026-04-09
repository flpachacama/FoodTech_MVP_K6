#!/bin/bash

###############################################################################
# start-monitoring.sh
# Levanta Prometheus y Grafana en Docker para monitoreo de k6
###############################################################################

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MONITORING_DIR="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo "🚀 Iniciando servicios de monitoreo (Prometheus + Grafana)"
echo "=========================================="

cd "$MONITORING_DIR"

# Verificar si docker-compose existe
if [ ! -f "docker-compose.yml" ]; then
  echo "❌ Error: docker-compose.yml no encontrado en $MONITORING_DIR"
  exit 1
fi

# Levantar servicios
echo "📦 Levantando servicios..."
docker-compose up -d

# Esperar a que Prometheus esté listo
echo "⏳ Esperando a que Prometheus esté listo..."
sleep 5
MAX_RETRIES=30
RETRIES=0
while ! curl -s http://localhost:9090/api/v1/status/config > /dev/null 2>&1; do
  RETRIES=$((RETRIES + 1))
  if [ $RETRIES -ge $MAX_RETRIES ]; then
    echo "❌ Prometheus no respondió en tiempo"
    docker-compose down
    exit 1
  fi
  echo "   Reintentando... ($RETRIES/$MAX_RETRIES)"
  sleep 2
done

# Esperar a que Grafana esté listo
echo "⏳ Esperando a que Grafana esté listo..."
RETRIES=0
while ! curl -s http://localhost:3000/api/health > /dev/null 2>&1; do
  RETRIES=$((RETRIES + 1))
  if [ $RETRIES -ge $MAX_RETRIES ]; then
    echo "❌ Grafana no respondió en tiempo"
    docker-compose down
    exit 1
  fi
  echo "   Reintentando... ($RETRIES/$MAX_RETRIES)"
  sleep 2
done

echo ""
echo "✅ Servicios levantados exitosamente"
echo ""
echo "=========================================="
echo "📊 Acceso a servicios:"
echo "=========================================="
echo "🔍 Prometheus: http://localhost:9090"
echo "📈 Grafana:    http://localhost:3000"
echo "   Usuario:    admin"
echo "   Contraseña: admin"
echo ""
echo "💡 Próximos pasos:"
echo "   1. Accede a Grafana (http://localhost:3000)"
echo "   2. Ve a Configuration → Data Sources"
echo "   3. Agrega Prometheus como datasource"
echo "   4. Ejecuta: bash monitoring/scripts/run-k6-prometheus.sh smoke.test.js"
echo ""
echo "❌ Para detener: bash monitoring/scripts/stop-monitoring.sh"
echo "=========================================="
