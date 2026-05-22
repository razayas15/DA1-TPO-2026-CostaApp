# CostaApp — Design System
**Versión:** 2.0 | **Estética:** Premium Coastal — Pinamar

---

## ROL DEL AGENTE

Actuás como **Senior UI/UX Designer + Android Engineer** especializado en Jetpack Compose y Material Design 3. Toda propuesta visual debe seguir este sistema sin excepción. Nunca usar sombras reales, gradientes decorativos, ni pesos tipográficos 600/700.

---

## 1. IDENTIDAD VISUAL

**Concepto:** Premium Coastal — limpia, profesional, con calidez de costa atlántica argentina.
**Plataforma:** Android nativo — Jetpack Compose + Material Design 3
**Viewport de referencia:** 375dp ancho (base para todos los diseños)

---

## 2. PALETA DE COLORES

### 2.1 Primarios — Navy & Ocean

| Token | Hex | Uso |
|-------|-----|-----|
| `NavyDeep` | `#0D2B45` | Color primario · Top App Bar · texto principal · botones primarios |
| `NavyLight` | `#1A4568` | Hover · pressed states |
| `Ocean` | `#1B6CA8` | Links · acciones secundarias · íconos activos · borde focus |
| `Sky` | `#E8F4FD` | Chip backgrounds · info surfaces |

### 2.2 Secundarios — Sand (identidad Pinamar)

| Token | Hex | Uso |
|-------|-----|-----|
| `SandLight` | `#F5EFE0` | Card backgrounds · warm surfaces · fondo AI card |
| `SandMid` | `#E8D9B8` | Dividers cálidos · bordes AI card |
| `SandDark` | `#C4A96A` | Texto sobre fondos arena |

### 2.3 Acento Teal — Alquiler & Success

| Token | Hex | Uso |
|-------|-----|-----|
| `Teal` | `#0E9E8A` | Pins de alquiler en mapa · éxito · badge "ALQUILER" |
| `TealLight` | `#E0F5F2` | Superficie teal · fondo badge alquiler |

### 2.4 CTA Coral — Acciones primarias

| Token | Hex | Uso |
|-------|-----|-----|
| `Coral` | `#E05C3A` | Botones CTA · favorito activo · marker seleccionado |
| `CoralDark` | `#B8421F` | Pressed state CTA |
| `CoralSurface` | `#FBF0EC` | Fondo íconos coral · badge offline/error |

### 2.5 Neutros

| Token | Hex | Uso |
|-------|-----|-----|
| `White` | `#FFFFFF` | Surface cards · modales · sheets |
| `Surface` | `#FAFAF8` | Page background |
| `TextPrimary` | `#0D2B45` | Títulos · cuerpo principal |
| `TextSecondary` | `#4A6478` | Subtítulos · labels · metadata |
| `TextHint` | `#8FA5B5` | Placeholders · disabled · timestamps |
| `Border` | `rgba(13,43,69,0.12)` | Borde estándar cards |
| `BorderFocus` | `#1B6CA8` | Borde inputs activos |

### 2.6 Semántica de color en el mapa (obligatoria)

```
operationType == "sale"  → MapMarker NavyDeep  #0D2B45
operationType == "rent"  → MapMarker Teal       #0E9E8A
Marker seleccionado      → MapMarker Coral      #E05C3A
```

### 2.7 Implementación Compose — MaterialTheme

```kotlin
val CostaAppColorScheme = lightColorScheme(
    primary          = Color(0xFF0D2B45),
    onPrimary        = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1A4568),
    secondary        = Color(0xFF0E9E8A),
    onSecondary      = Color(0xFFFFFFFF),
    tertiary         = Color(0xFFE05C3A),
    onTertiary       = Color(0xFFFFFFFF),
    background       = Color(0xFFFAFAF8),
    surface          = Color(0xFFFFFFFF),
    onBackground     = Color(0xFF0D2B45),
    onSurface        = Color(0xFF0D2B45),
    surfaceVariant   = Color(0xFFF5EFE0),
    outline          = Color(0x1F0D2B45),
)
```

---

## 3. TIPOGRAFÍA

**Fuente:** Inter · Fallback: Roboto (nativa Android)
**Pesos permitidos:** Solo `FontWeight.Normal (400)` y `FontWeight.Medium (500)`. Nunca 600 ni 700.

| Rol Compose | sp | Peso | Color default | Uso |
|------------|-----|------|---------------|-----|
| `displayLarge` | 28sp | 500 | NavyDeep | Precio en PropertyDetailScreen |
| `headlineMedium` | 22sp | 500 | NavyDeep | Precio en PropertyCard |
| `titleMedium` | 16sp | 500 | NavyDeep | Nombre propiedad · section headers |
| `bodyMedium` | 14sp | 400 | TextSecondary | Descripciones · metadata · análisis IA |
| `labelMedium` | 12sp | 500 | TextHint | Chips · badges · etiquetas uppercase |
| `labelSmall` | 11sp | 400 | TextHint | Timestamps · zona · Room cache info |

```kotlin
val CostaAppTypography = Typography(
    displayLarge   = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    titleMedium    = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyMedium     = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    labelMedium    = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = InterFamily, fontWeight = FontWeight.Normal,  fontSize = 11.sp),
)
```

---

## 4. ELEVACIÓN — MATERIAL DESIGN 3 (TONAL SURFACE)

Sin sombras reales. La jerarquía se expresa como tint de NavyDeep sobre la superficie.

| Nivel | tonalElevation | Color resultante | Uso |
|-------|----------------|-----------------|-----|
| 0 | 0dp | `#FAFAF8` | Page background |
| 1 | 1dp | `#FFFFFF` | Cards de propiedades |
| 2 | 3dp | `#F5F8FC` | FAB · SearchBar |
| 3 | 6dp | `#EFF4FA` | Bottom Navigation · Dialogs |
| 4 | 8dp | `#E8F4FD` | Bottom Sheets · Modales |

```kotlin
Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 1.dp,  // tonalElevation únicamente
        pressedElevation = 2.dp
    )
)
// shadowElevation = 0.dp SIEMPRE
```

---

## 5. TOKENS DE ESPACIADO Y FORMA

### Espaciado

| Token | dp | Uso |
|-------|-----|-----|
| `SpaceXS` | 4dp | Gap ícono-label en chips |
| `SpaceSM` | 8dp | Padding interno chips · gap entre chips |
| `SpaceMD` | 16dp | Padding horizontal cards · gap entre secciones |
| `SpaceLG` | 24dp | Padding vertical sections |
| `SpaceXL` | 32dp | Separación entre bloques |
| `Space2XL` | 48dp | Padding pantallas · secciones mayores |

### Forma

| Token | dp | Shape Compose | Uso |
|-------|-----|--------------|-----|
| `RadiusSM` | 8dp | `RoundedCornerShape(8.dp)` | Chips · badges |
| `RadiusMD` | 12dp | `RoundedCornerShape(12.dp)` | Botones · text fields |
| `RadiusLG` | 16dp | `RoundedCornerShape(16.dp)` | PropertyCard |
| `RadiusXL` | 24dp | `RoundedCornerShape(24.dp)` | Bottom Sheets |
| `RadiusFull` | 999dp | `CircleShape` | Pills · MapMarkers · avatares |

### Bordes

```kotlin
val CardBorder    = BorderStroke(0.5.dp, Color(0x1F0D2B45)) // borde estándar
val FocusBorder   = BorderStroke(1.dp,   Color(0xFF1B6CA8)) // input activo
val FeaturedBorder = BorderStroke(2.dp,  Color(0xFF1B6CA8)) // única excepción 2dp
```

---

## 6. COMPONENTES CORE

### 6.1 PropertyCard

```
┌──────────────────────────────┐
│ [Imagen 136dp · Glide]       │  ← contentScale = Crop · placeholder SandLight
│ [VENTA badge top-left]  [♥]  │  ← badge NavyDeep pill · ícono favorito Coral/Hint
├──────────────────────────────┤
│ USD 250.000                  │  ← headlineMedium 22sp 500 NavyDeep
│ Casa moderna cerca de Av...  │  ← bodyMedium 14sp TextSecondary, 1 línea ellipsis
│ [🏠 4 amb.] [⬛ 180 m²]     │  ← SuggestionChip · bg Sky · text Ocean
│ 📍 Ostende · Pinamar         │  ← labelSmall 11sp TextHint
└──────────────────────────────┘
```

**Estados:**
- `Loading` → skeleton shimmer sobre SandLight. **Nunca** CircularProgressIndicator global
- `Success` → datos completos desde Room
- `Offline` → datos Room + badge "Sin conexión · datos locales" + timestamp `lastUpdated`
- `Error imagen` → Glide fallback placeholder, nunca rompe la card

**Implementación clave:**
```kotlin
@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp), // para grid horizontal en Home
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, Color(0x1F0D2B45)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) { /* contenido */ }
}
```

### 6.2 MapMarker (Globo de precio)

```
    ┌─────────────┐
    │  USD 250k   │  ← pill redondeada · texto blanco 13sp 500
    └──────┬──────┘
           ▼          ← triángulo apuntando abajo (Canvas custom)
           •          ← dot 4dp
```

```kotlin
@Composable
fun PriceMarkerContent(
    price: String,
    operationType: String,
    isSelected: Boolean
) {
    val bgColor = when {
        isSelected              -> Color(0xFFE05C3A) // Coral
        operationType == "rent" -> Color(0xFF0E9E8A) // Teal
        else                    -> Color(0xFF0D2B45) // NavyDeep
    }
    // Renderizar como BitmapDescriptor para entregarlo a GoogleMap Marker
}
```

### 6.3 InfoWindow — CU15

```
┌─────────────────────┐
│ [Thumbnail 80dp]    │  ← Glide · radius 8dp
│ USD 250.000         │  ← titleMedium 14sp 500
│ Casa moderna cerca  │  ← labelSmall 11sp TextSecondary
│ Ver detalle →       │  ← labelMedium 12sp Ocean
└─────────────────────┘
            ▼           ← triángulo bottom-left
```

**IMPLEMENTACIÓN OBLIGATORIA:**
```kotlin
// ✅ Composable superpuesto — CORRECTO
selectedProperty?.let { property ->
    InfoWindowOverlay(
        property = property,
        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        onDismiss = { viewModel.clearSelection() },
        onNavigateToDetail = { navController.navigate("detail/${property.id}") }
    )
}

// ❌ PROHIBIDO — setInfoWindowAdapter() devuelve android.view.View
// Incompatible con Jetpack Compose
```

### 6.4 SearchBar

| Estado | Border | Background | Derecha |
|--------|--------|------------|---------|
| Idle | `rgba(13,43,69,0.18)` 1dp | White | CTA "Búsqueda IA" |
| Active CU04 | Ocean 1dp + ring 3dp | White | Ícono X limpiar |
| AI Mode CU08 | Purple 1dp + ring 3dp | `#FAF8FF` | Spinner purple |

```kotlin
// height = 48.dp · shape = RoundedCornerShape(12.dp)
// El filtrado opera SIEMPRE sobre Room, nunca hace llamada de red
// debounce 300ms antes de filtrar
```

### 6.5 AI Insight Card — CU08

```
┌──────────────────────────────────┐
│ ✦ Análisis IA        [Gemini 🔮] │  ← Header · border SandMid
├──────────────────────────────────┤
│ ✅ Zona céntrica a 2 cuadras...  │  ← Ventaja 1 · bodyMedium
│ ✅ Superficie amplia para...     │  ← Ventaja 2 · bodyMedium
│ ⚠️ Precio elevado respecto...   │  ← Desventaja · bodyMedium Coral
└──────────────────────────────────┘
```

**Estados:**
- `Loading` → skeleton 3 líneas shimmer sobre SandLight
- `Success` → texto del SDK
- `Error/Offline` → componente completamente oculto. Los datos crudos permanecen visibles

---

## 7. ESTADOS GLOBALES DE UI

Aplicables a toda pantalla con listado (obligatorio por la cátedra):

```kotlin
// Loading — NUNCA CircularProgressIndicator de pantalla completa
PropertyCardSkeleton() // shimmer sobre SandLight, repite N veces

// Offline — Room siempre tiene algo que mostrar
OfflineBanner(lastUpdated = state.lastUpdated) // banner superior Coral Surface

// Empty
EmptyStateView(
    icon = Icons.Outlined.SearchOff,
    title = "Sin propiedades guardadas",
    subtitle = "Explorá propiedades y marcalas como favoritas",
    ctaText = "Explorar",
    onCtaClick = { navController.navigate("home") }
)

// Error
ErrorStateView(
    message = state.message,
    onRetry = state.retryAction
)
```

**Regla:** La pantalla **nunca queda en blanco**. Sin datos y sin red → estado Empty con CTA.

---

## 8. BOTTOM NAVIGATION

```kotlin
val bottomNavItems = listOf(
    BottomNavItem("home",      Icons.Outlined.Home,            "Inicio"),
    BottomNavItem("map",       Icons.Outlined.Map,             "Mapa"),
    BottomNavItem("favorites", Icons.Outlined.FavoriteBorder,  "Favoritos")
)
// FAB de IA presente solo en Home y Map
// Ausente en: Favorites · Detail · Profile
```

---

## 9. REFERENCIA VISUAL — PROTOTIPOS EN ALTA (Base44)

Los prototipos de alta fidelidad muestran:

- **HomeScreen:** Top App Bar NavyDeep con logo CostaApp + ícono perfil · SearchBar · filtros "Todas / Relevancia" · sección "Recomendamos para vos" con grid horizontal de cards · listado vertical · Bottom Nav 3 tabs
- **PropertyDetailScreen:** Header con botón volver · galería imágenes · precio + título · chips ambientes/m² · análisis IA · botones WhatsApp + Favorito
- **ProfileScreen:** Avatar circular + nombre + email · stats Favoritos/Propiedades · links Mis Favoritos / Ver Mapa · botón Cerrar sesión
- **FavoritesScreen:** Top App Bar · sección "Mis Favoritos" · listado vertical · empty state con botón "Explorar propiedades"
- **LoginScreen:** Logo centrado · "Bienvenido" · botón "Continuar con Google" · campos email/password (Firebase)

---

*Archivo: 02_design_system.md | CostaApp v2.0 | UADE 2026*