@echo off
REM Stop Liberty Server Script (Windows)
REM This script properly stops the Liberty server and frees the port

echo ==========================================
echo Stopping Liberty Server
echo ==========================================
echo.

REM Try Maven stop command first
echo Attempting to stop Liberty server via Maven...
call mvn liberty:stop 2>nul
timeout /t 2 /nobreak > nul

echo.
echo Checking for processes on port 9081...

REM Find and kill process using port 9081
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :9081 ^| findstr LISTENING') do (
    set PID=%%a
    goto KILL_PROCESS
)

echo √ Port 9081 is free
goto CHECK_JAVA

:KILL_PROCESS
echo Found process %PID% using port 9081
echo Killing process...
taskkill /F /PID %PID% >nul 2>&1
timeout /t 1 /nobreak > nul

REM Verify it's killed
netstat -aon | findstr :9081 | findstr LISTENING >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo × Failed to free port 9081
    echo You may need to manually kill the process with: taskkill /F /PID %PID%
) else (
    echo √ Process killed, port 9081 is now free
)

:CHECK_JAVA
echo.
echo Checking for Liberty Java processes...

REM Kill any Java processes with "liberty" in command line
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /V ^| findstr /I "liberty"') do (
    echo Found Liberty process: %%a
    taskkill /F /PID %%a >nul 2>&1
)

echo √ Liberty processes terminated

echo.
echo ==========================================
echo √ Liberty Server Stopped
echo ==========================================
echo.
pause

@REM Made with Bob
