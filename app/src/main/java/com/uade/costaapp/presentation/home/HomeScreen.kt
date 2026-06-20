package com.uade.costaapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import android.widget.Toast
import coil.compose.rememberAsyncImagePainter
import com.uade.costaapp.data.local.entity.PropertyEntity
import com.uade.costaapp.presentation.auth.AuthViewModel

val DarkBlue = Color(0xFF0A1D37)
val BrandOrange = Color(0xFFE05E36)
val LightGrayBackground = Color(0xFFF5F6F8)
val LightBlueAccent = Color(0xFFE3F2FD)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val properties by homeViewModel.properties.collectAsStateWithLifecycle()
    val recommendedProperties by homeViewModel.recommendedProperties.collectAsStateWithLifecycle()
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedOperation by homeViewModel.selectedOperation.collectAsStateWithLifecycle()
    val selectedSort by homeViewModel.selectedSort.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = { 
            HomeHeader(
                onProfileClick = { 
                    try {
                        onNavigateToProfile() 
                    } catch (e: IllegalArgumentException) {
                        // Evita crash temporal si la ruta no existe aún
                    }
                },
                onLogoutClick = {
                    authViewModel.signOut()
                    onNavigateToLogin()
                }
            ) 
        },
        bottomBar = { HomeBottomNavigation() },
        containerColor = LightGrayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeSearchBar(
                query = searchQuery,
                onQueryChange = { homeViewModel.onSearchQueryChanged(it) }
            )
            HomeFilters(
                selectedOperation = selectedOperation,
                selectedSort = selectedSort,
                onOperationChanged = { homeViewModel.onOperationChanged(it) },
                onSortChanged = { homeViewModel.onSortChanged(it) },
                propertiesCount = properties.size
            )
            
            LazyColumn(
                contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Recomendamos para vos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recommendedProperties, key = { "rec_${it.id}" }) { property ->
                            PropertyCard(
                                property = property,
                                modifier = Modifier.width(280.dp).animateItemPlacement(),
                                onClick = { 
                                    homeViewModel.markAsViewed(property.id)
                                    onNavigateToDetail(property.id) 
                                },
                                onFavoriteClick = { homeViewModel.toggleFavorite(property) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Explorar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        modifier = Modifier.padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 8.dp)
                    )
                }

                items(properties, key = { "list_${it.id}" }) { property ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        PropertyCard(
                            property = property,
                            modifier = Modifier.animateItemPlacement(),
                            onClick = { 
                                homeViewModel.markAsViewed(property.id)
                                onNavigateToDetail(property.id) 
                            },
                            onFavoriteClick = { homeViewModel.toggleFavorite(property) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(BrandOrange, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("CostaApp", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Box {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = DarkBlue,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape)
                    .padding(6.dp)
                    .clickable { menuExpanded = true }
            )
            
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Mi perfil", color = DarkBlue) },
                    onClick = {
                        menuExpanded = false
                        onProfileClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Cerrar sesión", color = Color.Red) },
                    onClick = {
                        menuExpanded = false
                        onLogoutClick()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.White, RoundedCornerShape(12.dp)),
            placeholder = { Text("Buscar en Pinamar...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            singleLine = true
        )
    }
}

@Composable
fun HomeFilters(
    selectedOperation: String,
    selectedSort: String,
    onOperationChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    propertiesCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterDropdown(
            options = listOf("Todas", "Venta", "Alquiler"),
            selectedOption = selectedOperation,
            onOptionSelected = onOperationChanged
        )
        Spacer(modifier = Modifier.width(8.dp))
        FilterDropdown(
            options = listOf("Relevancia", "Menor precio", "Mayor precio", "Más ambientes", "Mayor superficie"),
            selectedOption = selectedSort,
            onOptionSelected = onSortChanged
        )
        Spacer(modifier = Modifier.weight(1f))
        Text("$propertiesCount propiedades", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun FilterDropdown(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(selectedOption, fontSize = 14.sp, color = DarkBlue)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(option, color = DarkBlue)
                            if (option == selectedOption) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Check, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: PropertyEntity, 
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onFavoriteClick: (PropertyEntity) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(property.id) }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(property.imageUrl),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                val isSale = property.operationType.equals("sale", true) || property.operationType.equals("venta", true)
                val tagColor = if (isSale) DarkBlue else Color(0xFF00BFA5)
                val tagText = if (isSale) "VENTA" else "ALQUILER"
                Text(
                    text = tagText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(tagColor, RoundedCornerShape(percent = 50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFavoriteClick(property) 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (property.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (property.isFavorite) BrandOrange else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${property.currency} ${property.price.toInt()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = property.title,
                    fontSize = 16.sp,
                    color = DarkBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InfoTag(icon = Icons.Default.Home, text = "${property.rooms} amb.")
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoTag(icon = Icons.Default.CheckCircle, text = "${property.surface.toInt()} m²")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${property.zone}, Pinamar", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun InfoTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(LightBlueAccent, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HomeBottomNavigation() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    NavigationBar(
        containerColor = Color.White,
        contentColor = DarkBlue
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = true,
            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandOrange, unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Mapa") },
            label = { Text("Mapa") },
            selected = false,
            onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, "Funcionalidad en desarrollo - Disponible en el próximo Sprint", Toast.LENGTH_SHORT).show() 
            },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = false,
            onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                // En el diseño final se podría navegar a Favoritos aquí, pero mantenemos la lógica actual en Profile
            },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}
