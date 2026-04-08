#!/bin/bash

###############################################################################
# Verificación de instalación: Monitoreo k6 con Prometheus + Grafana
# Ejecuta este script para validar que todo está listo
###############################################################################

set -e

echo ""
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║              Verificación de instalación - Monitoreo k6             ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Contadores
TOTAL=0
PASSED=0

# Función para verificar
check() {
  TOTAL=$((TOTAL + 1))
  echo -n "Verificando $1... "
  if eval "$2" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC}"
    PASSED=$((PASSED + 1))
  else
    echo -e "${RED}✗${NC}"
  fi
}

# Verificaciones
echo "📦 Herramientas necesarias:"
check "k6 instalado" "command -v k6"
check "docker instalado" "command -v docker"
check "docker-compose instalado" "command -v docker-compose"

echo ""
echo "📁 Archivos de configuración:"
check "docker-compose.yml" "test -f monitoring/docker-compose.yml"
check "prometheus.yml" "test -f monitoring/prometheus.yml"
check "datasource provisioning" "test -f monitoring/provisioning/datasources/prometheus.yml"

echo ""
echo "🔧 Scripts de ejecución:"
check "start-monitoring.sh" "test -f monitoring/scripts/start-monitoring.sh"
check "stop-monitoring.sh" "test -f monitoring/scripts/stop-monitoring.sh"
check "run-k6-prometheus.sh" "test -f monitoring/scripts/run-k6-prometheus.sh"

echo ""
echo "📚 Documentación:"
check "README.md" "test -f monitoring/README.md"
check "QUICKSTART.md" "test -f monitoring/QUICKSTART.md"
check "DELIVERY.md" "test -f monitoring/DELIVERY.md"

echo ""
echo "📋 package.json scripts:"
check "npm monitoring:start" "grep -q 'monitoring:start' package.json"
check "npm monitoring:stop" "grep -q 'monitoring:stop' package.json"
check "npm perf:smoke:prom" "grep -q 'perf:smoke:prom' package.json"
check "npm perf:load:prom" "grep -q 'perf:load:prom' package.json"
check "npm perf:stress:prom" "grep -q 'perf:stress:prom' package.json"
check "npm perf:spike:prom" "grep -q 'perf:spike:prom' package.json"

echo ""
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                          Resumen                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo ""
echo "Verificaciones: $PASSED / $TOTAL"
echo ""

if [ $PASSED -eq $TOTAL ]; then
  echo -e "${GREEN}✅ ¡Todo listo para empezar!${NC}"
  echo ""
  echo "Próximos pasos:"
  echo "  1. Levantar servicios:"
  echo "     bash monitoring/scripts/start-monitoring.sh"
  echo "     o"
  echo "     npm run monitoring:start"
  echo ""
  echo "  2. Ejecutar k6 con Prometheus:"
  echo "     bash monitoring/scripts/run-k6-prometheus.sh smoke.test.js"
  echo "     o"
  echo "     npm run perf:smoke:prom"
  echo ""
  echo "  3. Ver resultados en Grafana:"
  echo "     http://localhost:3000"
  echo ""
else
  echo -e "${YELLOW}⚠️  Algunas verificaciones fallaron${NC}"
  echo ""
  echo "Por favor revisa:"
  echo "  - ¿Está k6 instalado? (k6 version)"
  echo "  - ¿Está Docker corriendo?"
  echo "  - ¿Están todos los archivos en su lugar?"
  echo ""
  echo "Para más información: cat monitoring/README.md"
fi

echo ""
