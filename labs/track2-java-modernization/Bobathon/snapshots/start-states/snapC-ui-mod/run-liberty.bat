@echo off
REM Simple Pharmacy - Liberty Build & Run Script (Windows)
REM This script builds the application and runs it on Liberty server

echo ==========================================
echo Simple Pharmacy - Liberty Build ^& Run
echo ==========================================
echo.

REM Clean and build
echo [1/3] Building application...
call mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)

echo.
echo [2/3] Starting Liberty server...
start /B mvn liberty:run

REM Wait for server to start
echo.
echo Waiting for Liberty server to start...
timeout /t 5 /nobreak > nul

REM Check if server is running (simple approach for Windows)
set MAX_ATTEMPTS=30
set ATTEMPT=0

:CHECK_SERVER
if %ATTEMPT% GEQ %MAX_ATTEMPTS% goto SERVER_TIMEOUT
curl -s http://localhost:9081 > nul 2>&1
if %ERRORLEVEL% EQU 0 goto SERVER_READY
set /A ATTEMPT+=1
echo|set /p=.
timeout /t 2 /nobreak > nul
goto CHECK_SERVER

:SERVER_TIMEOUT
echo.
echo.
echo Server failed to start within expected time.
echo Please check the logs manually.
goto SHOW_URLS

:SERVER_READY
echo.
echo.
echo ==========================================
echo √ Liberty Server Started Successfully!
echo ==========================================
echo.
echo Application URLs:
echo   Dashboard:      http://localhost:9081/simple-pharmacy.war/dashboard
echo   Prescriptions:  http://localhost:9081/simple-pharmacy.war/prescription-list
echo   Orders:         http://localhost:9081/simple-pharmacy.war/order-list
echo   Medicines:      http://localhost:9081/simple-pharmacy.war/medicine-list
echo.
echo Press Ctrl+C to stop the server
echo.

:SHOW_URLS
REM Keep window open
pause

@REM Made with Bob
