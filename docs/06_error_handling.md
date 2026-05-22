# CostaApp — Error Handling Strategy
El usuario nunca debe ver una excepción técnica.

## 1. Estados de UI (UIState)
Toda pantalla debe usar una `sealed interface UiState<T>`:
- `Loading`: Renderiza Skeleton.
- `Success(data: T)`: Renderiza contenido.
- `Offline(data: T, lastUpdated: Long)`: Renderiza contenido + Banner superior.
- `Error(message: String, retryAction: () -> Unit)`: Renderiza pantalla de error con botón.

## 2. Global Exception Strategy
- API Error (Network): Capturar `IOException`, emitir `UiState.Offline` con datos de Room.
- Database Error: Loggear con Timber y emitir `UiState.Error`.