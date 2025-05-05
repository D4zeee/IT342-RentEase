package com.example.rentease.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rentease.R
import com.example.rentease.model.Room
import com.example.rentease.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun RoomsPage(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {}
) {
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)
    val scope = rememberCoroutineScope()
    var roomList by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                hasError = false
                val response = RetrofitInstance.api.getAllRooms()
                if (response.isSuccessful) {
                    roomList = response.body() ?: emptyList()
                } else {
                    hasError = true
                    errorMessage = "Error fetching rooms: ${response.code()}"
                    Log.e("RoomsPage", errorMessage)
                }
            } catch (e: Exception) {
                hasError = true
                errorMessage = "Exception: ${e.message}"
                Log.e("RoomsPage", "Exception fetching rooms: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        contentColor = Color.Black,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(tealColor, lightTeal)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Available Rooms",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = darkBlueColor)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onHomeClick) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                        }
                        IconButton(onClick = onRoomsClick) {
                            Icon(Icons.Default.KingBed, contentDescription = "Rooms", tint = Color.White)
                        }
                        IconButton(onClick = onPaymentClick) {
                            Icon(Icons.Default.CreditCard, contentDescription = "Payment", tint = Color.White)
                        }
                        IconButton(onClick = onPaymentHistoryClick) {
                            Icon(Icons.Default.Receipt, contentDescription = "Payment History", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search rooms...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = tealColor
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tealColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                // New horizontal filter design aligned with color palette
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Filter
                    Surface(
                        onClick = { selectedFilter = "All" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "All") darkBlueColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "All") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "All",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "All") Color.White else Color.DarkGray
                            )
                        }
                    }

                    // Available Filter
                    Surface(
                        onClick = { selectedFilter = "Available" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Available") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Available") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Available",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "Available") Color.White else Color.DarkGray
                            )
                        }
                    }

                    // Booked Filter
                    Surface(
                        onClick = { selectedFilter = "Booked" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Booked") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Booked") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Booked",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "Booked") Color.White else Color.DarkGray
                            )
                        }
                    }

                    // Rented Filter
                    Surface(
                        onClick = { selectedFilter = "Rented" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Rented") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Rented") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Rented",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "Rented") Color.White else Color.DarkGray
                            )
                        }
                    }
                }

                // Content based on state
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = tealColor)
                        }
                    }
                    hasError -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.Error,
                                    contentDescription = "Error",
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Something went wrong",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                isLoading = true
                                                hasError = false
                                                val response = RetrofitInstance.api.getAllRooms()
                                                if (response.isSuccessful) {
                                                    roomList = response.body() ?: emptyList()
                                                } else {
                                                    hasError = true
                                                    errorMessage = "Error fetching rooms: ${response.code()}"
                                                }
                                            } catch (e: Exception) {
                                                hasError = true
                                                errorMessage = "Exception: ${e.message}"
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                                ) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }
                    roomList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.Home,
                                    contentDescription = "No Rooms",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No rooms available",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                    else -> {
                        val filteredRooms = roomList.filter { room ->
                            val matchesSearch = if (searchText.isEmpty()) {
                                true
                            } else {
                                room.unitName.contains(searchText, ignoreCase = true)
                            }

                            val matchesFilter = selectedFilter == "All" ||
                                    room.status.equals(selectedFilter, ignoreCase = true)

                            matchesSearch && matchesFilter
                        }

                        if (filteredRooms.isEmpty()) {
                            // Show "No rooms found" message
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "No results",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No rooms found",
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            // Show matching rooms
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(filteredRooms) { room ->
                                    RoomItem(
                                        room = RoomUnit(
                                            id = room.roomId.toString(),
                                            name = room.unitName,
                                            price = "₱${room.rentalFee}",
                                            imageUrl = room.imagePaths.firstOrNull(),
                                            status = room.status
                                        ),
                                        onViewClick = {
                                            navController.navigate("book_page/${room.roomId}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomItem(
    room: RoomUnit,
    onViewClick: () -> Unit
) {
    val context = LocalContext.current
    val tealColor = Color(0xFF147B93)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room image without status overlay
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(room.imageUrl ?: R.drawable.default_property)
                        .crossfade(true)
                        .build(),
                    contentDescription = room.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = androidx.compose.ui.res.painterResource(id = R.drawable.default_property),
                    placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.default_property)
                )
            }

            // Room details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = room.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status chip with vibrant background colors
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = when(room.status?.lowercase()) {
                                "available" -> Color(0xFF4CAF50)
                                "booked" -> Color(0xFFFF9800)
                                "rented" -> Color(0xFF2196F3)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = when(room.status?.lowercase()) {
                            "available" -> Icons.Default.CheckCircle
                            "booked" -> Icons.Default.Schedule
                            "rented" -> Icons.Default.Home
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = room.status,
                        tint = Color.White, // White icon for better contrast on colored backgrounds
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = room.status ?: "Available",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White // White text for better contrast on colored backgrounds
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = room.price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tealColor
                )
            }

            // Simple arrow icon button without a box
            IconButton(
                onClick = onViewClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "View Details",
                    tint = tealColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class RoomUnit(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String? = null,
    val status: String? = "Available"
)