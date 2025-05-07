package com.example.rentease.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.rentease.network.RetrofitInstance
import com.example.rentease.model.RentedUnit
import com.example.rentease.model.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    onPayMongoClick: () -> Unit = {}
) {
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)
    val accentColor = Color(0xFFF9A826)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State for dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf<RentedUnit?>(null) }
    var rentedUnits by remember { mutableStateOf<List<RentedUnit>>(emptyList()) }
    var roomList by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetch rented units and all rooms when the page loads
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                hasError = false
                val sharedPreferences = context.getSharedPreferences("RentEasePrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("renterToken", null)
                if (token.isNullOrEmpty()) {
                    hasError = true
                    errorMessage = "Authentication token not found"
                    return@launch
                }
                // Get current user to get renterId
                val userResponse = RetrofitInstance.api.getCurrentUser("Bearer $token")
                if (!userResponse.isSuccessful) {
                    hasError = true
                    errorMessage = "Failed to get user information"
                    return@launch
                }
                val userMap = userResponse.body()
                val renterId = (userMap?.get("renterId") as? Double)?.toLong()
                Log.d("PaymentsPage", "Current renterId: $renterId")
                if (renterId == null) {
                    hasError = true
                    errorMessage = "Invalid user ID"
                    return@launch
                }
                // Fetch rented units for the current renter
                val rentedUnitsResponse = RetrofitInstance.api.getRentedUnitsByRenter("Bearer $token", renterId)
                // Fetch all rooms to get unitName and rentalFee
                val roomsResponse = RetrofitInstance.api.getAllRooms("Bearer $token")
                if (rentedUnitsResponse.isSuccessful && roomsResponse.isSuccessful) {
                    rentedUnits = rentedUnitsResponse.body() ?: emptyList()
                    roomList = roomsResponse.body() ?: emptyList()
                    Log.d("PaymentsPage", "Fetched rentedUnits: $rentedUnits")
                    Log.d("PaymentsPage", "Fetched roomList: $roomList")
                } else {
                    hasError = true
                    errorMessage = "Failed to fetch rented units or rooms"
                }
            } catch (e: Exception) {
                hasError = true
                errorMessage = "Error: ${e.message}"
                Log.e("PaymentsPage", "Error fetching rented units", e)
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
                        "Payments",
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = tealColor)
            }
        } else if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                // Retry loading rented units
                                isLoading = true
                                hasError = false
                                // ... (copy the LaunchedEffect block here)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tealColor
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }
        } else if (rentedUnits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "No Units",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No rented units found",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add spacer above the Payment Process card
                Spacer(modifier = Modifier.height(8.dp))

                // Payment Process Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Payment Process",
                            style = MaterialTheme.typography.titleMedium,
                            color = darkBlueColor,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentProcessStep(
                            number = 1,
                            title = "Select Rented Unit",
                            description = "Choose the unit you want to pay for from the dropdown below",
                            icon = Icons.Default.Home,
                            color = tealColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentProcessStep(
                            number = 2,
                            title = "Click Pay with PayMongo",
                            description = "Click the payment button to proceed to the payment gateway",
                            icon = Icons.Default.CreditCard,
                            color = tealColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentProcessStep(
                            number = 3,
                            title = "Complete Payment",
                            description = "Follow the instructions on PayMongo to complete your payment",
                            icon = Icons.Default.CheckCircle,
                            color = tealColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentProcessStep(
                            number = 4,
                            title = "Receive Confirmation",
                            description = "You'll receive a confirmation once your payment is processed",
                            icon = Icons.Default.MarkEmailRead,
                            color = tealColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Payment method info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F8FA))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = tealColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PayMongo supports credit/debit cards, GCash, GrabPay, and bank transfers.",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Unit Selection Dropdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Select Your Rented Unit",
                            style = MaterialTheme.typography.titleMedium,
                            color = darkBlueColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box {
                            val uniqueRentedUnits = rentedUnits.distinctBy { it.roomId }
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val selectedRoom = roomList.find { it.roomId == selectedUnit?.roomId }
                                Text(
                                    text = selectedRoom?.unitName ?: "Select a unit",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                uniqueRentedUnits.forEach { unit ->
                                    val room = roomList.find { it.roomId == unit.roomId }
                                    DropdownMenuItem(
                                        text = { Text(room?.unitName ?: "N/A") },
                                        onClick = {
                                            selectedUnit = unit
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Unit Information
                if (selectedUnit != null) {
                    val selectedRoom = roomList.find { it.roomId == selectedUnit!!.roomId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Unit Information",
                                style = MaterialTheme.typography.titleMedium,
                                color = darkBlueColor
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            // Unit Details
                            UnitInfoRow("Unit Name", selectedRoom?.unitName)
                            UnitInfoRow("Due Date", selectedUnit?.startDate)
                            UnitInfoRow("Price", "₱${String.format("%.2f", selectedRoom?.rentalFee ?: 0.0)}")
                            Spacer(modifier = Modifier.height(16.dp))
                            // PayMongo Button
                            Button(
                                onClick = onPayMongoClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = tealColor
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = "Pay",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pay with PayMongo")
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
private fun UnitInfoRow(label: String?, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label ?: "N/A",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentProcessStep(
    number: Int,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step number circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Step content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}