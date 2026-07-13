@echo off
echo ===================================================
echo   Compilando AliGest y Pruebas Unitarias...
echo ===================================================
if not exist bin (
    mkdir bin
)

:: 1. Compilar clases del modelo, negocio y GUI
javac -d bin -sourcepath src src/com/aligest/Main.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Error durante la compilacion de fuentes.
    if "%1" neq "nopause" pause
    exit /b %errorlevel%
)

:: 2. Compilar clases de prueba
javac -d bin -cp "bin;lib/*" -sourcepath test test/com/aligest/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Error durante la compilacion de pruebas unitarias.
    if "%1" neq "nopause" pause
    exit /b %errorlevel%
)

echo.
echo ===================================================
echo   Ejecutando Pruebas Unitarias con JUnit...
echo ===================================================
java -cp "bin;lib/*" org.junit.runner.JUnitCore com.aligest.AllTestsSuite

if %errorlevel% neq 0 (
    echo.
    echo.
    echo [FALLO] Algunas pruebas unitarias fallaron.
    if "%1" neq "nopause" pause
    exit /b %errorlevel%
)

echo.
echo ===================================================
echo   [EXITO] TODAS LAS PRUEBAS PASARON CORRECTAMENTE.
echo ===================================================
if "%1" neq "nopause" pause
