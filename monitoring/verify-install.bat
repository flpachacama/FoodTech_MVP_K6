@echo off
REM verify-install.bat
REM Verificacion de instalacion: Monitoreo k6 con Prometheus + Grafana

setlocal enabledelayedexpansion

echo.
echo ==========================================
echo.^^ Verificacion de instalacion - Monitoreo k6
echo ==========================================
echo.

set TOTAL=0
set PASSED=0

setlocal enabledelayedexpansion

echo.^^ Herramientas necesarias:

REM Verificar k6
set /a TOTAL+=1
where k6 > nul 2>&1
if !errorlevel! equ 0 (
  echo.^^ k6 instalado: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ k6 instalado: ^[FALTA^]
)

REM Verificar docker
set /a TOTAL+=1
where docker > nul 2>&1
if !errorlevel! equ 0 (
  echo.^^ docker instalado: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ docker instalado: ^[FALTA^]
)

REM Verificar docker-compose
set /a TOTAL+=1
where docker-compose > nul 2>&1
if !errorlevel! equ 0 (
  echo.^^ docker-compose instalado: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ docker-compose instalado: ^[FALTA^]
)

echo.
echo.^^ Archivos de configuracion:

REM Verificar archivos
set /a TOTAL+=1
if exist "monitoring\docker-compose.yml" (
  echo.^^ docker-compose.yml: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ docker-compose.yml: ^[FALTA^]
)

set /a TOTAL+=1
if exist "monitoring\prometheus.yml" (
  echo.^^ prometheus.yml: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ prometheus.yml: ^[FALTA^]
)

set /a TOTAL+=1
if exist "monitoring\provisioning\datasources\prometheus.yml" (
  echo.^^ datasource provisioning: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ datasource provisioning: ^[FALTA^]
)

echo.
echo.^^ Scripts de ejecucion:

set /a TOTAL+=1
if exist "monitoring\scripts\start-monitoring.bat" (
  echo.^^ start-monitoring.bat: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ start-monitoring.bat: ^[FALTA^]
)

set /a TOTAL+=1
if exist "monitoring\scripts\stop-monitoring.bat" (
  echo.^^ stop-monitoring.bat: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ stop-monitoring.bat: ^[FALTA^]
)

set /a TOTAL+=1
if exist "monitoring\scripts\run-k6-prometheus.bat" (
  echo.^^ run-k6-prometheus.bat: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ run-k6-prometheus.bat: ^[FALTA^]
)

echo.
echo.^^ Documentacion:

set /a TOTAL+=1
if exist "monitoring\README.md" (
  echo.^^ README.md: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ README.md: ^[FALTA^]
)

set /a TOTAL+=1
if exist "monitoring\QUICKSTART.md" (
  echo.^^ QUICKSTART.md: ^[OK^]
  set /a PASSED+=1
) else (
  echo.^^ QUICKSTART.md: ^[FALTA^]
)

echo.
echo ==========================================
echo.^^ Resumen
echo ==========================================
echo.
echo Verificaciones: !PASSED! / !TOTAL!
echo.

if !PASSED! equ !TOTAL! (
  echo.^^ ^! Todo listo para empezar^!
  echo.
  echo Proximos pasos:
  echo   1. Levantar servicios:
  echo      .\monitoring\scripts\start-monitoring.bat
  echo      o
  echo      npm run monitoring:start
  echo.
  echo   2. Ejecutar k6 con Prometheus:
  echo      .\monitoring\scripts\run-k6-prometheus.bat smoke.test.js
  echo      o
  echo      npm run perf:smoke:prom
  echo.
  echo   3. Ver resultados en Grafana:
  echo      http://localhost:3000
  echo.
) else (
  echo.^^ ^! Algunas verificaciones fallaron
  echo.
  echo Por favor revisa:
  echo   - ^? Esta k6 instalado^? ^(k6 version^)
  echo   - ^? Esta Docker corriendo^?
  echo   - ^? Estan todos los archivos en su lugar^?
  echo.
  echo Para mas informacion: type monitoring\README.md
)

echo.
