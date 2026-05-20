\# Workflow, Git y Testing (Requisitos Académicos)



\## 1. Git y Estrategia de Branching

\- PROHIBIDO codear directamente en la rama `main`.

\- Por cada nueva funcionalidad, debes crear una rama siguiendo el patrón: `feature/nombre-de-la-funcionalidad` (Ej: `feature/home-screen`, `feature/room-setup`).

\- Los commits deben ser descriptivos y en inglés (Convención Conventional Commits: `feat:`, `fix:`, `chore:`).



\## 2. Testing Obligatorio

\- Todo ViewModel debe tener Pruebas Unitarias.

\- Todo Repository debe tener Pruebas de Integración.

\- Framework de Mocking obligatorio: \*\*Mockk\*\*.

\- Framework de testing asíncrono: Coroutines Test (`runTest`).

\- El código debe estar diseñado para ser testeable (inyección por constructor con Hilt).



\## 3. Optimización y Profiling

\- No deben existir Memory Leaks.

\- Minimizar recomposiciones en Jetpack Compose (usar `remember`, `derivedStateOf`, y coleccionar flujos con `collectAsStateWithLifecycle`).

- Commits Atómicos: Cada funcionalidad o pantalla debe ser un commit separado (Ej: Un commit solo para el Splash Screen, otro commit para configurar Firebase, otro para el Login). NUNCA hagas un commit masivo con múltiples funcionalidades."
El agente de IA leerá esto y, cuando termine el Splash Screen, te dirá: "Terminé el Splash. ¿Hago el commit ahora antes de seguir?