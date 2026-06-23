package com.uade.costaapp.presentation.detail

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import coil.compose.AsyncImage
import com.uade.costaapp.presentation.home.BrandOrange
import com.uade.costaapp.presentation.home.DarkBlue
import com.uade.costaapp.presentation.home.InfoTag

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMap: (Double, Double, String) -> Unit = { _, _, _ -> },
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val property by viewModel.property.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    // Fondo general gris muy claro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        property?.let { prop ->
            val images = if (!prop.images.isNullOrEmpty()) prop.images else listOf(prop.imageUrl)
            val pagerState = rememberPagerState(pageCount = { images.size })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. CARRUSEL DE IMÁGENES Y BOTONES FLOTANTES
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp) // Altura contundente
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = "Imagen de la propiedad $page",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Puntos indicadores del Pager
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(images.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }

                    // Capa superior con los botones flotantes (Z-Index alto)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding() // Protege del Notch (Inmersivo)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Volver
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                .clickable { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateBack() 
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DarkBlue)
                        }

                        // Grupo Derecho: Compartir y Favorito
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("CostaApp", "Mirá esta propiedad en CostaApp: https://costaapp.com/detail/${prop.id}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = DarkBlue, modifier = Modifier.size(20.dp))
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.toggleFavorite()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (prop.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (prop.isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                    }

                    // BADGE VENTA / ALQUILER
                    val isSale = prop.operationType.equals("sale", true) || prop.operationType.equals("venta", true)
                    val tagColor = if (isSale) DarkBlue else Color(0xFF00BFA5)
                    val tagText = if (isSale) "VENTA" else "ALQUILER"
                    Text(
                        text = tagText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .background(tagColor, RoundedCornerShape(percent = 50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // 2. LAYOUT DE CONTENIDO (Debajo de la imagen)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .safeDrawingPadding() // Aplicamos padding acá abajo para asegurar
                ) {
                    // Precio y Tipo
                    Text(
                        text = "USD ${prop.price}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandOrange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título y Ubicación con Pin
                    Text(
                        text = prop.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = prop.zone, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!prop.description.isNullOrBlank()) {
                        Text(
                            text = prop.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Grilla de Atributos (Cápsulas Horizontales)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoTag(icon = Icons.Default.Home, text = "${prop.rooms} amb.")
                        InfoTag(icon = Icons.Default.CheckCircle, text = "${prop.surface.toInt()} m²")
                        InfoTag(icon = Icons.Default.Build, text = "${prop.bathrooms} baños")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Spacer(modifier = Modifier.height(32.dp))

                    AiAnalysisCard(
                        aiState = aiState,
                        onGenerateClick = { viewModel.analyzeProperty() }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // 3. BOTONES DE ACCIÓN (Fijos al final del scroll)
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val uri = Uri.parse("https://wa.me/5491112345678")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contactar por WhatsApp", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToMap(prop.latitude, prop.longitude, prop.title)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlue)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = DarkBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver ubicación en el mapa", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp)) // Padding final
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandOrange)
            }
        }
    }
}

@Composable
fun AiAnalysisCard(aiState: AiState, onGenerateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8DFCD))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "IA", tint = BrandOrange, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ANÁLISIS IA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandOrange,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            when (aiState) {
                is AiState.Idle -> {
                    Button(
                        onClick = onGenerateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Generar Análisis", color = Color.White)
                    }
                }
                is AiState.Loading, is AiState.Thinking -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandOrange, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Analizando propiedad...", color = Color.DarkGray, fontSize = 14.sp)
                    }
                }
                is AiState.Success -> {
                    Column {
                        Text(aiState.data.summary, color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        aiState.data.positives.forEach { text ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Positivo", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text, color = Color.DarkGray, fontSize = 14.sp)
                            }
                        }
                        
                        aiState.data.warnings.forEach { text ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Warning, contentDescription = "Advertencia", tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text, color = Color.DarkGray, fontSize = 14.sp)
                            }
                        }
                    }
                }
                is AiState.Error -> {
                    Text(
                        text = aiState.message,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Componente "Cápsula" para Atributos Rápidos
@Composable
fun AttributeCapsule(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFEAF0F6), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
