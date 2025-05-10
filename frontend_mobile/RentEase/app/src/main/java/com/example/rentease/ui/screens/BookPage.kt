package com.example.rentease.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rentease.R
import com.example.rentease.model.Room
import com.example.rentease.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun BookPage(
    roomId: String? = null,
    onBackClick: () -> Unit = {},
    onBookNowClick: () -> Unit = {}
) {
    val teal700 = Color(0xFF05445E)
    val teal600 = Color(0xFF0A6C8A)
    val teal800 = Color(0xFF032F43)
    val accentColor = Color(0xFFD4AF37)
    val backgroundColor = Color(0xFFF5F7FA)
    val surfaceColor = Color.White
    val textPrimaryColor = Color.Black
    val textSecondaryColor = Color(0xFF555555)
    val successColor = Color(0xFF4CAF50)
    val warningColor = Color(0xFFFFA000)
    val infoColor = Color(0xFF2196F3)

    var room by remember { mutableStateOf<Room?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var currentImageIndex by remember { mutableStateOf(0) }

    val imageUrl by derivedStateOf {
        room?.imagePaths?.getOrNull(currentImageIndex)
            ?: R.drawable.default_property
    }

    val totalImages by remember { derivedStateOf { room?.imagePaths?.size ?: 0 } }


    LaunchedEffect(roomId) {
        scope.launch {
            try {
                isLoading = true
                if (roomId != null) {
                    val response = RetrofitInstance.api.getRoomById(roomId.toLong())
                    if (response.isSuccessful) {
                        room = response.body()
                    }
                }
            } catch (e: Exception) {
                // Handle error if needed
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        contentColor = textPrimaryColor,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF147B93), Color(0xFF1A97B5))
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
                        text = room?.unitName ?: "Room Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 16.dp),
                color = teal600,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Monthly Rent",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            "₱${room?.rentalFee?.toInt() ?: 0}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onBookNowClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = teal700,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Calendar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Book Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = teal700)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Property Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.default_property),
                            placeholder = painterResource(id = R.drawable.default_property)
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.6f)
                                        ),
                                        startY = 100f,
                                        endY = 900f
                                    )
                                )
                        )

                        // Status badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(
                                    color = when(room?.status) {
                                        "Available" -> successColor
                                        "Booked" -> warningColor
                                        "Rented" -> infoColor
                                        else -> successColor
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = room?.status ?: "Available",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Room info at bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                room?.unitName ?: "Room Name",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${room?.city ?: "City"}, ${room?.postalCode ?: "Postal Code"}",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Image navigation dots
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(minOf(totalImages, 5)) { index ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(if (currentImageIndex == index) 10.dp else 8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (currentImageIndex == index) accentColor
                                                else Color.White.copy(alpha = 0.5f)
                                            )
                                            .clickable { currentImageIndex = index }
                                    )
                                }
                            }
                        }

                        // Image navigation arrows
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    if (totalImages > 0) {
                                        currentImageIndex = (currentImageIndex - 1 + totalImages) % totalImages
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (totalImages > 0) {
                                        currentImageIndex = (currentImageIndex + 1) % totalImages
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Room details section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = surfaceColor,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Address section
                            SectionTitle(
                                title = "Address",
                                icon = Icons.Outlined.LocationOn,
                                iconTint = teal700
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = backgroundColor
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        room?.addressLine1 ?: "Address line 1",
                                        fontSize = 16.sp,
                                        color = textPrimaryColor
                                    )
                                    if (!room?.addressLine2.isNullOrBlank()) {
                                        Text(
                                            room?.addressLine2 ?: "",
                                            fontSize = 16.sp,
                                            color = textPrimaryColor
                                        )
                                    }
                                    Text(
                                        "${room?.city ?: "City"}, ${room?.postalCode ?: "Postal Code"}",
                                        fontSize = 16.sp,
                                        color = textPrimaryColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Description section
                            SectionTitle(
                                title = "Description",
                                icon = Icons.Outlined.Description,
                                iconTint = teal700
                            )

                            Text(
                                room?.description ?: "No description available",
                                fontSize = 16.sp,
                                color = textSecondaryColor,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Gallery section
                            SectionTitle(
                                title = "Gallery",
                                icon = Icons.Outlined.PhotoLibrary,
                                iconTint = teal700
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (room?.imagePaths?.isNotEmpty() == true) {
                                    room!!.imagePaths.take(5).forEachIndexed { index, imagePath ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(
                                                    width = if (currentImageIndex == index) 2.dp else 0.dp,
                                                    color = teal700,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { currentImageIndex = index }
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(imagePath ?: R.drawable.default_property)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Gallery Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                                error = painterResource(id = R.drawable.default_property),
                                                placeholder = painterResource(id = R.drawable.default_property)
                                            )
                                        }
                                    }
                                } else {
                                    repeat(5) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(R.drawable.default_property)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Gallery Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                                error = painterResource(id = R.drawable.default_property),
                                                placeholder = painterResource(id = R.drawable.default_property)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Owner section
                            SectionTitle(
                                title = "Owner",
                                icon = Icons.Outlined.Person,
                                iconTint = teal700
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = teal700 // <- background is now teal700 (dark)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color.White), // background is now pure white
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (room?.ownerName?.firstOrNull() ?: "O").toString().uppercase(),
                                            color = teal700, // font color is teal700 for contrast
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            room?.ownerName?: "Owner's Name",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White // <- text is now white
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            "ID: ${room?.ownerId?: "Unknown"}",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.7f) // <- lighter white for subtlety
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
}

@Composable
fun SectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    iconTint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}