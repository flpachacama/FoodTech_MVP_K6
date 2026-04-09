@echo off
REM stop-monitoring.bat
REM Detiene servicios de monitoreo

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set MONITORING_DIR=%SCRIPT_DIR:~0,-9%

echo.
echo ==========================================
echo.^^ Deteniendo servicios de monitoreo
echo ==========================================
echo.

cd /d "%MONITORING_DIR%"

if not exist docker-compose.yml (
  echo ^^!^^! Error: docker-compose.yml no encontrado
  exit /b 1
)

echo.^^ Deteniendo contenedores...
docker-compose down

echo.
echo ==========================================
echo.^^ Servicios detenidos exitosamente
echo ==========================================
echo.
echo Nota: Los volumenes de datos persisten. Para eliminarlos:
echo    docker-compose down -v
echo ==========================================
echo.
