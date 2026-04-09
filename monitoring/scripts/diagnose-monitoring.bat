@echo off
REM diagnose-monitoring.bat
REM Diagnostica problemas con los servicios de monitoreo

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo.^^ Diagnostico de Monitoreo
echo ==========================================
echo.

echo.^^ Estado de Docker:
docker --version
echo.

echo.^^ Contenedores corriendo:
docker ps | findstr "prometheus\|grafana"
if errorlevel 1 (
  echo   ^^ No hay contenedores de monitoreo corriendo
) else (
  echo   ^^ OK
)
echo.

echo.^^ Puertos disponibles:
netstat -ano | findstr ":9090\|:3000"
if errorlevel 1 (
  echo   ^^ OK - Puertos 9090 y 3000 disponibles
) else (
  echo   ^^ ALERTA - Puertos pueden estar en uso
)
echo.

echo.^^ Logs de Prometheus:
docker logs k6-prometheus 2>&1 | tail -20
echo.

echo.^^ Logs de Grafana:
docker logs k6-grafana 2>&1 | tail -20
echo.

echo ==========================================
echo.^^ Fin del Diagnostico
echo ==========================================
echo.
