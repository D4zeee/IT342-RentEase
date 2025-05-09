package com.example.rentease.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentease.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun RemindersPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    initialTab: String = "Reminders"
) {
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)

    // State variables
    var notificationsList by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableStateOf(initialTab) }
    var remindersList by remember { mutableStateOf<List<ReminderItem>>(emptyList()) }

    // Fetch notifications when Notifications tab is selected
    LaunchedEffect(selectedTab) {
        if (selectedTab == "Notifications") {
            isLoading = true
            val sharedPreferences = context.getSharedPreferences("RentEasePrefs", android.content.Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("renterToken", null)
            if (!token.isNullOrEmpty()) {
                val authHeader = "Bearer $token"
                try {
                    val userResponse = RetrofitInstance.api.getCurrentUser(authHeader)
                    val userMap = userResponse.body()
                    val renterId = (userMap?.get("renterId") as? Double)?.toLong()
                    if (renterId != null) {
                        val response = RetrofitInstance.api.getRentedUnitNotificationsByRenter(authHeader, renterId)
                        if (response.isSuccessful) {
                            android.util.Log.d("RemindersPage", "Fetched notifications: ${response.body()}")
                            notificationsList = response.body()?.map { dto ->
                                NotificationItem(
                                    id = dto.room_id.toString(),
                                    title = "Room #${dto.room_id} - ${dto.unitname}",
                                    description = dto.note,
                                    date = formatDate(dto.startDate.toString()),
                                    type = "Approval",
                                    approval_status = dto.approval_status,
                                    unitname = dto.unitname,
                                    note = dto.note
                                )
                            } ?: emptyList()
                        } else {
                            android.util.Log.e("RemindersPage", "Failed to fetch notifications: ${response.code()} ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("RemindersPage", "Error fetching notifications", e)
                }
            }
            isLoading = false
        }
    }

    // Fetch reminders when Reminders tab is selected
    LaunchedEffect(selectedTab) {
        if (selectedTab == "Reminders") {
            isLoading = true
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
                            remindersList = response.body()?.map { dto: com.example.rentease.model.PaymentReminderDto ->
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
                } catch (e: Exception) {
                    // Handle error
                }
            }
            isLoading = false
        }
    }

    // --- IMPORTANT: Reminders and Notifications are strictly separated ---
    // remindersList: only owner-created reminders (from /payment_reminders/renter/{renterId})
    // notificationsList: only booking approval notifications (from /rented_units/renter/{renterId}/notifications)
    // Never mix these two lists in the UI or logic!

    val itemsList = remember(selectedTab, remindersList, notificationsList, selectedFilter) {
        when (selectedTab) {
            "Reminders" -> {
                // Only show reminders, never notifications
                remindersList
                    .distinctBy { it.room_id to it.due_date }
            }
            "Notifications" -> {
                // Only show the latest notification for each room_id where note starts with 'Booking pending approval for'
                notificationsList
                    .filter { it.note?.startsWith("Booking pending approval for", ignoreCase = true) == true }
                    .groupBy { it.id }
                    .map { (_, group) ->
                        group.maxByOrNull { it.date } ?: group.first()
                    }
                    .filter {
                        selectedFilter == "All" || it.approval_status.equals(selectedFilter, ignoreCase = true)
                    }
            }
            else -> emptyList()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        contentColor = Color.Black,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(tealColor, lightTeal)
                        )
                    )
                    .statusBarsPadding()
            ) {
                // Top bar with back button and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                        "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reminders Tab
                    Surface(
                        onClick = { selectedTab = "Reminders" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedTab == "Reminders") Color.White else Color.White.copy(alpha = 0.2f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Reminders",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedTab == "Reminders") tealColor else Color.White
                            )
                        }
                    }

                    // Notifications Tab
                    Surface(
                        onClick = { selectedTab = "Notifications" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = if (selectedTab == "Notifications") Color.White else Color.White.copy(alpha = 0.2f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Notifications",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedTab == "Notifications") tealColor else Color.White
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
                // Filter chips - different for each tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedTab == "Notifications") {
                        // Only show filter chips for Notifications tab
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
                        // Pending Filter
                        Surface(
                            onClick = { selectedFilter = "pending" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "pending") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "pending") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Pending",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "pending") Color.White else Color.DarkGray
                                )
                            }
                        }
                        // Approved Filter
                        Surface(
                            onClick = { selectedFilter = "approved" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "approved") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "approved") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Approved",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "approved") Color.White else Color.DarkGray
                                )
                            }
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
                    selectedTab == "Reminders" -> {
                        if (remindersList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = "No Items",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No reminders available",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(remindersList) { reminder ->
                                    ReminderCard(reminder)
                                }
                            }
                        }
                    }
                    selectedTab == "Notifications" -> {
                        val filteredItems = notificationsList
                            .filter { it.note?.startsWith("Booking pending approval for", ignoreCase = true) == true }
                            .groupBy { it.id }
                            .map { (_, group) -> group.maxByOrNull { it.date } ?: group.first() }
                            .filter {
                                selectedFilter == "All" || it.approval_status.equals(selectedFilter, ignoreCase = true)
                            }

                        if (filteredItems.isEmpty()) {
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
                                        text = "No notifications found",
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(filteredItems) { item ->
                                    NotificationItem(
                                        item = item,
                                        onViewClick = { /* Handle view click */ }
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
fun NotificationItem(
    item: NotificationItem,
    onViewClick: () -> Unit
) {
    val context = LocalContext.current
    val tealColor = Color(0xFF147B93)
    val orangeColor = Color(0xFFFF9800)  // Orange color for pending status
    val greenColor = Color(0xFF4CAF50)   // Green color for approved status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onViewClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = when(item.approval_status) {
                            "Due" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            "Overdue" -> Color(0xFFF44336).copy(alpha = 0.2f)
                            "pending" -> orangeColor.copy(alpha = 0.2f)  // Changed to orange for pending
                            "approved" -> greenColor.copy(alpha = 0.2f)  // Changed to green for approved
                            "Approve" -> greenColor.copy(alpha = 0.2f)
                            "Denied" -> Color(0xFFF44336).copy(alpha = 0.2f)
                            else -> tealColor.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.type) {
                        "Payment" -> Icons.Default.CreditCard
                        "Maintenance" -> Icons.Default.Build
                        "Inspection" -> Icons.Default.Search
                        "Contract" -> Icons.Default.Description
                        "Approval" -> Icons.Default.VerifiedUser
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = item.type,
                    tint = when(item.approval_status) {
                        "Due" -> Color(0xFFFF9800)
                        "Overdue" -> Color(0xFFF44336)
                        "pending" -> orangeColor  // Changed to orange for pending
                        "approved" -> greenColor  // Changed to green for approved
                        "Approve" -> greenColor
                        "Denied" -> Color(0xFFF44336)
                        else -> tealColor
                    },
                    modifier = Modifier.size(48.dp)
                )
            }

            // Item details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = item.unitname ?: "N/A",
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
                            color = when(item.approval_status) {
                                "Due" -> Color(0xFFFF9800)
                                "Overdue" -> Color(0xFFF44336)
                                "pending" -> orangeColor  // Changed to orange for pending
                                "approved" -> greenColor  // Changed to green for approved
                                "Approve" -> greenColor
                                "Denied" -> Color(0xFFF44336)
                                else -> greenColor
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = when(item.approval_status) {
                            "Due" -> Icons.Default.Schedule
                            "Overdue" -> Icons.Default.Warning
                            "pending" -> Icons.Default.Schedule  // Icon for pending
                            "approved" -> Icons.Default.CheckCircle  // Icon for approved
                            "Approve" -> Icons.Default.CheckCircle
                            "Denied" -> Icons.Default.Cancel
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = item.approval_status,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Capitalize the first letter of the status text
                    Text(
                        text = when(item.approval_status) {
                            "pending" -> "Pending"  // Capitalize for display
                            "approved" -> "Approved"  // Capitalize for display
                            else -> item.approval_status
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tealColor
                )
            }
        }
    }
}

// Unified data model for both reminders and notifications
data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val type: String,
    val approval_status: String,
    val unitname: String? = null,
    val note: String? = null
)

data class ReminderItem(
    val room_id: Long,
    val unit_name: String,
    val due_date: String,
    val note: String
)

// Helper function to format date (yyyy-MM-dd to MMM d, yyyy)
fun formatDate(dateStr: String?): String {
    return try {
        if (dateStr.isNullOrEmpty()) return ""
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        if (date != null) outputFormat.format(date) else dateStr
    } catch (e: Exception) {
        dateStr ?: ""
    }
}

@Composable
fun ReminderCard(reminder: ReminderItem) {
    val tealColor = Color(0xFF147B93)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.unit_name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Due: ${reminder.due_date}",
                fontSize = 16.sp,
                color = tealColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reminder.note,
                fontSize = 15.sp,
                color = Color.DarkGray
            )
        }
    }
}