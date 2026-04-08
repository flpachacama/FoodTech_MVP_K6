@echo off
REM run-k6-prometheus.bat
REM Ejecuta k6 con integracion a Prometheus (remote write)
REM
REM Uso:
REM    .\monitoring\scripts\run-k6-prometheus.bat smoke.test.js
REM    .\monitoring\scripts\run-k6-prometheus.bat load.test.js
REM    .\monitoring\scripts\run-k6-prometheus.bat stress.test.js
REM    .\monitoring\scripts\run-k6-prometheus.bat spike.test.js

setlocal enabledelayedexpansion

set PROMETHEUS_URL=http://localhost:9090
set TEST_FILE=%1
set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..\..") do set PROJECT_ROOT=%%~fI

if "!TEST_FILE!"=="" (
  set TEST_FILE=smoke.test.js
)

echo.
echo ==========================================
echo.^^ Ejecutando k6 con integracion Prometheus
echo ==========================================
echo.
echo Configuracion:
echo    Prometheus URL: !PROMETHEUS_URL!
echo    Test file:      !TEST_FILE!
echo    Directorio:     !PROJECT_ROOT!
echo.

REM Verificar que Prometheus este disponible
echo.^^ Verificando disponibilidad de Prometheus...
curl -s "!PROMETHEUS_URL!/api/v1/status/config" > nul 2>&1
if errorlevel 1 (
  echo ^^!^^! Error: Prometheus no esta disponible en !PROMETHEUS_URL!
  echo.
  echo Ejecuta primero:
  echo    .\monitoring\scripts\start-monitoring.bat
  exit /b 1
)
echo.^^ Prometheus disponible
echo.

REM Verificar que k6 este instalado
where k6 > nul 2>&1
if errorlevel 1 (
  echo ^^!^^! Error: k6 no esta instalado
  echo.
  echo Instala k6:
  echo    choco install k6
  exit /b 1
)
echo.^^ k6 disponible
echo.

REM Verificar que el archivo de test existe
cd /d "!PROJECT_ROOT!"
if not exist "performance\tests\!TEST_FILE!" (
  echo ^^!^^! Error: Archivo de test no encontrado: performance\tests\!TEST_FILE!
  echo.
  echo Tests disponibles:
  dir /b performance\tests\*.test.js 2>nul
  exit /b 1
)
echo.^^ Test encontrado: performance\tests\!TEST_FILE!
echo.

REM Ejecutar k6 con Prometheus remote write
echo ==========================================
echo.^^ Ejecutando prueba...
echo ==========================================
echo.

k6 run ^
  --env TEST_ENV=dev ^
  --env REPORT_DIR=performance\reports ^
  --out experimental-prometheus-rw ^
  "performance\tests\!TEST_FILE!"

set EXIT_CODE=!ERRORLEVEL!

echo.
echo ==========================================
echo.^^ Prueba completada
echo ==========================================
echo.
echo Proximos pasos:
echo    1. Accede a Grafana: http://localhost:3000
echo    2. Ve a dashboards y busca 'k6' o crea uno nuevo
echo    3. Selecciona Prometheus como datasource
echo.

exit /b !EXIT_CODE!
