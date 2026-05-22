# CostaApp — Navegación y Pantallas
**Versión:** 2.0 | 9 pantallas · 16 Casos de Uso

---

## 1. MAPA DE PANTALLAS

| ID | Pantalla | CUs | Entrada | Salidas |
|----|----------|-----|---------|---------|
| S01 | `SplashScreen` | CU01 | App launch | S02 (sin sesión) · S03 (con sesión) |
| S02 | `LoginScreen` | CU02 | Sin sesión Firebase | S03 (auth OK) |
| S03 | `HomeScreen` | CU03 · CU04 · CU13 | Tab Home · Login exitoso | S06 (tap card) · S04 (tab) · S08 (FAB) |
| S04 | `MapScreen` | CU14 · CU15 · CU16 | Tab Mapa | S06 (tap InfoWindow) |
| S05 | `FavoritesScreen` | CU07 | Tab Favoritos | S06 (tap item) |
| S06 | `PropertyDetailScreen` | CU05 · CU06 · CU08 · CU11 | Tap card · InfoWindow | WhatsApp Intent · Share Intent · S04 |
| S07 | `ProfileScreen` | CU09 · CU12 | Ícono perfil top bar | S02 (logout) |
| S08 | `AISearchBottomSheet` | CU08 | FAB en S03/S04 | Aplica filtros → S03 |
| S09 | `ErrorEmptyStateView` | CU09 | Fallo o Room vacío | Reintentar → pantalla origen |

---

## 2. GRAFO DE NAVEGACIÓN — NavHost

```kotlin
@Composable
fun CostaAppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSessionActive    = { navController.navigate("home") { popUpTo("splash") { inclusive = true } } },
                onNoSession        = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
            )
        }

        composable("login") {
            LoginScreen(
                onAuthSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
            )
        }

        // Bottom Nav Host — mantiene estado de tabs
        composable("home")      { HomeScreen(navController) }
        composable("map")       { MapScreen(navController) }
        composable("favorites") { FavoritesScreen(navController) }

        composable(
            route = "detail/{propertyId}",
            arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
        ) { backStackEntry ->
            PropertyDetailScreen(
                propertyId = backStackEntry.arguments?.getString("propertyId") ?: "",
                navController = navController
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogout = { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
            )
        }
    }
}
```

---

## 3. BOTTOM NAVIGATION

```kotlin
val bottomNavItems = listOf(
    BottomNavItem(route = "home",      icon = Icons.Outlined.Home,            label = "Inicio"),
    BottomNavItem(route = "map",       icon = Icons.Outlined.Map,             label = "Mapa"),
    BottomNavItem(route = "favorites", icon = Icons.Outlined.FavoriteBorder,  label = "Favoritos")
)
```

**Reglas:**
- El Bottom Nav es visible en: Home · Map · Favorites
- Oculto en: Detail · Profile · Splash · Login
- El ícono de favoritos cambia a `Icons.Filled.Favorite` cuando hay items guardados

---

## 4. FAB DE IA

- Presente en: `HomeScreen` y `MapScreen`
- Ausente en: `FavoritesScreen` · `PropertyDetailScreen` · `ProfileScreen` · `LoginScreen` · `SplashScreen`
- Abre: `AISearchBottomSheet` como `ModalBottomSheet`
- Ícono: Gemini con gradiente (azul → púrpura → rojo)

---

## 5. DETALLE DE CADA PANTALLA

### S01 — SplashScreen (CU01)

- **Trigger:** App launch
- **Lógica:** API de Splash Android → verifica `FirebaseAuth.getInstance().currentUser`
- **Duración:** Mínima posible — no delay artificial
- **Visual:** Logo CostaApp centrado · fondo NavyDeep · tagline "Tu hogar en Pinamar"
- **Salida:** navega con `popUpTo("splash") { inclusive = true }` para limpiar el back stack

### S02 — LoginScreen (CU02)

- **Trigger:** Usuario sin sesión activa
- **Componentes:** Logo · "Bienvenido" headline · botón "Continuar con Google" · campos email/password
- **Auth flow:** Firebase Google Sign-In SDK
- **Datos recibidos:** `uid` · `displayName` · `email` · `photoUrl`
- **Post-auth:** Crear/actualizar documento en Firestore `users/{uid}` · leer `favorite_property_ids` · actualizar `isFavorite` en Room
- **Flujo alterno:** Usuario cancela → permanece en LoginScreen

### S03 — HomeScreen (CU03 · CU04 · CU13)

- **Trigger:** Login exitoso o tab Home
- **Componentes:**
  - Top App Bar: logo CostaApp + ícono perfil (→ ProfileScreen)
  - SearchBar (CU04): filtrado reactivo sobre Room con debounce 300ms
  - Filtros: chips "Todas / Venta / Alquiler" + menú "Ordenar por" (CU13)
  - Sección "Recomendamos para vos": LazyRow horizontal con PropertyCards
  - Listado principal: LazyColumn vertical con PropertyCards
  - FAB IA: bottom-right
- **Estados:** Loading (skeleton) · Success · Offline (banner) · Empty
- **Datos:** `Flow<List<Property>>` desde Room observado con `collectAsStateWithLifecycle`
- **Filtrado:** SIEMPRE sobre Room, nunca llamada de red adicional

### S04 — MapScreen (CU14 · CU15 · CU16)

- **Trigger:** Tab Mapa
- **Componentes:**
  - `GoogleMap` full-screen
  - `PriceMarker` por cada propiedad en Room (Composable custom como BitmapDescriptor)
  - Toggle "Cerca de mí" (CU16): activa Haversine sobre coordenadas Room
  - `InfoWindowOverlay` (CU15): Composable superpuesto, NO InfoWindowAdapter nativo
  - FAB IA: bottom-right
- **Color markers:** Navy=venta · Teal=alquiler · Coral=seleccionado
- **Offline:** Room tiene coordenadas → mapa carga desde caché de Google Maps
- **GPS desactivado:** Dialog solicitando permiso `ACCESS_FINE_LOCATION`

### S05 — FavoritesScreen (CU07)

- **Trigger:** Tab Favoritos
- **Query Room:** `WHERE isFavorite = 1`
- **Componentes:** LazyColumn de PropertyCards (mismo componente que Home)
- **Empty state:** ícono corazón vacío + "Sin favoritos aún" + botón "Explorar propiedades"
- **Offline:** Funciona 100% sin red — Room es la única fuente

### S06 — PropertyDetailScreen (CU05 · CU06 · CU08 · CU11)

- **Trigger:** Tap en PropertyCard (desde Home, Favorites) o tap en InfoWindow (desde Map)
- **Parámetro de navegación:** `propertyId: String`
- **Componentes:**
  - Top App Bar: botón volver + ícono compartir (Share Intent CU15)
  - `HorizontalPager` con galería de imágenes (Glide)
  - Precio headline + título
  - Chips ambientes + superficie
  - Sección zona con ícono pin
  - `AIInsightCard` (CU08): carga al entrar, oculto si falla
  - Botón "Contactar por WhatsApp" (CU11): Intent a wa.me/{contactPhone}
  - Botón "Guardar / Favorito" (CU06): actualiza Room + Firestore
- **lastVisit:** Se actualiza en Room al entrar (`System.currentTimeMillis()`)
- **Flujo alterno WhatsApp:** Si no está instalado → Intent a cliente de email

### S07 — ProfileScreen (CU09 · CU12)

- **Trigger:** Ícono perfil en Top App Bar (visible en Home y Detail)
- **Datos de display:** `photoUrl` (Glide circular) + `displayName` + `email` (desde Firebase Auth)
- **Stats:** contador Favoritos (query Room) + contador Propiedades visitadas (lastVisit != null)
- **Sección Historial (CU12):** lista `ORDER BY lastVisit DESC` desde Room
  - Empty state si ninguna propiedad fue visitada
- **Botón "Cerrar sesión":** `FirebaseAuth.getInstance().signOut()` → navegar a Login + limpiar back stack

### S08 — AISearchBottomSheet (CU08)

- **Trigger:** FAB en Home o Map
- **Tipo:** `ModalBottomSheet` (no es pantalla independiente en el grafo)
- **Input:** TextField de lenguaje natural ("Busco casa con pileta para 6 personas")
- **Procesamiento:** Gemini Pro SDK convierte el texto en parámetros de filtro
- **Output:** Aplica filtros sobre Room en HomeScreen
- **Estado AI processing:** SearchBar en modo AI (borde purple + spinner)
- **Offline:** Si no hay conexión, muestra "Búsqueda IA no disponible sin conexión"

### S09 — ErrorEmptyStateView (CU09)

- **Tipo:** Componente reutilizable, no pantalla independiente
- **Error state:** ícono alerta + mensaje + botón "Reintentar"
- **Empty state:** ícono contextual + mensaje + CTA contextual
- **Implementación:** Composable pasado como slot a cada pantalla de listado

---

## 6. FLUJO DETALLADO DEL MAPA (CU14 → CU15 → S06)

```
Usuario abre tab Mapa
        ↓
MapViewModel observa Room → Flow<List<MapMarker>>
        ↓
GoogleMap renderiza PriceMarkers (color según operationType)
        ↓
  ┌─────────────────────────────┐
  │ Usuario toca un marker      │
  └──────────┬──────────────────┘
             ↓
    viewModel.onMarkerSelected(marker)
             ↓
    InfoWindowOverlay aparece sobre el mapa
    (Composable superpuesto, NO InfoWindowAdapter)
             ↓
  ┌──────────────────────────────────────┐
  │ Opciones del usuario                 │
  ├──────────────────────────────────────┤
  │ Tap "Ver detalle" → navegar a S06    │
  │ Tap fuera       → viewModel.clear()  │
  └──────────────────────────────────────┘
```

---

## 7. INTENTS EXTERNOS

| CU | Acción | Intent |
|----|--------|--------|
| CU11 | WhatsApp | `Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${phone}?text=${message}"))` |
| CU11 fallback | Email | `Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${email}"))` |
| CU15 | Compartir | `Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, shareText) }` |

---

*Archivo: 03_navegacion.md | CostaApp v2.0 | UADE 2026*