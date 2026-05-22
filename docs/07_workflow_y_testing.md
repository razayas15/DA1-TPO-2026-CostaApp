# CostaApp — Workflow y Testing
**Versión:** 2.0 | UADE 2026

## 1. FLUJO DE GIT (Estrategia Branching)
La rúbrica exige evaluación del proceso. Prohibido trabajar en `main`.
* **Ramas de Feature:** `feature/auth`, `feature/room-cache`, `feature/ui-home`.
* **Commits:** Deben ser atómicos y descriptivos usando Conventional Commits:
  * `feat:` Nueva funcionalidad.
  * `fix:` Corrección de bugs.
  * `chore:` Configuración, dependencias.
  * `test:` Agregado de pruebas unitarias.

## 2. ESTRATEGIA DE TESTING (OBLIGATORIA)
El proyecto será desaprobado si no contiene pruebas unitarias de integración.
* **Frameworks:** JUnit4 + Mockk + Coroutines Test.
* **ViewModels:** Se deben testear los flujos de emisión de estado (`UiState`).
* **Repositories:** Se debe mockear el DAO (Room) y el DataSource (Retrofit) para asegurar que el repositorio devuelve datos de la caché local cuando la red falla.

**Ejemplo de Test Requerido:**
```kotlin
@Test
fun `When API fails, repository should return local cached data`() = runTest {
    // 1. Mockear API para que lance Exception
    // 2. Mockear DAO para que devuelva lista local
    // 3. Llamar al repository.getProperties()
    // 4. Hacer assert de que el resultado es Success con la lista local
}