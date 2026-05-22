package com.uade.costaapp.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.uade.costaapp.presentation.home.BrandOrange
import com.uade.costaapp.presentation.home.DarkBlue
import com.uade.costaapp.presentation.home.LightBlueAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val property by viewModel.property.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Toggle Favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito", tint = BrandOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { DetailBottomBar() }
    ) { innerPadding ->
        if (property == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandOrange)
            }
        } else {
            val prop = property!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(prop.imageUrl),
                    contentDescription = prop.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${prop.currency} ${prop.price.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = prop.title, fontSize = 20.sp, color = DarkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${prop.zone}, Pinamar", fontSize = 16.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AttributeItem(icon = Icons.Default.Home, value = "${prop.rooms} Amb")
                        AttributeItem(icon = Icons.Default.Face, value = "${prop.bathrooms} Baños")
                        AttributeItem(icon = Icons.Default.CheckCircle, value = "${prop.surface.toInt()} m²")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(text = "Descripción", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = prop.description, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
fun AttributeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(
        modifier = Modifier
            .background(LightBlueAccent, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(value, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DetailBottomBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandOrange)
        ) {
            Icon(Icons.Default.Call, contentDescription = null, tint = BrandOrange)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Llamar", color = BrandOrange, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mensaje", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
