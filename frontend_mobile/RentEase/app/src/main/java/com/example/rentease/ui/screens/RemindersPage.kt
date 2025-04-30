package com.example.rentease.ui.screens

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
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.ripple.rememberRipple
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
import kotlinx.coroutines.launch

@Composable
fun RemindersPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {}
) {
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)

    // State variables
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(false) }

    // Tab selection state
    var selectedTab by remember { mutableStateOf("Reminders") }

    // Get appropriate data based on selected tab
    val itemsList = remember(selectedTab) {
        when (selectedTab) {
            "Reminders" -> getSampleReminders()
            "Notifications" -> getSampleApprovals()
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
                // Search bar
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            if (selectedTab == "Reminders") "Search reminders..."
                            else "Search notifications..."
                        )
                    },
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

                // Filter chips - different for each tab
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

                    if (selectedTab == "Reminders") {
                        // Due Filter
                        Surface(
                            onClick = { selectedFilter = "Due" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "Due") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "Due") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Due",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "Due") Color.White else Color.DarkGray
                                )
                            }
                        }

                        // Overdue Filter
                        Surface(
                            onClick = { selectedFilter = "Overdue" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "Overdue") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "Overdue") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Overdue",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "Overdue") Color.White else Color.DarkGray
                                )
                            }
                        }
                    } else {
                        // Approve Filter
                        Surface(
                            onClick = { selectedFilter = "Approve" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "Approve") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "Approve") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Approve",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "Approve") Color.White else Color.DarkGray
                                )
                            }
                        }

                        // Denied Filter
                        Surface(
                            onClick = { selectedFilter = "Denied" },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedFilter == "Denied") tealColor else Color.White,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedFilter == "Denied") Color.Transparent else Color.LightGray
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Denied",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedFilter == "Denied") Color.White else Color.DarkGray
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
                    itemsList.isEmpty() -> {
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
                                    text = if (selectedTab == "Reminders")
                                        "No reminders available"
                                    else
                                        "No notifications available",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                    else -> {
                        val filteredItems = itemsList.filter { item ->
                            val matchesSearch = if (searchText.isEmpty()) {
                                true
                            } else {
                                item.title.contains(searchText, ignoreCase = true) ||
                                        item.description.contains(searchText, ignoreCase = true)
                            }

                            val matchesFilter = selectedFilter == "All" ||
                                    item.status.equals(selectedFilter, ignoreCase = true)

                            matchesSearch && matchesFilter
                        }

                        if (filteredItems.isEmpty()) {
                            // Show "No items found" message
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
                                        text = if (selectedTab == "Reminders")
                                            "No reminders found"
                                        else
                                            "No notifications found",
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            // Show matching items
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
            // Icon with background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = when(item.status) {
                            "Due" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            "Overdue" -> Color(0xFFF44336).copy(alpha = 0.2f)
                            "Approve" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
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
                    tint = when(item.status) {
                        "Due" -> Color(0xFFFF9800)
                        "Overdue" -> Color(0xFFF44336)
                        "Approve" -> Color(0xFF4CAF50)
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
                    text = item.title,
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
                            color = when(item.status) {
                                "Due" -> Color(0xFFFF9800)
                                "Overdue" -> Color(0xFFF44336)
                                "Approve" -> Color(0xFF4CAF50)
                                "Denied" -> Color(0xFFF44336)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = when(item.status) {
                            "Due" -> Icons.Default.Schedule
                            "Overdue" -> Icons.Default.Warning
                            "Approve" -> Icons.Default.CheckCircle
                            "Denied" -> Icons.Default.Cancel
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = item.status,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = item.status,
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

            // Modern View Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = if (isPressed) 1.dp else 3.dp,
                        shape = CircleShape,
                        spotColor = tealColor
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isPressed) {
                                listOf(tealColor.copy(alpha = 0.8f), tealColor)
                            } else {
                                listOf(tealColor, Color(0xFF0A6277))
                            }
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = false, color = Color.White),
                        onClick = onViewClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = "View Details",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
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
    val status: String
)

// Sample data function for reminders
fun getSampleReminders(): List<NotificationItem> {
    return listOf(
        NotificationItem(
            id = "1",
            title = "Rent Payment Due",
            description = "Monthly rent payment for Unit 101 is due today",
            date = "May 1, 2023",
            type = "Payment",
            status = "Due"
        ),
        NotificationItem(
            id = "2",
            title = "Maintenance Check",
            description = "Scheduled maintenance check for air conditioning unit",
            date = "May 5, 2023",
            type = "Maintenance",
            status = "Due"
        ),
        NotificationItem(
            id = "3",
            title = "Property Inspection",
            description = "Annual property inspection by management",
            date = "May 10, 2023",
            type = "Inspection",
            status = "Due"
        ),
        NotificationItem(
            id = "4",
            title = "Utility Bill Payment",
            description = "Water and electricity bill payment",
            date = "April 25, 2023",
            type = "Payment",
            status = "Overdue"
        ),
        NotificationItem(
            id = "5",
            title = "Lease Renewal",
            description = "Your lease is up for renewal in 30 days",
            date = "June 1, 2023",
            type = "Contract",
            status = "Due"
        )
    )
}

// Sample data function for approval notifications
fun getSampleApprovals(): List<NotificationItem> {
    return listOf(
        NotificationItem(
            id = "A1",
            title = "Maintenance Request Approved",
            description = "Your request for plumbing repair has been approved",
            date = "May 2, 2023",
            type = "Approval",
            status = "Approve"
        ),
        NotificationItem(
            id = "A2",
            title = "Late Payment Fee Waiver",
            description = "Your request for late payment fee waiver has been approved",
            date = "April 28, 2023",
            type = "Approval",
            status = "Approve"
        ),
        NotificationItem(
            id = "A3",
            title = "Pet Request Denied",
            description = "Your request to keep a pet in your unit has been denied",
            date = "May 5, 2023",
            type = "Approval",
            status = "Denied"
        ),
        NotificationItem(
            id = "A4",
            title = "Rent Discount Request",
            description = "Your request for rent discount has been denied",
            date = "April 20, 2023",
            type = "Approval",
            status = "Denied"
        ),
        NotificationItem(
            id = "A5",
            title = "Lease Extension Approved",
            description = "Your request to extend your lease has been approved",
            date = "May 10, 2023",
            type = "Approval",
            status = "Approve"
        )
    )
}