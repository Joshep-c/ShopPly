# data module (multi-module)

Propósito

- Módulo `data` a nivel de repo (si se usa) que contiene lógica compartida de persistencia entre módulos.

Contenido sugerido

- Repositorios y contratos compartidos.
- Adaptadores para fuentes externas (Firebase, REST) si se agregan.

Cómo usar

- Importar como dependencia desde `app` en `settings.gradle.kts` si se pretende compartir código.
