package com.example.bitebook

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.bitebook.ui.theme.BiteBookTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BiteBook)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiteBookTheme {
                BiteBookApp()
            }
        }
    }
}

@Composable
fun BiteBookApp() {
    val activity = LocalContext.current as AppCompatActivity
    var navController by remember { mutableStateOf<NavController?>(null) }
    var currentDestinationId by remember { mutableStateOf<Int?>(null) }

    // Navigation listener to update UI when back stack changes
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
        }
        navController?.addOnDestinationChangedListener(listener)
        onDispose {
            navController?.removeOnDestinationChangedListener(listener)
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = currentDestinationId == destination.id,
                    onClick = {
                        val controller = navController ?: return@item
                        val destId = destination.id
                        
                        // Using the explicit member function to avoid ambiguity with type-safe routes
                        controller.navigate(
                            destId,
                            null,
                            navOptions {
                                popUpTo(controller.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        )
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AndroidView(
                modifier = Modifier.padding(innerPadding),
                factory = { ctx ->
                    val view = View.inflate(ctx, R.layout.main_activity, null)
                    val navHostFragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                    navController = navHostFragment.navController
                    view
                },
                update = { }
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val id: Int,
) {
    HOME("Home", Icons.Default.Home, R.id.homeFragment),
    ADD("Add", Icons.Default.Add, R.id.addRecipeFragment),
    PROFILE("Profile", Icons.Default.AccountBox, R.id.profileFragment),
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Welcome to BiteBook!", style = MaterialTheme.typography.headlineLarge)
        Text(text = "Discover and share your favorite recipes.")
    }
}

@Composable
fun AddRecipeScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Add a New Recipe", style = MaterialTheme.typography.headlineLarge)
        Text(text = "Form to add recipe details will go here.")
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val userRecipes = listOf("Pasta Carbonara", "Homemade Pizza", "Greek Salad", "Chocolate Brownies")

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Text(text = "Profile", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "User Info", style = MaterialTheme.typography.titleLarge)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: John Doe", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Email: john.doe@example.com", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "My Uploaded Recipes", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(userRecipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = recipe,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
