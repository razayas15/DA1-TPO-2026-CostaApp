# CostaApp — API Contract (GitHub)
**Versión:** 2.0 | API Estática en GitHub Raw

## 1. CONFIGURACIÓN DEL SERVIDOR
La aplicación consume un archivo JSON estático alojado en un repositorio de GitHub.

* **Base URL:** `https://raw.githubusercontent.com/razayas15/costaapp-api/refs/heads/main/`
* **Endpoint:** `GET api/v1/properties.json`
* **Headers requeridos:** `Accept: application/json`

## 2. ESTRUCTURA DEL JSON (Response)
El JSON devuelto es un **ARRAY DIRECTO**, no está envuelto en ningún objeto. Contiene objetos anidados para `location` y `attributes`.

**Estructura exacta:**
```json
[
  {
    "id": "PROP-001",
    "title": "Departamento con Pileto en Pinamar",
    "price": 110075,
    "currency": "USD",
    "operationType": "Venta",
    "thumbnail": "[https://picsum.photos/seed/1/800/600](https://picsum.photos/seed/1/800/600)",
    "location": {
      "city": "Partido de Pinamar",
      "zone": "Pinamar",
      "latitude": -34.114967,
      "longitude": -56.866525
    },
    "attributes": {
      "rooms": 2,
      "bathrooms": 2,
      "surface": 79
    }
  }
]

## 3. IMPLEMENTACIÓN RETROFIT ESPERADA
Dado que es un JSON Array directo, el servicio de Retrofit debe devolver una Lista del DTO:

```kotlin
interface CostaAppApiService {
    @GET("api/v1/properties.json")
    suspend fun getProperties(): Response<List<PropertyDto>>
}