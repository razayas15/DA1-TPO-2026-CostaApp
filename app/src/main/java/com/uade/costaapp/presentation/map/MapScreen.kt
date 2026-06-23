package com.uade.costaapp.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.uade.costaapp.presentation.home.BrandOrange
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    targetLat: Double? = null,
    targetLng: Double? = null,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val properties by homeViewModel.properties.collectAsStateWithLifecycle()
    var selectedProperty by remember { mutableStateOf<PropertyEntity?>(null) }
    
    // Check API Key programmatically
    val applicationInfo = try {
        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    } catch (e: Exception) { null }
    val apiKey = applicationInfo?.metaData?.getString("com.google.android.geo.API_KEY")
    val hasApiKey = !apiKey.isNullOrBlank() && apiKey != "YOUR_API_KEY"

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Propiedades", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            com.uade.costaapp.presentation.home.HomeBottomNavigation(
                currentRoute = "map",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMap = {}, 
                onNavigateToFavorites = onNavigateToFavorites
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!hasApiKey || !hasLocationPermission) {
                // Fallback Elegante
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "No disponible",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Servicio de mapas no disponible en este dispositivo (Entorno de desarrollo).",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            } else {
                // Mostrar Mapa
                val hasTarget = targetLat != null && targetLng != null
                val centerLocation = if (hasTarget) LatLng(targetLat!!, targetLng!!) else LatLng(-37.114967, -56.866525) // Pinamar
                
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(centerLocation, if (hasTarget) 15f else 12.5f)
                }
                
                var isMapLoaded by remember { mutableStateOf(false) }
                var showMap by remember { mutableStateOf(false) }
                
                // Delay renderizado inicial para que la animación de navegación termine suavemente
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(400)
                    showMap = true
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (showMap) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapLoaded = { isMapLoaded = true },
                            onMapClick = { selectedProperty = null }
                        ) {
                            properties.forEach { property ->
                        val position = LatLng(property.latitude, property.longitude)
                        Marker(
                            state = MarkerState(position = position),
                            title = property.title,
                            onClick = {
                                selectedProperty = property
                                true // Consume event to prevent native info window
                            }
                        )
                            }
                        }
                    }

                androidx.compose.animation.AnimatedVisibility(
                    visible = !isMapLoaded,
                    enter = androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F9FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandOrange)
                    }
                }
            }

                // Tarjeta Flotante
                if (selectedProperty != null) {
                    val prop = selectedProperty!!
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Evita clics transparentes */ }
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(prop.imageUrl),
                                        contentDescription = prop.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { selectedProperty = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(32.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${prop.currency} ${prop.price.toInt()}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0A1D37)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = prop.title,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0A1D37),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${prop.rooms} ambientes • ${prop.surface.toInt()} m²",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { onNavigateToDetail(prop.id) },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05E36)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Ver detalle", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
