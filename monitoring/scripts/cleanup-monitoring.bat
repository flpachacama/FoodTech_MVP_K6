@echo off
REM cleanup-monitoring.bat
REM Limpia todos los contenedores y volumenes de monitoreo

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set MONITORING_DIR=%SCRIPT_DIR:~0,-9%

echo.
echo ==========================================
echo.^^ Limpiando servicios de monitoreo
echo ==========================================
echo.

cd /d "%MONITORING_DIR%"

echo.^^ Deteniendo contenedores...
docker-compose down

echo.^^ Eliminando volumenes...
docker-compose down -v

echo.^^ Purgando imagenes...
docker image prune -f

echo.
echo ==========================================
echo.^^ Limpieza completada
echo ==========================================
echo.
echo Proximos pasos:
echo    .\monitoring\scripts\start-monitoring.bat
echo.
