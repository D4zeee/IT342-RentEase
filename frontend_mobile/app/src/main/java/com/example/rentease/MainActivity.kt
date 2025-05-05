package com.example.rentease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rentease.ui.screens.*
import com.example.rentease.ui.theme.RentEaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentEaseTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginPage(
                onLoginClick = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleLoginClick = { /* Optional */ },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignupPage(
                onSignUpClick = {
                    navController.navigate("dashboard") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }

        composable("dashboard") {
            DashboardPage(
                onNotificationClick = { navController.navigate("reminders") },
                onProfileClick = { navController.navigate("user") },
                onRoomsClick = { navController.navigate("rooms") },
                onHomeClick = { /* Optional */ },
                onPaymentClick = { /* Optional */ },
                onPaymentHistoryClick = { navController.navigate("payment_history") }, // ✅ Fixed here
                onViewAllRoomsClick = { navController.navigate("rooms") }
            )
        }


        composable("user") {
            UserPage(
                onHomeClick = { navController.navigate("dashboard") },
                onLogOutClick = { navController.navigate("login") },
                onRoomsClick = { navController.navigate("rooms") },
                onPaymentClick = { navController.navigate("reminders") },
                onPaymentHistoryClick = { navController.navigate("payment_history") },
                onDeleteAccountClick = { /* Optional */ },
                onBackClick = { navController.navigate("dashboard") },
                onNotificationsClick = { tab ->
                    when (tab) {
                        "Notifications" -> navController.navigate("reminders") // Or a dedicated screen later
                        "Reminders" -> navController.navigate("reminders")
                    }
                },
                onHelpSupportClick = { navController.navigate("help_support") },
                onAboutUsClick = { navController.navigate("about_us") }
            )
        }

        composable("rooms") {
            RoomsPage(
                navController = navController,
                onBackClick = { navController.navigate("dashboard") },
                onHomeClick = { navController.navigate("dashboard") },
                onRoomsClick = { /* Already on Rooms */ },
                onPaymentClick = {},
                onPaymentHistoryClick = { navController.navigate("payment_history") }
            )
        }

        composable("reminders") {
            RemindersPage(
                onBackClick = { navController.navigate("dashboard") },
                onHomeClick = { navController.navigate("dashboard") },
                onRoomsClick = { navController.navigate("rooms") },
                onPaymentClick = { /* TODO */ },
                onPaymentHistoryClick = { navController.navigate("payment_history") }
            )
        }

        composable("book_page/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            BookPage(
                navController = navController,
                roomId = roomId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("book_success/{roomId}/{startDate}/{endDate}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val startDate = backStackEntry.arguments?.getString("startDate")
            val endDate = backStackEntry.arguments?.getString("endDate")

            BookSuccessPage(
                navController = navController,
                roomId = roomId,
                startDate = startDate,
                endDate = endDate
            )
        }

        composable("payment_history") {
            PaymentHistoryPage(
                onBackClick = { navController.navigate("dashboard") },
                onHomeClick = { navController.navigate("dashboard") },
                onRoomsClick = { navController.navigate("rooms") },
                onPaymentClick = { /* TODO */ },
                onPaymentHistoryClick = { navController.navigate("payment_history") }
            )
        }

        composable("help_support") {
            HelpAndSupportPage(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("dashboard") },
                onRoomsClick = { navController.navigate("rooms") },
                onPaymentClick = { navController.navigate("reminders") },
                onProfileClick = { navController.navigate("user") }
            )
        }

        composable("about_us") {
            AboutUsPage(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("dashboard") },
                onRoomsClick = { navController.navigate("rooms") },
                onPaymentClick = { navController.navigate("reminders") },
                onProfileClick = { navController.navigate("user") }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderPage(title: String, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("This is the $title page.", style = MaterialTheme.typography.headlineMedium)
        }
    }
}



