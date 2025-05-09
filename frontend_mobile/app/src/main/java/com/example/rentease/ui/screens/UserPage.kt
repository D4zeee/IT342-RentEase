package com.example.rentease.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.rentease.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun UserPage(
    onDeleteAccountClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    // Add new navigation callbacks for notifications and reminders
    onNotificationsClick: (String) -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var isEditing by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Color scheme
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)
    val deleteRedColor = Color(0xFFD32F2F)
    val surfaceColor = Color(0xFFF5F7FA)
    val dividerColor = Color(0xFFE0E0E0)

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("RentEasePrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("renterToken", null)

        if (!token.isNullOrBlank()) {
            try {
                val response = RetrofitInstance.api.getCurrentUser("Bearer $token")
                if (response.isSuccessful) {
                    val data = response.body()
                    name = data?.get("name")?.toString() ?: "Unknown"
                    email = data?.get("email")?.toString() ?: "Unknown"
                } else {
                    name = "Unauthorized"
                    email = "Failed: ${response.code()}"
                }
            } catch (e: Exception) {
                name = "Error"
                email = e.localizedMessage ?: "Unknown error"
            }
        } else {
            name = "Not logged in"
            email = "No token found"
        }
        isLoading = false
    }

    Scaffold(
        containerColor = surfaceColor,
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
                        "Profile",
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
                            Icon(Icons.Default.Receipt, contentDescription = "Payment History", tint = Color.White.copy(alpha = 0.7f))
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
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile header with gradient background
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        tealColor.copy(alpha = 0.8f),
                                        lightTeal.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name.take(1).uppercase(),
                                        color = tealColor,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isEditing) {
                                    OutlinedTextField(
                                        value = newName,
                                        onValueChange = { newName = it },
                                        label = { Text("New Name", color = Color.White) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.White,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                            focusedLabelColor = Color.White,
                                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                            cursorColor = Color.White,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        OutlinedButton(
                                            onClick = { isEditing = false },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color.White
                                            ),
                                            border = BorderStroke(1.dp, Color.White),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel")
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Button(
                                            onClick = {
                                                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                                val token = prefs.getString("jwt_token", null)
                                                if (!token.isNullOrBlank()) {
                                                    scope.launch {
                                                        try {
                                                            val result = RetrofitInstance.api.updateRenterName("Bearer $token", mapOf("name" to newName))
                                                            if (result.isSuccessful) {
                                                                name = newName
                                                                isEditing = false
                                                                Toast.makeText(context, "Name updated successfully", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(context, "Failed to update name: ${result.code()}", Toast.LENGTH_SHORT).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.White,
                                                contentColor = tealColor
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Save", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        IconButton(
                                            onClick = {
                                                newName = name
                                                isEditing = true
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit Name",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = email,
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings options with updated options and navigation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlueColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )

                        // 1. Notifications - Navigate to Notifications tab
                        Surface(
                            onClick = { onNotificationsClick("Notifications") },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Notifications",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(tealColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Notifications,
                                            contentDescription = "Notifications",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Go to notifications",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp))

                        // 2. Reminders - Navigate to Reminders tab
                        Surface(
                            onClick = { onNotificationsClick("Reminders") },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Reminders",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(tealColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Alarm,
                                            contentDescription = "Reminders",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Go to reminders",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp))

                        // 3. Help & Support
                        Surface(
                            onClick = { onHelpSupportClick() },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Help & Support",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(tealColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Help,
                                            contentDescription = "Help",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Go to help",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp))

                        // 4. About Us
                        Surface(
                            onClick = { onAboutUsClick() },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "About Us",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(tealColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Info,
                                            contentDescription = "About Us",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "Go to about us",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account actions with improved styling
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlueColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )

                        // Delete Account Button
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = deleteRedColor
                            ),
                            border = BorderStroke(1.dp, deleteRedColor.copy(alpha = 0.5f)),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Account",
                                    tint = deleteRedColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Delete Account",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Log Out Button
                        Button(
                            onClick = {
                                context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit {
                                    remove("jwt_token")
                                }
                                onLogOutClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tealColor,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Logout,
                                    contentDescription = "Log Out",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Log Out",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog with improved styling
        AnimatedVisibility(
            visible = showDeleteConfirmation,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = deleteRedColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Delete Account",
                            fontWeight = FontWeight.Bold,
                            color = deleteRedColor,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Text(
                        "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently lost.",
                        color = Color.DarkGray,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                            val token = prefs.getString("jwt_token", null)
                            if (!token.isNullOrBlank()) {
                                scope.launch {
                                    try {
                                        val result = RetrofitInstance.api.deleteRenter("Bearer $token")
                                        if (result.isSuccessful) {
                                            prefs.edit().remove("jwt_token").apply()
                                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                            onLogOutClick()
                                        } else {
                                            Toast.makeText(context, "Delete failed: ${result.code()}", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = deleteRedColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete", fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirmation = false },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Text("Cancel", color = Color.DarkGray)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 8.dp
            )
        }
    }
}