package com.uade.costaapp.presentation.detail

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.uade.costaapp.presentation.home.BrandOrange
import com.uade.costaapp.presentation.home.DarkBlue

@Composable
fun PropertyDetailScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val property by viewModel.property.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    // El Box principal ahora es nuestro lienzo para superposiciones
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo por si la imagen tarda
    ) {
        property?.let { prop ->
            // 1. IMAGEN DE CABECERA (Mitad Superior)
            AsyncImage(
                model = prop.imageUrl,
                contentDescription = "Property Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp) // Generosa para ser pisada por la tarjeta
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            // 2. BOTONES FLOTANTES SUPERIORES (Volver y Favorito)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .safeDrawingPadding() // Evita Notch y Status Bar
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Volver
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DarkBlue)
                }

                // Favorito
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
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

            // 3. TARJETA PRINCIPAL BLANCA (Mitad Inferior + Superposición)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f) // Ocupa el 65% inferior de la pantalla
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Fila Principal: Precio y Botón Compartir
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (prop.operationType == "sale") "VENTA" else "ALQUILER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "USD ${prop.price}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandOrange
                            )
                        }

                        // Compartir (Estilo Red Social)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = android.content.ClipData.newPlainText("CostaApp", "Mirá esta propiedad en CostaApp: https://costaapp.com/detail/${prop.id}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF5F6F8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = DarkBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Compartir", fontSize = 12.sp, color = DarkBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título y Zona
                    Text(
                        text = prop.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = prop.zone, fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Grilla de Atributos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AttributeCard(Icons.Default.Home, "Ambientes", prop.rooms.toString())
                        AttributeCard(Icons.Default.Build, "Baños", prop.bathrooms.toString())
                        AttributeCard(Icons.Default.Place, "Superficie", "${prop.surface} m²")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Descripción
                    Text(text = "Descripción", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = prop.description,
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Botones de Acción Finales
                    Button(
                        onClick = {
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            try {
                                val uri = Uri.parse("geo:0,0?q=${prop.zone}")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlue)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = DarkBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver ubicación en el mapa", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp)) // Espacio final
                }
            }
        } ?: run {
            // Spinner de Carga Inicial
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandOrange)
            }
        }
    }
}

@Composable
fun AttributeCard(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFF5F6F8), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp)
            .width(100.dp)
    ) {
        Icon(icon, contentDescription = label, tint = DarkBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
