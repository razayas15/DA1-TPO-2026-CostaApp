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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Scaffold(
        modifier = Modifier.safeDrawingPadding()
    ) { innerPadding ->
        property?.let { prop ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                // Header (Imagen + Botones Flotantes)
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    AsyncImage(
                        model = prop.imageUrl,
                        contentDescription = "Property Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Botón Volver
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DarkBlue)
                    }

                    // Botón Favorito aislado
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
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

                // Cuerpo de Información
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "USD ${prop.price}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prop.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prop.zone,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Grilla de Atributos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AttributeItem("Ambientes", prop.rooms.toString())
                        AttributeItem("Baños", prop.bathrooms.toString())
                        AttributeItem("Superficie", "${prop.surface} m²")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Descripción",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prop.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón Contactar por WhatsApp
                    Button(
                        onClick = {
                            val uri = Uri.parse("https://wa.me/5491112345678") // Número Inmobiliaria
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contactar por WhatsApp", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón Mapa
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
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBlue)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = DarkBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver ubicación en el mapa", color = DarkBlue, fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botón Compartir
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = android.content.ClipData.newPlainText("CostaApp", "Mirá esta propiedad en CostaApp: https://costaapp.com/detail/${prop.id}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = BrandOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compartir", color = BrandOrange)
                    }
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
fun AttributeItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFF5F6F8), RoundedCornerShape(8.dp))
            .padding(12.dp)
            .width(100.dp)
    ) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
