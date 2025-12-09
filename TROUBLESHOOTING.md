# 🔧 Guía de Solución de Problemas - ShopPly2

## Problema: "No me deja iniciar la app"

### Soluciones paso a paso:

---

## 1️⃣ Limpiar y Reconstruir el Proyecto

### Opción A: Desde Android Studio
1. **Build** → **Clean Project**
2. Esperar a que termine
3. **Build** → **Rebuild Project**
4. Esperar a que compile completamente

### Opción B: Desde Terminal
```bash
# En Windows (cmd)
cd E:\IDNP\ShopPly2
gradlew.bat clean build

# O solo limpiar
gradlew.bat clean
```

---

## 2️⃣ Invalidar Caché de Android Studio

1. **File** → **Invalidate Caches**
2. Seleccionar:
   - ✅ Invalidate and Restart
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
3. Click en **Invalidate and Restart**

---

## 3️⃣ Sincronizar Gradle

1. **File** → **Sync Project with Gradle Files**
2. Esperar a que termine la sincronización
3. Revisar la pestaña **Build** para ver si hay errores

---

## 4️⃣ Verificar Configuración del SDK

### Verificar SDK de Android:
1. **File** → **Project Structure** → **SDK Location**
2. Verificar que la ruta del SDK sea correcta:
   ```
   C:\Users\Joshep\AppData\Local\Android\Sdk
   ```

### Verificar que tengas instalado:
- ✅ Android SDK Platform 35
- ✅ Android SDK Build-Tools 35.0.0 o superior
- ✅ Android Emulator (si usas emulador)

**Configuración:**
1. **Tools** → **SDK Manager**
2. En **SDK Platforms**: Marcar **Android 14.0 (API 35)** o superior
3. En **SDK Tools**: Verificar que estén instalados los build tools

---

## 5️⃣ Eliminar Carpeta Build

A veces archivos corruptos causan problemas:

```bash
# Cerrar Android Studio primero
# Luego eliminar carpetas build:
rd /s /q "E:\IDNP\ShopPly2\app\build"
rd /s /q "E:\IDNP\ShopPly2\build"
rd /s /q "E:\IDNP\ShopPly2\.gradle"

# Luego abrir Android Studio y hacer Rebuild
```

---

## 6️⃣ Verificar Errores Específicos

### Ver el Logcat:
1. Ejecutar la app
2. Ir a **Logcat** (parte inferior de Android Studio)
3. Filtrar por:
   - **Tag: "DatabaseInitializer"** - Ver si la BD se inicializa
   - **Tag: "MainActivity"** - Ver errores en la actividad principal
   - **Error** (nivel de log) - Ver todos los errores

### Errores Comunes:

#### ❌ Error: "Hilt component not found"
**Solución:**
```bash
gradlew.bat clean
gradlew.bat build
```

#### ❌ Error: "Unable to instantiate application"
**Causa:** ShopPlyApplication no se está inicializando
**Solución:**
1. Verificar que `android:name=".ShopPlyApplication"` esté en AndroidManifest.xml
2. Rebuild Project

#### ❌ Error: "Room database schema export"
**Solución:** Ignorar, es solo un warning

#### ❌ Error: "BCrypt not found"
**Solución:** La librería jbcrypt ya está en build.gradle.kts, solo rebuild

---

## 7️⃣ Configuración del Emulador/Dispositivo

### Si usas Emulador:
1. **Tools** → **Device Manager**
2. Crear un nuevo dispositivo si es necesario:
   - **Device:** Pixel 5 o similar
   - **System Image:** API 35 (Android 14.0)
   - **RAM:** Mínimo 2GB

### Si usas Dispositivo Físico:
1. Habilitar **Opciones de Desarrollador**
2. Activar **Depuración USB**
3. Conectar el dispositivo
4. Aceptar el diálogo de autorización en el teléfono

---

## 8️⃣ Verificar Dependencias

Verificar que todas las dependencias se descarguen:

```bash
gradlew.bat --refresh-dependencies
```

---

## 9️⃣ Modo Debug: Ver Logs de Inicialización

La app tiene logs detallados. Al iniciar verás:

```
D/DatabaseInitializer: Iniciando inicialización de base de datos...
D/DatabaseInitializer: USUARIOS:
D/DatabaseInitializer: - Total: 6
D/DatabaseInitializer: - Compradores: 2
D/DatabaseInitializer: - Vendedores: 3
D/DatabaseInitializer: - Admins: 1
D/DatabaseInitializer: TIENDAS:
D/DatabaseInitializer: - Total: 3
...
```

Si no ves estos logs, el problema está en la inicialización.

---

## 🔟 Solución Nuclear (Último Recurso)

Si nada funciona:

1. **Cerrar Android Studio**
2. **Eliminar carpetas:**
   ```bash
   rd /s /q "E:\IDNP\ShopPly2\.gradle"
   rd /s /q "E:\IDNP\ShopPly2\.idea"
   rd /s /q "E:\IDNP\ShopPly2\app\build"
   rd /s /q "E:\IDNP\ShopPly2\build"
   ```
3. **Abrir Android Studio**
4. **File** → **Open** → Seleccionar `E:\IDNP\ShopPly2`
5. **Esperar** a que Gradle sincronice (puede tardar 5-10 minutos)
6. **Build** → **Rebuild Project**
7. **Run**

---

## 📱 Verificar que la App Funciona

Cuando la app inicie correctamente, deberías ver:

1. **SplashScreen** (1-2 segundos)
2. **AuthScreen** (pantalla de bienvenida)
3. Opciones para:
   - Iniciar Sesión
   - Registrarse

### Usuarios de Prueba:

```
Administrador:
Email: admin@shopply.pe
Password: admin123

Vendedor:
Email: maria.garcia@email.com
Password: seller123

Comprador:
Email: carlos.lopez@email.com
Password: buyer123
```

---

## 📊 Verificar que la Base de Datos se Creó

Si la app inicia pero no hay datos:

1. Ver el **Logcat**
2. Buscar: `DatabaseInitializer`
3. Deberías ver:
   - 6 usuarios creados
   - 3 tiendas creadas
   - 8 categorías creadas
   - 12+ productos creados

---

## 🆘 Si Aún No Funciona

Comparte:
1. **Mensaje de error completo** del Logcat
2. **Captura de pantalla** del error
3. **Versión de Android Studio** (Help → About)
4. **Sistema operativo** (Windows 10/11)

---

## ✅ Checklist de Verificación

Antes de pedir ayuda, verifica:

- [ ] Android Studio actualizado a última versión
- [ ] SDK API 35 instalado
- [ ] Gradle sync exitoso (sin errores rojos)
- [ ] Clean + Rebuild realizado
- [ ] Caché invalidado
- [ ] Dispositivo/Emulador configurado
- [ ] Logs revisados en Logcat
- [ ] Internet disponible (para descargar dependencias)

---

## 🎯 Errores Frecuentes y Soluciones Rápidas

| Error | Solución |
|-------|----------|
| App se cierra inmediatamente | Ver Logcat, buscar "FATAL EXCEPTION" |
| Pantalla negra | Verificar Theme en styles.xml |
| "Unable to resolve dependency" | `gradlew.bat --refresh-dependencies` |
| "Duplicate class found" | `gradlew.bat clean build` |
| Emulador muy lento | Habilitar aceleración de hardware (HAXM/Hyper-V) |

---

**Última actualización:** 2025-12-09

