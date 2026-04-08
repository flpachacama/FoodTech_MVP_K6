@echo off
setlocal

if "%~1"=="" (
  set TEST_FILE=smoke.test.js
) else (
  set TEST_FILE=%~1
)

if "%TEST_ENV%"=="" set TEST_ENV=dev
if "%ORDER_BASE_URL%"=="" set ORDER_BASE_URL=http://localhost:8081
if "%DELIVERY_BASE_URL%"=="" set DELIVERY_BASE_URL=http://localhost:8080

k6 run --env TEST_ENV=%TEST_ENV% --env ORDER_BASE_URL=%ORDER_BASE_URL% --env DELIVERY_BASE_URL=%DELIVERY_BASE_URL% tests\%TEST_FILE%
