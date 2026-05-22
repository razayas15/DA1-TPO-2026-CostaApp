Este es el **Sprint Planning**. Le dice a la IA exactamente qué hacer AHORA y qué dejar para después.

```markdown
# CostaApp — Roadmap Segunda Entrega
**Fecha límite:** 27/05/2026

## OBJETIVO DEL SPRINT
Completar exclusivamente las funcionalidades requeridas para la segunda entrega. Cualquier intento de implementar Google Maps o Gemini AI debe ser pospuesto para el sprint final.

## CHECKLIST DE TAREAS ESTRATÉGICAS (En orden de ejecución)

### Fase 1: Capa de Datos (Data Layer)
- [ ] Implementar la entidad `PropertyEntity` de Room y el `PropertyDao`.
- [ ] Configurar la base de datos `CostaAppDatabase` e Inyección de Dependencias (Hilt).
- [ ] Crear el modelo de red `PropertyDto` y el servicio de Retrofit (Mock API).
- [ ] Implementar el Repository uniendo Retrofit y Room (Offline-first strategy).

### Fase 2: Autenticación (Auth)
- [ ] Integrar Firebase BOM y dependencias de Google Auth en `build.gradle`.
- [ ] Implementar flujo de Login con Google.
- [ ] Persistir estado de sesión para saltar el Login si ya hay usuario.

### Fase 3: Interfaz de Usuario (UI Layer)
- [ ] Implementar `SplashScreen` nativa (API de Android).
- [ ] Crear `HomeScreen` con listado reactivo observando Room (`LazyColumn`).
- [ ] Implementar buscador y filtrado reactivo.
- [ ] Crear `PropertyDetailScreen` recuperando datos locales e imágenes con Glide.

**Regla de ejecución:** El Agente debe sugerir abordar una de estas fases por vez y solicitar confirmación antes de iniciar el código.