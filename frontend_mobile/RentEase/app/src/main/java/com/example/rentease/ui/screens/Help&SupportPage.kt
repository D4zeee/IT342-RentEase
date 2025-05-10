package com.example.rentease.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import android.widget.Toast

@Composable
fun HelpAndSupportPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Color scheme
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)
    val surfaceColor = Color(0xFFF5F7FA)
    val dividerColor = Color(0xFFE0E0E0)

    // State variables
    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
                        "Help & Support",
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
                        IconButton(onClick = onProfileClick) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
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
                // Contact Options Card
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
                            "Contact Us",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlueColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )

                        // Email Option
                        Surface(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:joshuadaniel.pusing@cit.edu")
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Email Support",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "joshuadaniel.pusing@cit.edu",
                                        fontSize = 14.sp,
                                        color = Color.Gray
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
                                            Icons.Outlined.Email,
                                            contentDescription = "Email",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.OpenInNew,
                                        contentDescription = "Open Email",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp))

                        // Phone Option
                        Surface(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+639295674961")
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Call Support",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "+63-929-567-4961",
                                        fontSize = 14.sp,
                                        color = Color.Gray
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
                                            Icons.Outlined.Phone,
                                            contentDescription = "Phone",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        Icons.Default.OpenInNew,
                                        contentDescription = "Call",
                                        tint = Color.Gray
                                    )
                                }
                            )
                        }

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp))

                        // Live Chat Option
                        Surface(
                            onClick = { /* Handle live chat click */ },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Live Chat",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "Available 24/7",
                                        fontSize = 14.sp,
                                        color = Color.Gray
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
                                            Icons.Outlined.Chat,
                                            contentDescription = "Chat",
                                            tint = tealColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.Green)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // FAQ Section
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
                            "Frequently Asked Questions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlueColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )

                        // FAQ Items
                        FaqItem(
                            question = "How do I pay my rent?",
                            answer = "You can pay your rent through the Payment section of the app. We accept credit/debit cards, bank transfers, and digital wallets."
                        )

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))

                        FaqItem(
                            question = "How do I report maintenance issues?",
                            answer = "Navigate to your room details and click on 'Report Issue'. Fill out the form with details about the problem, and our maintenance team will be notified immediately."
                        )

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))

                        FaqItem(
                            question = "Can I extend my lease?",
                            answer = "Yes, you can request a lease extension through the app. Go to your profile, select 'Lease Details', and click on 'Request Extension'. Our property manager will review your request and get back to you."
                        )

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))

                        FaqItem(
                            question = "How do I update my payment method?",
                            answer = "Go to the Payment section, select 'Payment Methods', and click on 'Add New Method' or edit an existing one. Follow the prompts to securely update your payment information."
                        )

                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))

                        FaqItem(
                            question = "What should I do if I'm locked out?",
                            answer = "If you're locked out during business hours (9 AM - 5 PM), please visit the property management office. For after-hours lockouts, call our emergency line at +1-800-RENTEASE-911."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Question row with expand/collapse functionality
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState),
                tint = Color.Gray
            )
        }

        // Expandable answer
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Text(
                text = answer,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}