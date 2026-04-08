#!/bin/bash

###############################################################################
# run-k6-prometheus.sh
# Ejecuta k6 con integración a Prometheus (remote write)
# 
# Uso:
#   bash monitoring/scripts/run-k6-prometheus.sh smoke.test.js
#   bash monitoring/scripts/run-k6-prometheus.sh load.test.js
#   bash monitoring/scripts/run-k6-prometheus.sh stress.test.js
#   bash monitoring/scripts/run-k6-prometheus.sh spike.test.js
###############################################################################

set -e

# Variables
PROMETHEUS_URL="${PROMETHEUS_URL:-http://localhost:9090}"
TEST_FILE="${1:-smoke.test.js}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"

echo "=========================================="
echo "🚀 Ejecutando k6 con integración Prometheus"
echo "=========================================="
echo ""
echo "📊 Configuración:"
echo "   Prometheus URL: $PROMETHEUS_URL"
echo "   Test file:      $TEST_FILE"
echo "   Directorio:     $PROJECT_ROOT"
echo ""

# Verificar que Prometheus esté disponible
echo "🔍 Verificando disponibilidad de Prometheus..."
if ! curl -s "$PROMETHEUS_URL/api/v1/status/config" > /dev/null 2>&1; then
  echo "❌ Error: Prometheus no está disponible en $PROMETHEUS_URL"
  echo ""
  echo "💡 Ejecuta primero:"
  echo "   bash monitoring/scripts/start-monitoring.sh"
  exit 1
fi
echo "✅ Prometheus disponible"
echo ""

# Verificar que k6 esté instalado
if ! command -v k6 &> /dev/null; then
  echo "❌ Error: k6 no está instalado o no está en PATH"
  echo ""
  echo "💡 Instala k6:"
  echo "   Windows: choco install k6"
  echo "   macOS:   brew install k6"
  echo "   Linux:   apt-get install k6 (o tu package manager)"
  exit 1
fi
echo "✅ k6 disponible ($(k6 version))"
echo ""

# Verificar que el archivo de test existe
cd "$PROJECT_ROOT"
if [ ! -f "performance/tests/$TEST_FILE" ]; then
  echo "❌ Error: Archivo de test no encontrado: performance/tests/$TEST_FILE"
  echo ""
  echo "📝 Tests disponibles:"
  ls -1 performance/tests/*.test.js 2>/dev/null | sed 's/.*\//   - /' || echo "   (ninguno encontrado)"
  exit 1
fi
echo "✅ Test encontrado: performance/tests/$TEST_FILE"
echo ""

# Ejecutar k6 con Prometheus remote write
echo "=========================================="
echo "▶️  Ejecutando prueba..."
echo "=========================================="
echo ""

k6 run \
  --env TEST_ENV=dev \
  --env REPORT_DIR=performance/reports \
  --out experimental-prometheus-rw \
  "performance/tests/$TEST_FILE"

EXIT_CODE=$?

echo ""
echo "=========================================="
echo "✅ Prueba completada"
echo "=========================================="
echo ""
echo "📊 Próximos pasos:"
echo "   1. Accede a Grafana: http://localhost:3000"
echo "   2. Ve a dashboards y busca 'k6' o crea uno nuevo"
echo "   3. Selecciona Prometheus como datasource"
echo ""

exit $EXIT_CODE
