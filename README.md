# 🛍️ ShopPly - E-Commerce para PYMEs Peruanas

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

**App para pequeñas y medianas empresas peruanas**

[Características](#-características) • [Arquitectura](#-arquitectura) • [Instalación](#-instalación) • [Documentación](#-documentación)

</div>

---

## 📋 Descripción del Proyecto

**ShopPly** es una aplicación móvil de comercio electrónico diseñada específicamente para conectar a compradores con pequeñas y medianas empresas (PYMEs) peruanas. La plataforma permite a los vendedores crear sus tiendas virtuales y a los compradores acceder a productos locales de calidad.

### 🎯 Objetivos

- ✅ Facilitar la digitalización de PYMEs peruanas
- ✅ Proporcionar una plataforma de ventas accesible y profesional
- ✅ Conectar compradores con productos locales
- ✅ Implementar un sistema de autenticación seguro y eficiente
- ✅ Ofrecer una experiencia de usuario moderna con Jetpack Compose

---

## ✨ Características

### 🔐 Sistema de Autenticación Completo

- **Registro de Usuarios**
  - Selector de tipo de cuenta (Comprador/Vendedor)
  - Validación de campos en tiempo real
  - Confirmación de contraseña
  - Campos opcionales (teléfono)
  
- **Inicio de Sesión**
  - Validación de credenciales
  - Toggle de visibilidad de contraseña
  - Manejo de errores amigable
  - Auto-login con persistencia de sesión

- **Gestión de Sesión**
  - Persistencia con DataStore
  - Auto-login al reiniciar la app
  - Logout seguro con limpieza de datos
  - SplashScreen profesional durante verificación

### 👥 Tipos de Usuario

#### 🛒 **Comprador**
- Visualizar catálogo de productos
- Agregar productos al carrito
- Realizar compras
- Ver historial de órdenes

#### 🏪 **Vendedor (PYME)**
- Crear y gestionar tienda
- Publicar productos
- Gestionar inventario
- Procesar órdenes

---

## 🏗️ Arquitectura

### **Clean Architecture + MVVM Pattern**

```
┌─────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                │
│  ┌───────────────────────────────────────────────┐  │
│  │ MainActivity                                  │  │
│  │ ├─ MainViewModel → Estados de autenticación  │  │
│  │ └─ SplashScreen → Animaciones                │  │
│  ├───────────────────────────────────────────────┤  │
│  │ Auth Screens                                  │  │
│  │ ├─ AuthScreen → Pantalla de bienvenida       │  │
│  │ ├─ LoginScreen → Formulario de login         │  │
│  │ ├─ SignUpScreen → Registro con selector      │  │
│  │ └─ AuthViewModel → Lógica de autenticación   │  │
│  ├───────────────────────────────────────────────┤  │
│  │ Home Screen                                   │  │
│  │ ├─ HomeScreen → Dashboard personalizado      │  │
│  │ └─ HomeViewModel → Estado del usuario        │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────┐
│              Domain Layer (Repositories)            │
│  ┌───────────────────────────────────────────────┐  │
│  │ UserRepository                                │  │
│  │ ├─ login() → Validación y sesión             │  │
│  │ ├─ register() → Creación de usuario          │  │
│  │ ├─ logout() → Limpieza de sesión             │  │
│  │ ├─ getCurrentUser() → Usuario actual         │  │
│  │ └─ isLoggedIn() → Verificación de sesión     │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────┐
│                 Data Layer                          │
│  ┌──────────────────┐      ┌────────────────────┐  │
│  │  Room Database   │      │    DataStore       │  │
│  │  ┌────────────┐  │      │  ┌──────────────┐ │  │
│  │  │ UserDao    │  │      │  │ Preferences  │ │  │
│  │  │ ProductDao │  │      │  │ - Session ID │ │  │
│  │  │ OrderDao   │  │      │  └──────────────┘ │  │
│  │  └────────────┘  │      └────────────────────┘  │
│  └──────────────────┘                              │
└─────────────────────────────────────────────────────┘
```

### **Patrón MVVM**

```kotlin
View (Compose) ←→ ViewModel ←→ Repository ←→ DataSource
     │                │              │             │
     │                │              │             ├─ Room DB
     │                │              │             └─ DataStore
     │                │              │
     │                │              └─ Business Logic
     │                └─ UI States (StateFlow)
     └─ Composables reactivos
```

---

## 🛠️ Stack Tecnológico

### **Frontend**
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Sistema de diseño de Google
- **Compose Navigation** - Navegación type-safe

### **Arquitectura**
- **MVVM Pattern** - Separación de responsabilidades
- **Clean Architecture** - Código mantenible y testeable
- **StateFlow** - Gestión de estados reactivos
- **Coroutines** - Programación asíncrona

### **Persistencia**
- **Room Database** - Base de datos local (SQLite)
- **DataStore** - Preferencias y sesiones
- **Type Converters** - Conversión de tipos complejos

### **Inyección de Dependencias**
- **Hilt** - Inyección de dependencias de Dagger

---

## 📦 Instalación

### **Requisitos Previos**

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 11 o superior
- SDK de Android 24+ (Android 7.0 Nougat)
- Gradle 8.0+

### **Clonar el Repositorio**

```bash
git clone https://github.com/tu-usuario/ShopPly2.git
cd ShopPly2
```

### **Configurar el Proyecto**

1. Abrir el proyecto en Android Studio
2. Esperar a que Gradle sincronice las dependencias
3. Conectar un dispositivo Android o iniciar un emulador
4. Ejecutar la aplicación con `Run > Run 'app'`

### **Build desde Terminal**

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Ejecutar tests
./gradlew test

# Instalar en dispositivo
./gradlew installDebug
```

---

## 📱 Estructura del Proyecto

```
ShopPly2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/shopply/appEcommerce/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   │   ├── DatabaseInitializer.kt
│   │   │   │   │   │   │   └── Converters.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   │   ├── ProductDao.kt
│   │   │   │   │   │   │   └── OrderDao.kt
│   │   │   │   │   │   └── entities/
│   │   │   │   │   │       ├── User.kt
│   │   │   │   │   │       ├── Product.kt
│   │   │   │   │   │       └── Order.kt
│   │   │   │   │   ├── preferences/
│   │   │   │   │   │   └── UserPreferences.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── UserRepository.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── DataStoreModule.kt
│   │   │   │   │   └── RepositoryModule.kt
│   │   │   │   ├── domain/
│   │   │   │   │   └── model/
│   │   │   │   │       └── Result.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── AuthScreen.kt
│   │   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   └── SignUpScreen.kt
│   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   ├── main/
│   │   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │   └── MainViewModel.kt
│   │   │   │   │   ├── splash/
│   │   │   │   │   │   └── SplashScreen.kt
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   │   └── Screen.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   └── ShopPlyApp.kt
│   │   │   └── res/
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── README.md
```

---

## 🔐 Sistema de Autenticación

### **Modelo de Datos**

#### **Entidad User**

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,              // Único
    val passwordHash: String,       // Hasheado
    val name: String,
    val phone: String? = null,
    val userRole: UserRole,         // BUYER | SELLER | ADMIN
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    BUYER,    // Comprador
    SELLER,   // Vendedor (PYME)
    ADMIN     // Administrador
}
```

### **Flujo de Autenticación**

#### **1. Registro de Usuario**

```
Usuario → SignUpScreen
    ↓
Selecciona tipo (Comprador/Vendedor)
    ↓
Llena formulario
    ↓
AuthViewModel.register()
    ↓
UserRepository.register()
    ↓
├─ Validar email único
├─ Hash de contraseña
├─ Insertar en Room DB
└─ Guardar sesión en DataStore
    ↓
Navegar a HomeScreen
```

#### **2. Inicio de Sesión**

```
Usuario → LoginScreen
    ↓
Ingresa credenciales
    ↓
AuthViewModel.login()
    ↓
UserRepository.login()
    ↓
├─ Buscar usuario por email
├─ Verificar contraseña
└─ Guardar sesión en DataStore
    ↓
Navegar a HomeScreen
```

#### **3. Auto-Login**

```
App inicia → MainActivity
    ↓
MainViewModel.init()
    ↓
UserRepository.isLoggedIn()
    ↓
Consultar DataStore
    ↓
┌──────┴──────┐
│             │
Hay sesión   No hay
│             │
▼             ▼
HomeScreen   AuthScreen
```

### **Gestión de Estados**

```kotlin
// MainViewModel - Estado de la app
sealed class MainUiState {
    data object Loading          // Verificando sesión
    data object Authenticated    // Usuario logueado
    data object Unauthenticated  // Sin sesión
}

// AuthViewModel - Estado de autenticación
sealed class AuthUiState {
    data object Idle             // Estado inicial
    data object Loading          // Procesando login/registro
    data class Success(message)  // Operación exitosa
    data class Error(message)    // Error con mensaje
}

// HomeViewModel - Estado del home
sealed class HomeUiState {
    data object Loading          // Cargando datos
    data class Success(user)     // Usuario cargado
    data class Error(message)    // Error al cargar
}
```

---

## 🎨 Interfaz de Usuario

### **Pantallas Implementadas**

#### **1. SplashScreen** 
- Animaciones de fade-in
- Gradientes modernos
- Verificación de sesión en background

#### **2. AuthScreen**
- Presentación de la app
- Botones para Login/SignUp
- Diseño minimalista

#### **3. LoginScreen**
- Campos de email y contraseña
- Toggle de visibilidad de contraseña
- Validación en tiempo real
- Link a registro

#### **4. SignUpScreen**
- Selector de tipo de cuenta (Comprador/Vendedor)
- Formulario completo
- Confirmación de contraseña
- Validaciones exhaustivas

#### **5. HomeScreen**
- Dashboard personalizado por rol
- Información del usuario
- Botón de logout
- Mensajes de bienvenida

### **Componentes Reutilizables**

- **FilterChip** - Selector de tipo de cuenta
- **OutlinedTextField** - Campos de entrada
- **Button/OutlinedButton** - Acciones primarias/secundarias
- **Card** - Contenedores de información
- **TopAppBar** - Barra de navegación

---

## 📊 Base de Datos

### **Esquema de Base de Datos**

```sql
-- Tabla de Usuarios
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    passwordHash TEXT NOT NULL,
    name TEXT NOT NULL,
    phone TEXT,
    userRole TEXT NOT NULL,
    isBanned INTEGER DEFAULT 0,
    createdAt INTEGER NOT NULL,
    lastLoginAt INTEGER NOT NULL
);

-- Índices
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(userRole);
```

### **Relaciones (Futuras)**

```
users (1) ───< (N) products
  │
  └───< (N) orders
        │
        └───< (N) order_items
```

---

## 🔒 Seguridad

### **Implementado**

- ✅ Hash de contraseñas (simulado - usar bcrypt en producción)
- ✅ Validación de emails únicos
- ✅ DataStore encriptado por el SO
- ✅ Índices de BD para consultas rápidas
- ✅ Validación de entrada en todos los formularios

### **Por Implementar**

- ⚠️ Hash real con BCrypt/Argon2
- ⚠️ Tokens JWT para API
- ⚠️ Refresh tokens
- ⚠️ Rate limiting en login
- ⚠️ Encriptación de BD con SQLCipher
- ⚠️ Biometría (huella/Face ID)

---

## 🚀 Roadmap

### **Fase 1: Autenticación**
- [x] Sistema de registro
- [x] Sistema de login
- [x] Gestión de sesión
- [x] Auto-login
- [x] Pantalla de inicio (Home)

### **Fase 2: Catálogo de Productos** 🚧 En Progreso
- [ ] CRUD de productos (vendedores)
- [ ] Listado de productos
- [ ] Detalle de producto
- [ ] Búsqueda y filtros
- [ ] Categorías

### **Fase 3: Carrito y Órdenes** 📅 Planificado
- [ ] Carrito de compras
- [ ] Proceso de checkout
- [ ] Gestión de órdenes
- [ ] Historial de compras

### **Fase 4: Backend y API** 📅 Planificado
- [ ] API REST con Ktor/Spring Boot
- [ ] Sincronización en tiempo real
- [ ] Sistema de notificaciones
- [ ] Gestión de imágenes (Cloud Storage)

### **Fase 5: Pagos** 📅 Planificado
- [ ] Integración con pasarelas peruanas
- [ ] Culqi/Niubiz/Mercado Pago
- [ ] Gestión de transacciones

---

## 👨‍💻 Desarrollo

### **Convenciones de Código**

- **Kotlin Coding Conventions** - Estilo oficial de Kotlin
- **Clean Code** - Principios de código limpio
- **SOLID Principles** - Principios de diseño orientado a objetos

---

## 📄 Licencia

Este proyecto es desarrollado con fines educativos para el curso de Desarrollo de Aplicaciones Móviles.

---

## 👥 Equipo

- **Desarrollador Principal** - Implementación de arquitectura MVVM y sistema de autenticación
- **Instructor** - Guía y supervisión del proyecto

---

## 🙏 Agradecimientos

- [Android Developers](https://developer.android.com/) - Documentación oficial
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Framework de UI
- [Philipp Lackner](https://www.youtube.com/@PhilippLackner) - Tutoriales de Android
- [Google Codelabs](https://codelabs.developers.google.com/) - Guías prácticas

---

<div align="center">

⭐️ Si te gusta este proyecto, dale una estrella en GitHub




## 🔄 Flujo de la Actividad

```
┌─────────────────┐
│   MainActivity  │
│   (Hilt Entry)  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    NavGraph     │
│ startDestination│
│  = "auth"       │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   AuthScreen    │──────┐
│  (Bienvenida)   │      │
└────────┬────────┘      │
         │               │
    ┌────┴────┐          │
    ▼         ▼          │
┌─────────┐ ┌─────────┐ │
│ Login   │ │ SignUp  │◄┘
│ Screen  │ │ Screen  │
└────┬────┘ └────┬────┘
     │           │
     └─────┬─────┘
           │
           ▼
    ┌──────────────┐
    │ AuthViewModel│
    │  - login()   │
    │  - register()│
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │ UserRepository
    │  - Valida    │
    │  - Guarda    │
    │  - Sesión    │
    └──────┬───────┘
           │
      ┌────┴────┐
      ▼         ▼
┌─────────┐ ┌──────────┐
│Room DB  │ │DataStore │
│(Users)  │ │(Session) │
└─────────┘ └──────────┘
           │
           ▼
    ┌──────────────┐
    │ isLoggedIn   │
    │   = true     │
    └──────┬───────┘
           │
           ▼
    ┌──────────────┐
    │ HomeScreen   │
    │ (Bienvenida) │
    │   + Logout   │
    └──────────────┘
```

---

### **Flujo Detallado de Login**

1. **Usuario ingresa credenciales** → `LoginScreen`
2. **ViewModel valida entrada** → `AuthViewModel.login()`
3. **Repository consulta BD** → `UserRepository.login()`
   ```kotlin
   val user = userDao.getUserByEmail(email)
   if (user.passwordHash != hashPassword(password)) {
       return Result.Error("Contraseña incorrecta")
   }
   ```
4. **Guarda sesión** → `DataStore.edit { it[CURRENT_USER_ID] = user.id }`
5. **Actualiza estado** → `isLoggedIn = true`
6. **LaunchedEffect detecta cambio** → Navega a Home
7. **HomeViewModel carga datos** → `userRepository.getCurrentUser()`
8. **HomeScreen muestra perfil** del usuario

---

### **Flujo de Registro**

1. **Usuario selecciona tipo de cuenta** (Comprador/Vendedor)
2. **Llena formulario** → `SignUpScreen`
3. **ViewModel valida** (contraseñas coinciden, email válido, etc.)
4. **Repository verifica duplicados**:
   ```kotlin
   if (userDao.getUserByEmail(email) != null) {
       return Result.Error("Email ya registrado")
   }
   ```
5. **Crea usuario en BD**:
   ```kotlin
   val newUser = User(
       email = email,
       name = name,
       passwordHash = hashPassword(password),
       userRole = if (isBusinessAccount) SELLER else BUYER
   )
   val userId = userDao.insertUser(newUser)
   ```
6. **Guarda sesión automáticamente**
7. **Navega a Home** (mismo flujo que login)

---

### **Flujo de Cierre de Sesión**

1. **Usuario presiona botón Logout** → `HomeScreen`
2. **HomeViewModel ejecuta** → `userRepository.logout()`
3. **Limpia DataStore** → `dataStore.edit { it.clear() }`
4. **Navega a Auth** → Limpia backstack completo
5. **Usuario regresa a pantalla de bienvenida**

---

## 📂 Estructura de Archivos Creados

```
app/
├── build.gradle.kts                          [MODIFICADO]
├── src/main/java/com/shopply/appEcommerce/
    ├── data/
    │   ├── local/
    │   │   ├── database/
    │   │   │   ├── AppDatabase.kt            [CREADO]
    │   │   │   └── DatabaseInitializer.kt    [CREADO]
    │   │   ├── dao/
    │   │   │   └── UserDao.kt                [CREADO]
    │   │   └── entities/
    │   │       └── User.kt                   [CREADO]
    │   ├── preferences/
    │   │   └── UserPreferences.kt            [CREADO]
    │   └── repository/
    │       └── UserRepository.kt             [CREADO]
    ├── di/
    │   ├── DatabaseModule.kt                 [CREADO]
    │   ├── DataStoreModule.kt                [CREADO]
    │   └── RepositoryModule.kt               [CREADO]
    ├── domain/
    │   └── model/
    │       └── Result.kt                     [CREADO]
    ├── ui/
    │   ├── auth/
    │   │   ├── AuthScreen.kt                 [CREADO]
    │   │   ├── AuthViewModel.kt              [CREADO]
    │   │   ├── LoginScreen.kt                [CREADO]
    │   │   └── SignUpScreen.kt               [CREADO]
    │   ├── home/
    │   │   ├── HomeScreen.kt                 [CREADO]
    │   │   └── HomeViewModel.kt              [CREADO]
    │   ├── main/
    │   │   └── MainActivity.kt               [MODIFICADO]
    │   ├── navigation/
    │   │   ├── NavGraph.kt                   [CREADO]
    │   │   └── Screen.kt                     [CREADO]
    │   └── theme/
    │       └── Theme.kt                      [MODIFICADO]
    └── ShopPlyApp.kt                         [CREADO - Hilt]
```

---

## 🎯 Características Implementadas

### ✅ Autenticación
- [x] Registro de usuarios (Comprador/Vendedor)
- [x] Login con validación de credenciales
- [x] Hash de contraseñas (simulado - usar bcrypt en producción)
- [x] Validación de emails únicos
- [x] Manejo de errores amigable

### ✅ Gestión de Sesión
- [x] Persistencia de sesión con DataStore
- [x] Auto-login en reinicio de app
- [x] Logout con limpieza completa
- [x] Verificación de sesión activa

### ✅ UI/UX
- [x] Pantalla de bienvenida (AuthScreen)
- [x] Formulario de login con visibilidad de contraseña
- [x] Formulario de registro con selector de tipo de cuenta
- [x] Pantalla principal (Home) personalizada por rol
- [x] Indicadores de carga (CircularProgressIndicator)
- [x] Mensajes de error/éxito

### ✅ Arquitectura
- [x] Clean Architecture (Domain, Data, UI)
- [x] MVVM Pattern
- [x] Inyección de dependencias con Hilt
- [x] Repository Pattern
- [x] StateFlow para estados reactivos
- [x] Navegación con Jetpack Compose Navigation

---

## 🚀 Próximos Pasos

1. **Catálogo de Productos**
   - Entidad Product con relaciones
   - CRUD de productos para vendedores
   - Galería de imágenes

2. **Carrito de Compras**
   - Entidad Cart
   - Persistencia local
   - Sincronización con backend

3. **Sistema de Pagos**
   - Integración con pasarelas peruanas
   - Historial de órdenes

4. **Backend API**
   - Migrar de Room local a API REST
   - Sincronización en tiempo real

---

**Estadísticas del proyecto**:
- 📝 15+ archivos creados
- 🔧 3 dependencias críticas agregadas
- 🎨 4 pantallas funcionales
- 🏗️ Arquitectura MVVM completa
- ⚡ 100% Kotlin + Jetpack Compose

