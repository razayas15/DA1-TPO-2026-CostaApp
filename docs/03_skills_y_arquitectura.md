\# Arquitectura de Datos y Casos de Uso - CostaApp



\## 1. Persistencia Local (Room - Smart Cache)

Room almacena el catálogo para garantizar funcionamiento Offline-first.

\- \*\*Tabla `properties`:\*\* `id` (PK, String), `title`, `description`, `price` (Double), `currency`, `zone`, `rooms` (Int), `surface` (Double), `latitude`, `longitude`, `operationType` (String), `imageUrl`, `isFavorite` (Boolean, Default false), `lastVisit` (Long), `lastUpdated` (Long).



\## 2. Persistencia Remota (Firestore - Sync Liviano)

Firestore NO almacena propiedades. Solo sincroniza favoritos.

\- \*\*Colección `users`:\*\* Documento por UID (Auth). Campos: `email`, `displayName`, `photoUrl`, `favorite\_property\_ids` (Array de Strings).



\## 3. Inteligencia Artificial (Gemini Pro SDK)

\- \*\*Caso de Uso CU08:\*\* Solo se ejecuta en la pantalla de detalle de propiedad. 

\- \*\*Regla:\*\* NO es un chatbot. Genera un output determinista leyendo de Room: 2 Ventajas y 1 Desventaja basado en `rooms`, `surface`, `zone`, `price`, `operationType`.



## 4. Endpoints (Mock API Propia)
- **URL Completa de Producción:** `https://raw.githubusercontent.com/razayas15/costaapp-api/refs/heads/main/api/v1/properties.json`
- **Base URL para Retrofit:** `https://raw.githubusercontent.com/`
- **Endpoint GET:** `razayas15/costaapp-api/refs/heads/main/api/v1/properties.json`
- **Formato Response:** JSON array de objetos property.
