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
import androidx.compose.material.icons.outlined.Receipt
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistoryPage(
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
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentsList by remember { mutableStateOf(getSamplePayments()) }

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
                        "Payment History",
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
                    placeholder = { Text("Search payments...") },
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

                // Filter chips - Updated to Paid, Pending, Not Paid
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

                    // Paid Filter
                    Surface(
                        onClick = { selectedFilter = "Paid" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Paid") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Paid") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Paid",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "Paid") Color.White else Color.DarkGray
                            )
                        }
                    }

                    // Pending Filter
                    Surface(
                        onClick = { selectedFilter = "Pending" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Pending") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Pending") Color.Transparent else Color.LightGray
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
                                color = if (selectedFilter == "Pending") Color.White else Color.DarkGray
                            )
                        }
                    }

                    // Not Paid Filter
                    Surface(
                        onClick = { selectedFilter = "Not Paid" },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedFilter == "Not Paid") tealColor else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedFilter == "Not Paid") Color.Transparent else Color.LightGray
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Not Paid",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFilter == "Not Paid") Color.White else Color.DarkGray
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
                    paymentsList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.Receipt,
                                    contentDescription = "No Payments",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No payment history available",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                    else -> {
                        val filteredPayments = paymentsList.filter { payment ->
                            val matchesSearch = if (searchText.isEmpty()) {
                                true
                            } else {
                                payment.description.contains(searchText, ignoreCase = true) ||
                                        payment.paymentId.contains(searchText, ignoreCase = true) ||
                                        payment.amount.toString().contains(searchText, ignoreCase = true)
                            }

                            val matchesFilter = when (selectedFilter) {
                                "All" -> true
                                else -> payment.status == selectedFilter
                            }

                            matchesSearch && matchesFilter
                        }

                        if (filteredPayments.isEmpty()) {
                            // Show "No payments found" message
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
                                        text = "No payments found",
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            // Show matching payments
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(filteredPayments) { payment ->
                                    PaymentItem(
                                        payment = payment,
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
fun PaymentItem(
    payment: Payment,
    onViewClick: () -> Unit
) {
    val tealColor = Color(0xFF147B93)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onViewClick), // Make the entire card clickable
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment icon with background - Updated for new status types
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = when(payment.status) {
                            "Paid" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            "Pending" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            "Not Paid" -> Color(0xFFF44336).copy(alpha = 0.2f)
                            else -> tealColor.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (payment.type) {
                        "Rent" -> Icons.Default.Home
                        "Utilities" -> Icons.Default.Bolt
                        "Deposit" -> Icons.Default.AccountBalance
                        else -> Icons.Default.Receipt
                    },
                    contentDescription = payment.type,
                    tint = when(payment.status) {
                        "Paid" -> Color(0xFF4CAF50)
                        "Pending" -> Color(0xFFFF9800)
                        "Not Paid" -> Color(0xFFF44336)
                        else -> tealColor
                    },
                    modifier = Modifier.size(48.dp)
                )
            }

            // Payment details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = payment.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status chip with vibrant background colors - Updated for new status types
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = when(payment.status) {
                                "Paid" -> Color(0xFF4CAF50)
                                "Pending" -> Color(0xFFFF9800)
                                "Not Paid" -> Color(0xFFF44336)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = when(payment.status) {
                            "Paid" -> Icons.Default.CheckCircle
                            "Pending" -> Icons.Default.Schedule
                            "Not Paid" -> Icons.Default.Close
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = payment.status,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = payment.status,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "₱${payment.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tealColor
                )
            }

            // The "View" button has been removed
        }
    }
}

// Sample data model
data class Payment(
    val paymentId: String,
    val description: String,
    val amount: Double,
    val date: String,
    val type: String,
    val status: String // Updated to use "Paid", "Pending", "Not Paid"
)

// Sample data function - Updated with new status types
fun getSamplePayments(): List<Payment> {
    return listOf(
        Payment(
            paymentId = "PAY-001",
            description = "May 2023 Rent Payment",
            amount = 15000.00,
            date = "May 1, 2023",
            type = "Rent",
            status = "Paid"
        ),
        Payment(
            paymentId = "PAY-002",
            description = "April Utilities",
            amount = 2500.00,
            date = "April 15, 2023",
            type = "Utilities",
            status = "Paid"
        ),
        Payment(
            paymentId = "PAY-003",
            description = "Security Deposit",
            amount = 20000.00,
            date = "January 1, 2023",
            type = "Deposit",
            status = "Paid"
        ),
        Payment(
            paymentId = "PAY-004",
            description = "June 2023 Rent Payment",
            amount = 15000.00,
            date = "June 1, 2023",
            type = "Rent",
            status = "Pending"
        ),
        Payment(
            paymentId = "PAY-005",
            description = "May Utilities",
            amount = 2800.00,
            date = "May 15, 2023",
            type = "Utilities",
            status = "Not Paid"
        )
    )
}