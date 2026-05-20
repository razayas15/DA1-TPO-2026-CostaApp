\# Requisitos Académicos y Evaluación (TPO - UADE 2026)



Este proyecto será evaluado bajo estrictos estándares de la industria. Cada línea de código debe contemplar lo siguiente:



\## 1. Stack Tecnológico Obligatorio

\- \*\*Lenguaje:\*\* Kotlin.

\- \*\*UI:\*\* Jetpack Compose avanzado (State Hoisting, `collectAsStateWithLifecycle`).

\- \*\*Arquitectura:\*\* MVVM con Clean Architecture (Capas: data, domain, presentation, di).

\- \*\*Inyección de Dependencias:\*\* Hilt obligatorio.

\- \*\*Gestión de Red:\*\* Retrofit.

\- \*\*Imágenes:\*\* Coil / Glide.



\## 2. Estrategia de Datos (Offline-First)

\- Room es la Única Fuente de Verdad (Single Source of Truth).

\- Flujo estricto: Retrofit -> Room -> UI. La UI JAMÁS consume a Retrofit directamente.



\## 3. Funcionalidades Core

\- Splash Screen nativa de Android.

\- Autenticación con Google Firebase (Persistencia de sesión automática).

\- LazyColumn para el listado, con búsqueda y filtrado reactivo.

\- Pantalla de detalle profundo de la propiedad.



\## 4. Calidad y Testing

\- \*\*Obligatorio:\*\* Pruebas unitarias para ViewModels y Pruebas de integración para Repositories usando frameworks de Mocking (Mockk).

\- El código debe estar optimizado para no generar memory leaks ni recomposiciones innecesarias (evaluable con Android Profiler).

\- Uso mandatorio de Git bajo estrategia de Branching (una rama por feature antes del merge a main).

