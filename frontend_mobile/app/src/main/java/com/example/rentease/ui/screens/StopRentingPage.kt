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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StopRentingSuccessPage(
    navController: NavController,
    roomId: String?,
    endDate: String?
) {
    // Enhanced color palette
    val primaryColor = Color(0xFF0F7A8A)
    val secondaryColor = Color(0xFF005566)
    val accentColor = Color(0xFF4ECDC4)
    val whiteColor = Color.White
    val cardColor = Color(0xFFF7F9FA)
    val textSecondaryColor = Color(0xFF78909C)
    val successColor = Color(0xFF4CAF50)

    // Animation states
    var showCheckmark by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }
    var showInfoMessage by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Use passed values with fallback if null
    val displayEndDate = endDate ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("RentEasePrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("renterToken", null)

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
        showCard = true
        delay(200)
        showInfoMessage = true
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
                            spotColor = accentColor
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(successColor, secondaryColor)
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

            Spacer(modifier = Modifier.height(32.dp))

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
                    text = "Stop Renting Confirmed!",
                    color = whiteColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
            }

            // Details card with animation
            AnimatedVisibility(
                visible = showCard,
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
                        Text(
                            text = "Stop Renting Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Room info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = "Room",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Room ID",
                                    fontSize = 14.sp,
                                    color = textSecondaryColor
                                )
                                Text(
                                    text = roomId ?: "Unknown",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        // End Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "End Date",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "End Date",
                                    fontSize = 14.sp,
                                    color = textSecondaryColor
                                )
                                Text(
                                    text = displayEndDate,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Info message with animation
            AnimatedVisibility(
                visible = showInfoMessage,
                enter = fadeIn(animationSpec = tween(500)) +
                        expandVertically(animationSpec = tween(500))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = successColor.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = successColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Your request to stop renting has been submitted successfully",
                            color = Color.Black.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        // Navigate back to Rooms screen
                        navController.navigate("rooms") {
                            popUpTo("rooms") { inclusive = false }
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
                            text = "Go Back to Rooms",
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
}