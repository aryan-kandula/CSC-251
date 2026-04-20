@echo off
echo ============================================================
echo   Concrete Pad ^& Chain-Link Fence Estimator
echo   Author: Aryan Kandula  ^|  CSC-251  ^|  Module 5
echo ============================================================

:: Create output directories if they don't exist
if not exist out      mkdir out
if not exist estimates mkdir estimates

:: Compile all Java source files
echo.
echo Compiling Java source files...
javac -d out -sourcepath src src\Main.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed.
    echo         Make sure Java JDK 17 or later is installed.
    echo         Download from: https://adoptium.net/
    pause
    exit /b 1
)

echo.
echo [OK] Compilation complete!
echo.
echo Starting application...
echo ============================================================
echo.

:: Run from project root so the data/ folder resolves correctly
java -cp out Main

pause
