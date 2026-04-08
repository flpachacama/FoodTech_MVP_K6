@echo off
REM start-monitoring.bat
REM Levanta Prometheus y Grafana en Docker para monitoreo de k6

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set MONITORING_DIR=%SCRIPT_DIR:~0,-9%

echo.
echo ==========================================
echo.^^ Iniciando servicios de monitoreo (Prometheus + Grafana)
echo ==========================================
echo.

cd /d "%MONITORING_DIR%"

if not exist docker-compose.yml (
  echo ^^!^^! Error: docker-compose.yml no encontrado
  exit /b 1
)

echo.^^ Levantando servicios...
docker-compose up -d

if errorlevel 1 (
  echo.
  echo ==========================================
  echo ^^!^^! Error: No se pudieron levantar los servicios de monitoreo
  echo ==========================================
  echo.
  exit /b 1
)

echo.
echo ==========================================
echo.^^ Servicios levantados exitosamente
echo ==========================================
echo.
echo Acceso a servicios:
echo ^^ Prometheus: http://localhost:9090
echo ^^ Grafana:    http://localhost:3000
echo    Usuario:    admin
echo    Contraseña: admin
echo.
echo Proximos pasos:
echo    1. Accede a Grafana (http://localhost:3000)
echo    2. Ve a Configuration - Data Sources
echo    3. Agrega Prometheus como datasource
echo    4. Ejecuta: .\monitoring\scripts\run-k6-prometheus.bat smoke.test.js
echo.
echo Para detener: .\monitoring\scripts\stop-monitoring.bat
echo ==========================================
echo.
