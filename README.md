# CostaApp 🏖️🏠

**CostaApp** es una solución inmobiliaria móvil nativa para la ciudad de Pinamar, desarrollada como Trabajo Práctico Obligatorio (TPO) para la Universidad Argentina de la Empresa (UADE). 

La aplicación permite a los usuarios explorar propiedades disponibles, previsualizar su ubicación en un mapa interactivo, marcar propiedades como favoritas (sincronizadas en la nube) y obtener análisis inteligentes sobre las características de cada inmueble utilizando Inteligencia Artificial.

---

## 🚀 Stack Tecnológico

El proyecto está construido siguiendo los más altos estándares y buenas prácticas de desarrollo Android moderno:

- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material Design 3)
- **Arquitectura:** MVVM (Model-View-ViewModel) bajo los principios de Clean Architecture.
- **Inyección de Dependencias:** Dagger / Hilt
- **Persistencia Local (Caché Inteligente):** Room Database
- **Consumo de API:** Retrofit + OkHttp
- **Navegación:** Jetpack Navigation Compose
- **Imágenes Asíncronas:** Glide
- **Autenticación y Nube:** Firebase Auth & Firebase Firestore
- **Geolocalización:** Google Maps SDK for Android (Maps Compose)
- **Inteligencia Artificial:** Google Gemini Pro SDK (`generativeai`)

---

## 💾 Estrategia de Datos (Offline-First)

CostaApp implementa una estrategia **Offline-First**. 
**Room Database** actúa como la Única Fuente de Verdad (*Single Source of Truth - SSOT*). 
1. La UI siempre observa (`StateFlow`) los datos provenientes de la base de datos local (Room).
2. En segundo plano, Retrofit consulta la API externa y actualiza la base de datos local.
3. Cualquier cambio del usuario (ej. agregar a favoritos) se impacta instantáneamente en Room para que la UI sea reactiva, y luego se sincroniza de forma remota y silenciosa con Firebase Firestore. Si el dispositivo no tiene red, el fallo es manejado silenciosamente manteniendo la experiencia intacta.

---

## 🛠️ Instrucciones de Compilación y Ejecución

Para correr este proyecto en tu entorno local, sigue estos pasos:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/costaapp.git
   cd costaapp
   ```

2. **Configurar el entorno seguro (`local.properties`):**
   Debes crear o modificar el archivo `local.properties` en el directorio raíz del proyecto y agregar tus propias claves (estas claves nunca se suben al repositorio):
   ```properties
   MAPS_API_KEY=tu_clave_de_google_maps_aqui
   GEMINI_API_KEY=tu_clave_de_gemini_aqui
   ```

3. **Configurar Firebase (`google-services.json`):**
   Descarga tu archivo `google-services.json` desde la consola de Firebase y colócalo en el directorio `app/`. 
   *Nota: Debes registrar la firma SHA-1 de tu entorno local (`debug.keystore`) en Firebase para que los servicios de Google funcionen correctamente.*

4. **Sincronizar y Compilar:**
   Abre el proyecto en **Android Studio (Iguana o superior)**, espera a que Gradle sincronice las dependencias y presiona **Run (Shift + F10)** en un emulador o dispositivo físico (API 24+).

---

## 🧪 Calidad y Pruebas (Testing)

El proyecto incluye un entorno de testing validado y preparado:
- **Pruebas Unitarias:** Implementadas con **JUnit4** y **MockK**.
- **Flujos Reactivos:** Validación de las máquinas de estado del ViewModel (`StateFlow`) mediante la librería **Turbine**.
- **Performance:** La aplicación fue auditada con **Android Profiler**, logrando transiciones fluidas a 60fps (resolviendo *Skipped frames*) gracias al uso intensivo de `Dispatchers.IO` y carga diferida (`AnimatedVisibility`) en componentes pesados como el mapa.

---
