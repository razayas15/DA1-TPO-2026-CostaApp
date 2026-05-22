CostaApp — Base de Datos y API Contract
Versión: 2.0 | Room (local) + GitHub API (remoto) + Firebase Firestore (sincronizador)

## 1. API CONTRACT (GitHub Source)
La aplicación consume datos de nuestra API propia hospedada en GitHub.

* **Base URL:** `https://raw.githubusercontent.com/razayas15/costaapp-api/refs/heads/main/api/v1/properties.json`

### Data Transfer Object (DTO)
Para deserializar el JSON anidado correctamente, usaremos estas clases DTO:

```kotlin
// data/remote/dto/PropertyDto.kt
data class PropertyDto(
    val id: String,
    val title: String,
    val price: Double,
    val currency: String,
    val operationType: String,
    val thumbnail: String?,
    val location: LocationDto,
    val attributes: AttributesDto
)

data class LocationDto(
    val city: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double
)

data class AttributesDto(
    val rooms: Int,
    val bathrooms: Int,
    val surface: Double
)```

data class PictureDto(val url: String)
2. ROOM — SMART CACHE (Persistencia Local)
Room actúa como Caché Inteligente:

Almacena las propiedades visualizadas recientemente.

Persiste el estado de favoritos (isFavorite) localmente.

Es la única fuente de verdad que observa la UI (nunca consumir Retrofit desde el ViewModel).

3. ENTITY — TABLA properties
Kotlin
// data/local/entity/PropertyEntity.kt
@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val zone: String,
    val rooms: Int,
    val surface: Double,
    val bathrooms: Int,
    val latitude: Double,
    val longitude: Double,
    val operationType: String,
    val imageUrl: String,
    val contactPhone: String,
    val isFavorite: Boolean = false,
    val lastVisit: Long? = null,
    val lastUpdated: Long
)
4. DAO — QUERIES
Kotlin
@Dao
interface PropertyDao {
    @Upsert
    suspend fun upsertAll(properties: List<PropertyEntity>)

    @Query("UPDATE properties SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM properties ORDER BY lastUpdated DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: String): PropertyEntity?
}
5. MAPPER (DTO → Entity)
Crucial: Aquí es donde conectamos el DTO de GitHub con tu base local.

Kotlin
class PropertyMapper @Inject constructor() {
    fun dtoToEntity(dto: PropertyDto): PropertyEntity = PropertyEntity(
        id            = dto.id,
        title         = dto.title,
        description   = "", // No provisto por la API actual
        price         = dto.price,
        currency      = dto.currency,
        zone          = dto.location.zone,
        rooms         = dto.attributes.rooms,
        surface       = dto.attributes.surface,
        bathrooms     = dto.attributes.bathrooms,
        latitude      = dto.location.latitude,
        longitude     = dto.location.longitude,
        // Adaptamos "Venta" / "Alquiler" a "sale" / "rent" para el Design System del Mapa
        operationType = if (dto.operationType.equals("Venta", ignoreCase = true)) "sale" else "rent",
        imageUrl      = dto.thumbnail ?: "",
        contactPhone  = "", // No provisto por la API actual
        lastUpdated   = System.currentTimeMillis()
    )
}
6. FIREBASE FIRESTORE — SINCRONIZADOR
Se mantiene igual que la versión anterior, actuando solo sobre favorite_property_ids.

7. DOMAIN MODEL
Clase pura Property (sin anotaciones de Room) para usar en la capa de UI.

Archivo: 04_base_de_datos.md | CostaApp v2.0 | UADE 2026