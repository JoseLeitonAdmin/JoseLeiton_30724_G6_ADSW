@echo off
echo ===================================================
echo   Compilando AliGest en Java...
echo ===================================================
if not exist bin (
    mkdir bin
)

javac -d bin -sourcepath src src/com/aligest/Main.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Error durante la compilacion.
    pause
    exit /b %errorlevel%
)

echo.
echo ===================================================
echo   Iniciando AliGest (Condominio La Primavera)...
echo ===================================================
java -cp bin com.aligest.Main
