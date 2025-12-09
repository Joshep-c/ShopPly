# 🚀 SOLUCIÓN RÁPIDA - Cómo Iniciar ShopPly2

## ⚡ SOLUCIÓN EN 3 PASOS

### 1️⃣ LIMPIAR Y SINCRONIZAR

En **Android Studio**:

1. `Build` → `Clean Project` (esperar 10-30 segundos)
2. `File` → `Sync Project with Gradle Files` (esperar 1-3 minutos)
3. `Build` → `Rebuild Project` (esperar 1-2 minutos)

### 2️⃣ CONFIGURAR DISPOSITIVO

**Opción A - Emulador:**
- `Tools` → `Device Manager` → Create Device
- Seleccionar: **Pixel 5**
- System Image: **API 35 (Android 14)**
- Finish

**Opción B - Dispositivo Físico:**
- Activar "Opciones de Desarrollador" en tu teléfono
- Activar "Depuración USB"
- Conectar con cable USB
- Autorizar en el teléfono

### 3️⃣ EJECUTAR

Presionar el botón verde ▶️ `Run 'app'`

**O:**

`Run` → `Run 'app'`

---

## ✅ LA APP DEBE MOSTRAR:

1. **SplashScreen** (logo, 1-2 segundos)
2. **Pantalla de Bienvenida** con botones:
   - 🔑 Iniciar Sesión
   - 📝 Crear Cuenta

---

## 🔐 USUARIOS DE PRUEBA

Puedes iniciar sesión con:

### Admin
```
Email: admin@shopply.pe
Password: admin123
```

### Vendedor
```
Email: tech@arequipa.pe
Password: seller123
```

### Comprador
```
Email: comprador@gmail.com
Password: buyer123
```

---

## ❌ SI ALGO FALLA

### Error: "Gradle sync failed"

**Solución:**
```
File > Invalidate Caches > Invalidate and Restart
```

Luego repetir los pasos 1-3.

### Error: "Unable to locate SDK"

**Solución:**
1. `File` → `Project Structure` → `SDK Location`
2. Verificar que apunte a: `C:\Users\Joshep\AppData\Local\Android\Sdk`
3. Si no existe, descargar SDK desde `Tools` → `SDK Manager`

### Error: App se cierra inmediatamente

**Solución:**
1. Abrir pestaña `Logcat` (parte inferior)
2. Filtrar por: `Error`
3. Copiar el error y buscar ayuda
4. O ver el archivo `TROUBLESHOOTING.md` para más detalles

---

## 🎯 ALTERNATIVA: Script de Compilación

Si prefieres compilar desde la terminal:

1. Abre `cmd` o `PowerShell`
2. Navega a: `E:\IDNP\ShopPly2`
3. Ejecuta:
   ```cmd
   build_app.bat
   ```

Esto limpiará y compilará el proyecto automáticamente.

---

## 📋 CHECKLIST RÁPIDO

Antes de ejecutar, verificar:

- [ ] Android Studio abierto
- [ ] Internet conectado (primera vez)
- [ ] Gradle sync sin errores rojos
- [ ] Emulador o dispositivo conectado
- [ ] Botón verde ▶️ habilitado

---

## 📚 MÁS INFORMACIÓN

- **Análisis Completo:** Ver `ANALISIS_COMPLETO.md`
- **Guía de Problemas:** Ver `TROUBLESHOOTING.md`
- **Documentación:** Ver `README.md`

---

## 🎉 ¡ESO ES TODO!

El proyecto está completamente funcional. Solo necesitas compilarlo correctamente siguiendo los 3 pasos arriba.

Si sigues teniendo problemas después de estos pasos, revisa los archivos de ayuda mencionados arriba.

**¡Buena suerte! 🚀**

