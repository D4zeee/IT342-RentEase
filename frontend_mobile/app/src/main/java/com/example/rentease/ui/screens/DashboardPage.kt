package com.example.rentease.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.rentease.model.PaymentReminderDto
import com.example.rentease.model.Room
import com.example.rentease.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.example.rentease.model.RentedUnit
import com.example.rentease.ui.screens.ReminderItem

@Composable
fun DashboardPage(
    onPropertyClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    onViewAllRoomsClick: () -> Unit = {}
) {
    val tealColor = Color(0xFF147B93)
    val darkBlueColor = Color(0xFF0A3F52)
    val scope = rememberCoroutineScope()
    var roomList by remember { mutableStateOf<List<Room>>(emptyList()) }
    var myRentedRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var rentedUnits by remember { mutableStateOf<List<RentedUnit>>(emptyList()) }
    var remindersList by remember { mutableStateOf<List<ReminderItem>>(emptyList()) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        scope.launch {
            val sharedPreferences = context.getSharedPreferences("RentEasePrefs", android.content.Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("renterToken", null)
            if (token.isNullOrEmpty()) {
                Log.e("Dashboard", "No token found")
                isLoading = false
                return@launch
            }

            try {
                // Fetch current user to get renterId
                val userResponse = RetrofitInstance.api.getCurrentUser("Bearer $token")
                val userMap = userResponse.body()
                val renterId = (userMap?.get("renterId") as? Double)?.toLong()
                if (renterId != null) {
                    // Fetch only the rooms rented by this renter
                    val rentedRoomsResponse = RetrofitInstance.api.getBookedOrRentedRoomsForRenter("Bearer $token", renterId)
                    val roomResponse = RetrofitInstance.api.getAllRooms("Bearer $token")

                    if (rentedRoomsResponse.isSuccessful && roomResponse.isSuccessful) {
                        myRentedRooms = rentedRoomsResponse.body() ?: emptyList()
                        roomList = roomResponse.body() ?: emptyList()
                        Log.d("Dashboard", "myRentedRooms: $myRentedRooms")
                    } else {
                        Log.e(
                            "Dashboard",
                            "Error fetching data: rentedRoomsResponse=${rentedRoomsResponse.code()} ${rentedRoomsResponse.message()}, body=${rentedRoomsResponse.errorBody()?.string()} | roomResponse=${roomResponse.code()} ${roomResponse.message()}, body=${roomResponse.errorBody()?.string()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Dashboard", "Exception fetching data: ${e.message}")
            } finally {
                isLoading = false
            }
        }

        // Fetch reminders for current renter
        val sharedPreferences = context.getSharedPreferences("RentEasePrefs", android.content.Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("renterToken", null)
        if (!token.isNullOrEmpty()) {
            val authHeader = "Bearer $token"
            try {
                val userResponse = RetrofitInstance.api.getCurrentUser(authHeader)
                val userMap = userResponse.body()
                val renterId = (userMap?.get("renterId") as? Double)?.toLong()
                if (renterId != null) {
                    val response = RetrofitInstance.api.getRemindersByRenter(authHeader, renterId)
                    if (response.isSuccessful) {
                        remindersList = response.body()?.map { dto: PaymentReminderDto ->
                            ReminderItem(
                                room_id = dto.room.roomId ?: 0L,
                                unit_name = dto.room.unitName ?: "",
                                due_date = dto.dueDate ?: "",
                                note = dto.note ?: ""
                            )
                        }?.filter { !it.note.contains("Booking pending approval", ignoreCase = true) }
                         ?.distinctBy { it.room_id to it.due_date } ?: emptyList()
                    }
                }
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        containerColor = tealColor,
        contentColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Dashboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Welcome to RentEase",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                    }
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Statistics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Available Rooms Card
                    StatisticsCard(
                        title = "Available Rooms",
                        value = roomList.count { it.status.equals("available", ignoreCase = true) }.toString(),
                        icon = Icons.Default.Search,
                        backgroundColor = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )

                    // My Rented Rooms Card
                    StatisticsCard(
                        title = "My Rented Units",
                        value = myRentedRooms.size.toString(),
                        icon = Icons.Default.Home,
                        backgroundColor = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Activity Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    TextButton(onClick = onNotificationClick) {
                        Text(
                            "View All",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Show reminders as activity, or empty state
                if (remindersList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Reminders Found",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        remindersList.take(2).forEach { reminder ->
                            ActivityCard(
                                title = reminder.unit_name,
                                description = reminder.note,
                                time = reminder.due_date,
                                icon = Icons.Default.Notifications,
                                iconTint = Color(0xFF147B93)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Featured Rooms Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Featured Rooms",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    TextButton(onClick = onViewAllRoomsClick) {
                        Text(
                            "View All",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Featured Rooms Horizontal Scroll - exclude "Booked" and "Rented"
                val featuredRooms = roomList
                    .filter { it.status.equals("Available", ignoreCase = true) }
                    .take(5)

                if (featuredRooms.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Available Rooms",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredRooms) { room ->
                            FeaturedRoomCard(
                                room = room,
                                onClick = { onPropertyClick(room.roomId.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    title: String,
    description: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FeaturedRoomCard(
    room: Room,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val imageUrl = room.imagePaths.firstOrNull()

                AsyncImage(
                    model = imageUrl,
                    contentDescription = room.unitName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.default_property),
                    error = painterResource(id = R.drawable.default_property)
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = room.unitName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "₱${String.format("%.2f", room.rentalFee)}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


