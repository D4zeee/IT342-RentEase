package com.example.rentease.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentSuccessPage(
    navController: NavController,
    unitName: String?,
    startDate: String?,
    rentalFee: Double?,
    transactionId: String? = null
) {
    // Enhanced color palette
    val primaryColor = Color(0xFF147B93)
    val secondaryColor = Color(0xFF0A3F52)
    val accentColor = Color(0xFF4ECDC4)
    val whiteColor = Color.White
    val cardColor = Color(0xFFF7F9FA)
    val textSecondaryColor = Color(0xFF78909C)
    val successGreen = Color(0xFF4CAF50)

    // Animation states
    var showCheckmark by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var showThankYou by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Format values with fallbacks
    val displayUnitName = unitName ?: "N/A"
    val displayStartDate = startDate ?: "N/A"
    val displayRentalFee = if (rentalFee != null) "₱${String.format("%.2f", rentalFee)}" else "N/A"

    // Current date for receipt
    val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
    val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

    // Animated scale for checkmark
    val checkmarkScale by animateFloatAsState(
        targetValue = if (showCheckmark) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkmarkScale"
    )

    // Pulsating animation for the circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulseAnimation")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Background gradient
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            primaryColor,
            secondaryColor
        )
    )

    // Trigger animations sequentially
    LaunchedEffect(key1 = true) {
        showCheckmark = true
        delay(300)
        showTitle = true
        delay(200)
        showMessage = true
        delay(300)
        showReceipt = true
        delay(200)
        showThankYou = true
        delay(200)
        showButton = true
    }

    // Background with decorative elements
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw decorative circles
            for (i in 0..20) {
                val radius = (10..60).random().dp.toPx()
                val x = (0..size.width.toInt()).random().toFloat()
                val y = (0..size.height.toInt()).random().toFloat()

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Animated checkmark
            AnimatedVisibility(
                visible = showCheckmark,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            animationSpec = tween(500, easing = LinearOutSlowInEasing),
                            initialOffsetY = { -200 }
                        )
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = successGreen
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(successGreen, primaryColor)
                            )
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Success",
                            tint = whiteColor,
                            modifier = Modifier
                                .size(60.dp)
                                .scale(checkmarkScale)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App name with animation
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(500)) +
                        expandVertically(animationSpec = tween(500))
            ) {
                Text(
                    text = "RentEase",
                    color = whiteColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Success message with animation
            AnimatedVisibility(
                visible = showMessage,
                enter = fadeIn(animationSpec = tween(500)) +
                        expandVertically(animationSpec = tween(500))
            ) {
                Text(
                    text = "Payment Successful!",
                    color = whiteColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )
            }

            // Payment receipt card with animation
            AnimatedVisibility(
                visible = showReceipt,
                enter = fadeIn(animationSpec = tween(700)) +
                        expandVertically(animationSpec = tween(700))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Receipt header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Payment Receipt",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor
                            )

                            Icon(
                                imageVector = Icons.Rounded.Receipt,
                                contentDescription = "Receipt",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        //Date
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Date & Time",
                                fontSize = 12.sp,
                                color = textSecondaryColor
                            )
                            Text(
                                text = "$currentDate, $currentTime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondaryColor
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Unit Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Unit",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Unit Name",
                                    fontSize = 14.sp,
                                    color = textSecondaryColor
                                )
                                Text(
                                    text = displayUnitName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        // Due Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Due Date",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Due Date",
                                    fontSize = 14.sp,
                                    color = textSecondaryColor
                                )
                                Text(
                                    text = displayStartDate,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        // Amount
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payments,
                                contentDescription = "Amount",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Amount Paid",
                                    fontSize = 14.sp,
                                    color = textSecondaryColor
                                )
                                Text(
                                    text = displayRentalFee,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Payment Status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(successGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Paid",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = successGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Payment Method
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Payment Method",
                                fontSize = 14.sp,
                                color = textSecondaryColor
                            )
                            Text(
                                text = "PayMongo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondaryColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Go back to home button with animation
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            animationSpec = tween(500),
                            initialOffsetY = { 100 }
                        )
            ) {
                ElevatedButton(
                    onClick = {
                        // Navigate to Dashboard screen
                        navController.navigate("dashboard") {
                            // Clear the back stack so user can't go back to this success page
                            popUpTo("dashboard") { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = whiteColor,
                        contentColor = primaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Go Back to Home",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowRight,
                            contentDescription = "Go Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("RentEasePrefs", android.content.Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("renterToken", null)
}

