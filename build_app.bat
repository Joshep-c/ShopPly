@echo off
echo ====================================
echo   ShopPly2 - Script de Compilacion
echo ====================================
echo.

cd /d E:\IDNP\ShopPly2

echo [1/4] Limpiando proyecto...
call gradlew.bat clean
if errorlevel 1 (
    echo ERROR: Fallo al limpiar el proyecto
    pause
    exit /b 1
)

echo.
echo [2/4] Compilando proyecto...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo ERROR: Fallo al compilar el proyecto
    echo Revisa los errores arriba
    pause
    exit /b 1
)

echo.
echo [3/4] Verificando APK generado...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo APK generado exitosamente!
    echo Ubicacion: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ADVERTENCIA: No se encontro el APK
)

echo.
echo [4/4] Proceso completado!
echo.
echo ====================================
echo   SIGUIENTE PASO:
echo   Abre Android Studio y ejecuta
echo   Run ^> Run 'app'
echo ====================================
echo.
pause

