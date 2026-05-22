package com.example.bitebook

import android.os.Bundle
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import coil.compose.AsyncImage
import android.net.Uri
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.bitebook.data.RecipeResponse
import com.example.bitebook.data.Ingredient
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

    val showNavigation = currentDestinationId != null &&
            currentDestinationId != R.id.loginFragment &&
            currentDestinationId != R.id.signUpFragment

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            if (showNavigation) {
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
fun HomeScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: BiteBookViewModel = viewModel()
) {
    val recipes = viewModel.recipes.collectAsLazyPagingItems()
    val userName by viewModel.userName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.triggerRefresh()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.triggerRefresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ... (Header remains mostly same, but maybe uses first letter of userName)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Avatar instead of logo if preferred, or keep logo.
                // Let's keep the logo but welcome the user.
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = Color(0xFFF08143)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.firstOrNull()?.toString() ?: "",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hello, $userName!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "BiteBook",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = "Explore Recipes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (recipes.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFF08143))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = recipes.itemCount,
                    key = recipes.itemKey { it._id },
                    contentType = recipes.itemContentType { "recipe" }
                ) { index ->
                    val recipe = recipes[index]
                    if (recipe != null) {
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe._id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: RecipeResponse,
    onClick: () -> Unit = {},
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Image
                if (recipe.image != null) {
                    AsyncImage(
                        model = recipe.image,
                        contentDescription = recipe.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(Color.LightGray)
                    ) {
                        Text(
                            "No Image",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                }

                // Edit and Delete Overlays (Only shown if callbacks are provided)
                if (onEdit != null || onDelete != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        onEdit?.let {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(36.dp),
                                shadowElevation = 2.dp
                            ) {
                                IconButton(onClick = it) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFFF08143)
                                    )
                                }
                            }
                        }
                        onDelete?.let {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(36.dp),
                                shadowElevation = 2.dp
                            ) {
                                IconButton(onClick = it) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF08143)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.prepTime + recipe.cookTime} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF08143)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.servings}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun AddRecipeScreen(
    modifier: Modifier = Modifier,
    recipeId: String? = null,
    onCancel: () -> Unit = {},
    onCreate: () -> Unit = {},
    onUpdate: () -> Unit = {},
    onError: (String) -> Unit = {},
    viewModel: BiteBookViewModel = viewModel()
) {
    val userRecipes = viewModel.userRecipes.collectAsLazyPagingItems()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("4") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val ingredients = remember { mutableStateListOf<Ingredient>(Ingredient("", "")) }
    val instructions = remember { mutableStateListOf<String>("") }

    val selectedRecipe by viewModel.selectedRecipe.collectAsState()

    androidx.compose.runtime.LaunchedEffect(recipeId) {
        if (recipeId != null) {
            // Try to find in current list first to avoid extra network call if possible
            val existing = (0 until userRecipes.itemCount).mapNotNull { userRecipes[it] }.find { it._id == recipeId }
            if (existing != null) {
                title = existing.title
                description = existing.description
                prepTime = existing.prepTime.toString()
                cookTime = existing.cookTime.toString()
                servings = existing.servings.toString()
                imageUri = existing.image?.let { Uri.parse(it) }
                ingredients.clear()
                ingredients.addAll(existing.ingredients)
                instructions.clear()
                instructions.addAll(existing.instructions)
            } else {
                viewModel.getRecipeById(recipeId)
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(selectedRecipe) {
        if (recipeId != null && selectedRecipe?._id == recipeId && title.isEmpty()) {
            selectedRecipe?.let { recipe ->
                title = recipe.title
                description = recipe.description
                prepTime = recipe.prepTime.toString()
                cookTime = recipe.cookTime.toString()
                servings = recipe.servings.toString()
                imageUri = recipe.image?.let { Uri.parse(it) }
                ingredients.clear()
                ingredients.addAll(recipe.ingredients)
                instructions.clear()
                instructions.addAll(recipe.instructions)
            }
        }
    }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ... (Header and Image Upload same as before)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = if (recipeId == null) "Create New Recipe" else "Edit Recipe",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recipe Image Upload
        Text(text = "Recipe Image", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Gray
                    )
                    Text(text = "Click to upload image", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recipe Title
        Text(text = "Recipe Title *", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Enter recipe title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(text = "Description *", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Describe your recipe") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prep Time, Cook Time, Servings
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Prep Time (min)", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = prepTime,
                    onValueChange = { prepTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Cook Time (min)", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = cookTime,
                    onValueChange = { cookTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Servings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ingredients Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ingredients", fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = { ingredients.add(Ingredient("", "")) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Add", style = MaterialTheme.typography.bodySmall)
            }
        }

        ingredients.forEachIndexed { index, ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ingredient.amount,
                    onValueChange = { ingredients[index] = Ingredient(it, ingredient.name) },
                    placeholder = { Text("Amount", fontSize = MaterialTheme.typography.bodySmall.fontSize) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = ingredient.name,
                    onValueChange = { ingredients[index] = Ingredient(ingredient.amount, it) },
                    placeholder = { Text("Ingredient name", fontSize = MaterialTheme.typography.bodySmall.fontSize) },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(8.dp)
                )
                IconButton(onClick = { if (ingredients.size > 1) ingredients.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Instructions", fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = { instructions.add("") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Add Step", style = MaterialTheme.typography.bodySmall)
            }
        }

        instructions.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (index + 1).toString(), fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(
                    value = step,
                    onValueChange = { instructions[index] = it },
                    placeholder = { Text("Describe this step...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                IconButton(onClick = { if (instructions.size > 1) instructions.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    isSaving = true
                    if (recipeId == null) {
                        viewModel.addRecipe(
                            title = title,
                            description = description,
                            image = imageUri?.toString(),
                            prepTime = prepTime.toIntOrNull() ?: 0,
                            cookTime = cookTime.toIntOrNull() ?: 0,
                            servings = servings.toIntOrNull() ?: 1,
                            ingredients = ingredients.toList(),
                            instructions = instructions.toList(),
                            onSuccess = {
                                isSaving = false
                                onCreate()
                            },
                            onError = { e ->
                                isSaving = false
                                onError(e.message ?: "Failed to save recipe")
                            }
                        )
                    } else {
                        viewModel.updateRecipe(
                            id = recipeId,
                            title = title,
                            description = description,
                            image = imageUri?.toString(),
                            prepTime = prepTime.toIntOrNull() ?: 0,
                            cookTime = cookTime.toIntOrNull() ?: 0,
                            servings = servings.toIntOrNull() ?: 1,
                            ingredients = ingredients.toList(),
                            instructions = instructions.toList(),
                            onSuccess = {
                                isSaving = false
                                onUpdate()
                            },
                            onError = { e ->
                                isSaving = false
                                onError(e.message ?: "Failed to update recipe")
                            }
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF08143)),
                enabled = title.isNotBlank() && description.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (recipeId == null) "Create Recipe" else "Save Changes", color = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: BiteBookViewModel = viewModel()
) {
    val userRecipes = viewModel.userRecipes.collectAsLazyPagingItems()
    val userName by viewModel.userName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    
    var isEditingProfile by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }
    var editedImageUri by remember { mutableStateOf<Uri?>(profileImageUri?.let { Uri.parse(it) }) }

    LaunchedEffect(Unit) {
        viewModel.triggerRefresh()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.triggerRefresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val profileImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        editedImageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F6))
    ) {
        // Top Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF08143))
                        .clickable(enabled = isEditingProfile) {
                            profileImagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isEditingProfile) {
                        if (editedImageUri != null) {
                            AsyncImage(
                                model = editedImageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Photo", tint = Color.White)
                        }
                    } else if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = userName.firstOrNull()?.toString() ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    if (isEditingProfile) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Name") },
                            modifier = Modifier.width(150.dp),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${userRecipes.itemCount} recipes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingProfile) {
                    IconButton(onClick = {
                        viewModel.updateProfile(editedName, editedImageUri?.toString())
                        isEditingProfile = false
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color(0xFF4CAF50))
                    }
                    IconButton(onClick = {
                        isEditingProfile = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color(0xFFF44336))
                    }
                } else {
                    IconButton(
                        onClick = { 
                            editedName = userName
                            editedImageUri = profileImageUri?.let { Uri.parse(it) }
                            isEditingProfile = true 
                        },
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Logout Button
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // "My Recipes" Title
        Text(
            text = "My Recipes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = userRecipes.itemCount,
                key = userRecipes.itemKey { it._id },
                contentType = userRecipes.itemContentType { "recipe" }
            ) { index ->
                val recipe = userRecipes[index]
                if (recipe != null) {
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe._id) },
                        onEdit = { onEditClick(recipe._id) },
                        onDelete = { onDeleteClick(recipe._id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: BiteBookViewModel = viewModel()
) {
    androidx.compose.runtime.LaunchedEffect(recipeId) {
        viewModel.getRecipeById(recipeId)
    }

    val recipe by viewModel.selectedRecipe.collectAsState()

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator(color = Color(0xFFF08143))
        }
        return
    }

    val nonNullRecipe = recipe!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Image Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Image
            if (nonNullRecipe.image != null) {
                AsyncImage(
                    model = nonNullRecipe.image,
                    contentDescription = nonNullRecipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                ) {
                    Text(
                        "No Image",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }
            }

            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = nonNullRecipe.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Author Section
            nonNullRecipe.author?.let { author ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF08143)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = author.name.firstOrNull()?.toString() ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "by ${author.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time and Servings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time and Servings
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFF08143)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${nonNullRecipe.prepTime + nonNullRecipe.cookTime} min",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFF08143)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${nonNullRecipe.servings} Servings",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Description",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nonNullRecipe.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Ingredients
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            nonNullRecipe.ingredients.forEach { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFFF08143), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${ingredient.amount} ${ingredient.name}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            nonNullRecipe.instructions.forEachIndexed { index, instruction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF08143)
                    )
                    Text(
                        text = instruction,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
