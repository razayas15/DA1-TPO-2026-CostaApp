\# Design System - CostaApp (Premium Coastal v2.0)



El diseño debe respetar estrictamente estas directrices en Jetpack Compose (Material Design 3).



\## 1. Paleta de Colores

\- \*\*Primarios:\*\* Navy Deep `#0D2B45` (TopBar, textos primarios), Navy Light `#1A4568` (Hover), Ocean `#1B6CA8` (Links, iconos activos), Sky `#E8F4FD` (Fondos de chips).

\- \*\*Secundarios:\*\* Sand Light `#F5EFE0` (Fondos cards), Sand Mid `#E8D9B8` (Dividers), Sand Dark `#C4A96A` (Texto sobre arena).

\- \*\*Semántica Mapa \& Operación:\*\* Navy `#0D2B45` (Venta), Teal `#0E9E8A` (Alquiler / Success), Coral `#E05C3A` (CTA / Favorito / Pin seleccionado).

\- \*\*Neutros:\*\* White `#FFFFFF` (Cards), Surface `#FAFAF8` (Fondo general de la app).



\## 2. Tipografía y Elevación

\- \*\*Fuente:\*\* Inter (Fallback: Roboto). Solo pesos 400 (Regular) y 500 (Medium). NUNCA 600 ni 700.

\- \*\*Elevación:\*\* Tonal Surface MD3 (sin sombras reales, solo tintes Navy). 0dp (Fondo), 1dp (Cards), 36dp (Bottom Nav).



\## 3. Componentes Core en Compose

\- \*\*PropertyCard:\*\* Radius 16dp. Imagen alto 136dp (Glide). Badge de operación superpuesto (Pill Navy).

\- \*\*MapMarker:\*\* Custom Composable (Globo de precio).

\- \*\*InfoWindow (Mapa - CU15):\*\* DEBE ser un Composable superpuesto (LaunchedEffect + MarkerState). PROHIBIDO usar `InfoWindowAdapter` heredado de Views.

\- \*\*SearchBar:\*\* Radius 12dp. Borde Ocean al enfocar. Borde Purple en modo IA (CU08).

\- \*\*Estados de UI (Obligatorios en listas):\*\* - `Loading`: Skeleton shimmer (NUNCA spinner global).

&#x20; - `Success`: Datos completos.

&#x20; - `Offline`: Banner Coral Surface alertando modo offline.

&#x20; - `Empty`: Ilustración + CTA. NUNCA pantalla blanca.

