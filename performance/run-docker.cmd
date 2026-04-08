@echo off
setlocal

if "%~1"=="" (
  set TEST_FILE=smoke.test.js
) else (
  set TEST_FILE=%~1
)

if "%TEST_ENV%"=="" set TEST_ENV=dev
if "%ORDER_BASE_URL%"=="" set ORDER_BASE_URL=http://host.docker.internal:8081
if "%DELIVERY_BASE_URL%"=="" set DELIVERY_BASE_URL=http://host.docker.internal:8080

docker build -t foodtech-k6 .
docker run --rm -v "%cd%\reports:/performance/reports" -e TEST_ENV=%TEST_ENV% -e ORDER_BASE_URL=%ORDER_BASE_URL% -e DELIVERY_BASE_URL=%DELIVERY_BASE_URL% foodtech-k6 run tests/%TEST_FILE%
