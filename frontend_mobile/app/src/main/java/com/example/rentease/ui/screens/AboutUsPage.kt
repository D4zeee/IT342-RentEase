package com.example.rentease.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutUsPage(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRoomsClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPaymentHistoryClick: () -> Unit = {}
) {
    // Color scheme
    val tealColor = Color(0xFF147B93)
    val lightTeal = Color(0xFF1A97B5)
    val darkBlueColor = Color(0xFF0A3F52)
    val surfaceColor = Color(0xFFF5F7FA)
    val dividerColor = Color(0xFFE0E0E0)
    val accentColor = Color(0xFFFFA726) // Orange accent color

    // State variables
    val scrollState = rememberScrollState()

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
                        "About Us",
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
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Section with Logo and App Name
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        tealColor.copy(alpha = 0.8f),
                                        lightTeal.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Logo
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(
                                        width = 4.dp,
                                        color = accentColor,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RE",
                                    color = tealColor,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "RentEase",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Simplifying the Rental Experience",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Our Mission Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Section Title with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lightbulb,
                                contentDescription = "Mission",
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Our Mission",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkBlueColor
                            )
                        }

                        Text(
                            text = "To revolutionize the rental experience by creating a seamless, transparent, and efficient platform that connects renters and property owners.",
                            fontSize = 16.sp,
                            color = Color.DarkGray,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Purpose Section with Visual Enhancement
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Section Title with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Stars,
                                contentDescription = "Purpose",
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Our Purpose",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkBlueColor
                            )
                        }

                        // Feature highlights
                        FeatureItem(
                            icon = Icons.Outlined.Search,
                            title = "Browse Available Rooms",
                            description = "Easily find and explore available rental properties"
                        )

                        FeatureItem(
                            icon = Icons.Outlined.Payments,
                            title = "Track Rent Payments",
                            description = "Monitor payment history and upcoming dues"
                        )

                        FeatureItem(
                            icon = Icons.Outlined.Notifications,
                            title = "Automated Reminders",
                            description = "Never miss a payment with timely notifications"
                        )

                        FeatureItem(
                            icon = Icons.Outlined.Security,
                            title = "Secure Authentication",
                            description = "Protect your data with our robust security system"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "The purpose of RentEase is to simplify the rental process by providing an efficient platform for renters and property owners. With an intuitive interface, RentEase enhances the rental experience by ensuring transparency, efficiency, and convenience for both parties.",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scope Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Section Title with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Layers,
                                contentDescription = "Scope",
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Our Scope",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkBlueColor
                            )
                        }

                        // Platform Cards with matched heights
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Mobile App Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp), // Fixed height for both cards
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = tealColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize() // Fill the entire card
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center // Center content vertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PhoneAndroid,
                                        contentDescription = "Mobile App",
                                        tint = tealColor,
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Mobile App",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = darkBlueColor,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "For Renters",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Web Platform Card
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp), // Same fixed height as Mobile App card
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = tealColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize() // Fill the entire card
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center // Center content vertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Computer,
                                        contentDescription = "Web Platform",
                                        tint = tealColor,
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Web Platform",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = darkBlueColor,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "For Property Owners",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "The software products to be produced are the Mobile App for Renters and the Web Platform for Property Owners, which are distinct but integrated components of RentEase. Payments will be processed through PayMongo, a third-party payment gateway integrated into both platforms.",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Meet Our Team Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Section Title with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.People,
                                contentDescription = "Team",
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Meet Our Team",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkBlueColor
                            )
                        }

                        Text(
                            text = "The brilliant minds behind RentEase",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Developer Cards in a more visually appealing layout
                        DeveloperCard(
                            name = "Martin John V. Tabasa",
                            role = "Frontend Developer",
                            description = "Develops the web interface and user experience for property owners on the RentEase platform"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DeveloperCard(
                            name = "Joshua Daniel B. Pusing",
                            role = "Mobile Developer",
                            description = "Creates the mobile application for renters with intuitive interfaces and seamless functionality"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DeveloperCard(
                            name = "Paul Dave Q. Binoya",
                            role = "Backend Developer",
                            description = "Builds the robust backend systems and APIs that power the RentEase platform"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Version Information with enhanced styling
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = darkBlueColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RentEase",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Version 1.0.0",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "© 2023 RentEase. All rights reserved.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF147B93).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF147B93),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text content
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DeveloperCard(
    name: String,
    role: String,
    description: String,
    imageUrl: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF147B93).copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Developer Image with decorative border
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF147B93),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // If there's an image URL, load it, otherwise show initials
                if (imageUrl != null) {
                    // In a real app, you would use a library like Coil or Glide to load images
                    // For this example, we'll just show a placeholder
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Developer",
                        tint = Color(0xFF147B93),
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    // Show initials
                    Text(
                        text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        color = Color(0xFF147B93),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Developer Info with enhanced typography
            Column {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A3F52)
                )

                Text(
                    text = role,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF147B93)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}