# app module

Repository

- GitHub: https://github.com/Joshep-c/ShopPly

Propósito

- Contiene la aplicación Android principal (UI, ViewModels, DI, wiring).
- Punto de integración entre módulos: `data`, `domain`, `core`.

Qué incluye

- `src/main/java/.../ui/` — pantallas en Jetpack Compose y NavGraph.
- `src/main/java/.../di/` — módulos Hilt para repositorios, base de datos y DataStore.
- `src/main/java/.../data/` — adaptadores locales (DAOs/repositorios) específicos de la app.
- `build.gradle.kts` — configuración Gradle del módulo Android.

Cómo compilar (desde la raíz del repo)

```powershell
# Windows PowerShell (usa gradlew.bat)
.\gradlew.bat assembleDebug
```

Clone repo

```powershell
git clone https://github.com/Joshep-c/ShopPly.git
``` 

Quickstart

1. Revisa `app/di` para ver cómo se inyectan repositorios.
2. Ejecuta la app en un emulador Android 11+.
3. Para añadir una pantalla, crea su ViewModel en `ui` y registra ruta en `NavGraph.kt`.

Notas

- Las pruebas unitarias se ubican en `src/test` y los instrumented tests en `src/androidTest`.
- Para cambios en la DB revisa `data/local/database/AppDatabase.kt` y las migraciones.
