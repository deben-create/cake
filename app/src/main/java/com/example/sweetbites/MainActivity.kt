package com.example.sweetbites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sweetbites.data.Recipe
import com.example.sweetbites.ui.theme.SweetBitesTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SweetBitesApp(viewModel)
        }
    }
}

private val categories = listOf("All", "Cakes", "Cupcakes", "Cookies", "Brownies", "Cheesecakes", "Puddings", "No Bake", "Healthy")

@Composable
fun SweetBitesApp(vm: MainViewModel) {
    val recipes by vm.recipes.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val recent by vm.recent.collectAsStateWithLifecycle()

    var page by remember { mutableStateOf("Home") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    var darkMode by remember { mutableStateOf(false) }
    var servings by remember { mutableIntStateOf(8) }
    var cookStep by remember { mutableIntStateOf(0) }

    SweetBitesTheme(darkTheme = darkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val recipe = selectedRecipe

            when (page) {
                "Details" -> {
                    if (recipe != null) {
                        BackHandler { page = "Home" }
                        RecipeDetails(
                            recipe = recipe,
                            onBack = { page = "Home" },
                            onFavorite = { vm.toggleFavorite(recipe) },
                            onCook = {
                                cookStep = 0
                                page = "Cooking"
                            },
                            onCalculator = {
                                servings = recipe.servings
                                page = "Calculator"
                            }
                        )
                    } else {
                        page = "Home"
                    }
                }
                "Cooking" -> {
                    if (recipe != null) {
                        BackHandler { page = "Details" }
                        CookingScreen(
                            recipe = recipe,
                            step = cookStep,
                            onStep = { cookStep = it },
                            onBack = { page = "Details" },
                            onDone = { page = "Details" }
                        )
                    } else {
                        page = "Home"
                    }
                }
                "Search" -> {
                    BackHandler { page = "Home" }
                    SearchScreen(
                        recipes = recipes,
                        query = search,
                        onQuery = { search = it },
                        onBack = { page = "Home" },
                        onOpen = {
                            selectedRecipe = it
                            vm.markViewed(it.id)
                            page = "Details"
                        },
                        onFavorite = { vm.toggleFavorite(it) }
                    )
                }
                "Calculator" -> {
                    val calcRecipe = recipe ?: recipes.firstOrNull()
                    if (calcRecipe != null) {
                        BackHandler { page = if (recipe != null) "Details" else "More" }
                        CalculatorScreen(
                            recipe = calcRecipe,
                            servings = servings,
                            onServings = { servings = it },
                            onBack = { page = if (recipe != null) "Details" else "More" }
                        )
                    } else {
                        page = "Home"
                    }
                }
                "Shopping" -> {
                    BackHandler { page = "More" }
                    ShoppingScreen(recipes = recipes, onBack = { page = "More" })
                }
                "Recent" -> {
                    BackHandler { page = "More" }
                    RecentScreen(
                        recipes = recent,
                        onBack = { page = "More" },
                        onOpen = {
                            selectedRecipe = it
                            vm.markViewed(it.id)
                            page = "Details"
                        }
                    )
                }
                "About" -> {
                    BackHandler { page = "More" }
                    AboutScreen(onBack = { page = "More" })
                }
                else -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        contentWindowInsets = WindowInsets.safeDrawing,
                        bottomBar = {
                            BottomBar(page = page, onNavigate = { page = it })
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            when (page) {
                                "Home" -> HomeScreen(
                                    recipes = recipes,
                                    recent = recent,
                                    onSearch = { page = "Search" },
                                    onOpen = {
                                        selectedRecipe = it
                                        vm.markViewed(it.id)
                                        page = "Details"
                                    },
                                    onFavorite = { vm.toggleFavorite(it) },
                                    onExplore = { page = "Explore" },
                                    onCategory = {
                                        selectedCategory = it
                                        page = "Explore"
                                    }
                                )
                                "Explore" -> ExploreScreen(
                                    recipes = recipes,
                                    category = selectedCategory,
                                    onCategory = { selectedCategory = it },
                                    onSearch = { page = "Search" },
                                    onOpen = {
                                        selectedRecipe = it
                                        vm.markViewed(it.id)
                                        page = "Details"
                                    },
                                    onFavorite = { vm.toggleFavorite(it) }
                                )
                                "Favorites" -> FavoritesScreen(
                                    recipes = favorites,
                                    onOpen = {
                                        selectedRecipe = it
                                        vm.markViewed(it.id)
                                        page = "Details"
                                    },
                                    onFavorite = { vm.toggleFavorite(it) }
                                )
                                "More" -> MoreScreen(
                                    darkMode = darkMode,
                                    onDarkMode = { darkMode = it },
                                    onShopping = { page = "Shopping" },
                                    onRecent = { page = "Recent" },
                                    onCalculator = {
                                        selectedRecipe = recipes.firstOrNull()
                                        servings = recipes.firstOrNull()?.servings ?: 8
                                        page = "Calculator"
                                    },
                                    onAbout = { page = "About" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(page: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        listOf(
            Triple("Home", Icons.Default.Home, "Home"),
            Triple("Explore", Icons.Default.Search, "Explore"),
            Triple("Favorites", Icons.Default.Favorite, "Favorites"),
            Triple("More", Icons.Default.GridView, "More")
        ).forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = page == route,
                onClick = { onNavigate(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp) },
                alwaysShowLabel = true,
                modifier = Modifier.testTag("nav_tab_${route.lowercase()}")
            )
        }
    }
}

@Composable
private fun HomeScreen(
    recipes: List<Recipe>,
    recent: List<Recipe>,
    onSearch: () -> Unit,
    onOpen: (Recipe) -> Unit,
    onFavorite: (Recipe) -> Unit,
    onExplore: () -> Unit,
    onCategory: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.testTag("home_screen_scroll")
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Sweet Bites ♡",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Little treats, lovely moments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧁", fontSize = 24.sp)
                }
            }
        }
        item { SearchBarCard(onClick = onSearch) }
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFF8DDE6))
                    .clickable { recipes.firstOrNull()?.let(onOpen) }
                    .padding(20.dp)
                    .testTag("featured_banner")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "BAKE SOMETHING LOVELY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9E3F61),
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            "Your next\nsweet obsession",
                            fontSize = 25.sp,
                            lineHeight = 29.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF54273A)
                        )
                        Text(
                            "100% offline, pure delight",
                            fontSize = 12.sp,
                            color = Color(0xFF7D4A5C)
                        )
                        Button(
                            onClick = onExplore,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 7.dp),
                            modifier = Modifier.testTag("explore_recipes_btn")
                        ) {
                            Text("Explore recipes")
                        }
                    }
                    Box(
                        Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFFFF2F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍰", fontSize = 70.sp)
                    }
                }
            }
        }
        item { SectionHeader("Browse by category", "See all", onExplore) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(
                    listOf(
                        "Cakes" to "🎂",
                        "Cupcakes" to "🧁",
                        "Cookies" to "🍪",
                        "Brownies" to "🍫",
                        "Puddings" to "🍮",
                        "No Bake" to "🍓"
                    )
                ) { (name, emoji) ->
                    Column(
                        Modifier
                            .width(76.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onCategory(name) }
                            .padding(vertical = 12.dp)
                            .testTag("cat_tile_${name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 25.sp)
                        }
                        Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
        }
        item { SectionHeader("Popular this week", "View all", onExplore) }
        items(recipes.take(6), key = { it.id }) {
            RecipeCard(it, onClick = { onOpen(it) }, onFavorite = { onFavorite(it) })
        }
        if (recent.isNotEmpty()) {
            item { SectionHeader("Pick up where you left off", "", {}) }
            items(recent.take(3), key = { "recent_${it.id}" }) {
                RecipeCard(it, onClick = { onOpen(it) }, onFavorite = { onFavorite(it) })
            }
        }
    }
}

@Composable
private fun SearchBarCard(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp)
            .testTag("search_bar_trigger"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        Text(
            "Search cakes, ingredients...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (action.isNotBlank()) {
            Text(
                action,
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAction() }
                    .padding(5.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit, onFavorite: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("recipe_card_${recipe.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DessertTile(recipe, Modifier.size(104.dp))
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                recipe.category.uppercase(),
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                recipe.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${recipe.prepMinutes + recipe.bakeMinutes} min  •  ${recipe.difficulty}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PeopleAlt,
                    contentDescription = null,
                    Modifier.size(13.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${recipe.servings} servings",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(
            onClick = onFavorite,
            modifier = Modifier.testTag("favorite_btn_${recipe.id}")
        ) {
            Icon(
                if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DessertTile(recipe: Recipe, modifier: Modifier = Modifier) {
    val bg = runCatching { Color(android.graphics.Color.parseColor(recipe.colorHex)) }
        .getOrDefault(Color(0xFFF8DDE6))
    Box(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(recipe.emoji, fontSize = 45.sp)
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(7.dp)
                .size(23.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                Modifier.size(13.dp),
                tint = Color(0xFFB95677)
            )
        }
    }
}

@Composable
private fun ExploreScreen(
    recipes: List<Recipe>,
    category: String,
    onCategory: (String) -> Unit,
    onSearch: () -> Unit,
    onOpen: (Recipe) -> Unit,
    onFavorite: (Recipe) -> Unit
) {
    val filtered = recipes.filter {
        category == "All" || it.category.equals(category, true) || (category == "No Bake" && it.category == "No Bake")
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.testTag("explore_screen_list")
    ) {
        item {
            Text("Explore recipes", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Find your next favourite dessert.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { SearchBarCard(onSearch) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { onCategory(cat) },
                        label = { Text(cat) },
                        modifier = Modifier.testTag("chip_${cat.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }
        item {
            Text(
                "${filtered.size} delicious ideas",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(filtered, key = { it.id }) {
            RecipeCard(it, onClick = { onOpen(it) }, onFavorite = { onFavorite(it) })
        }
        if (filtered.isEmpty()) {
            item { EmptyState("No recipes here yet", "Try another category to find something sweet.", "🍓") }
        }
    }
}

@Composable
private fun FavoritesScreen(
    recipes: List<Recipe>,
    onOpen: (Recipe) -> Unit,
    onFavorite: (Recipe) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.testTag("favorites_screen_list")
    ) {
        item {
            Text("Your favourites", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("All the treats you want to bake again.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (recipes.isEmpty()) {
            item { EmptyState("A little love goes here", "Tap the heart on any recipe to save it for later.", "💗") }
        }
        items(recipes, key = { it.id }) {
            RecipeCard(it, onClick = { onOpen(it) }, onFavorite = { onFavorite(it) })
        }
    }
}

@Composable
private fun RecipeDetails(
    recipe: Recipe,
    onBack: () -> Unit,
    onFavorite: () -> Unit,
    onCook: () -> Unit,
    onCalculator: () -> Unit
) {
    var checked by remember(recipe.id) { mutableStateOf(setOf<Int>()) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 32.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("recipe_details_screen")
    ) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFFF7DDE6))
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(recipe.emoji, fontSize = 130.sp)
                }
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 14.dp, top = 8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .testTag("details_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF54273A))
                }
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(end = 14.dp, top = 8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .testTag("details_favorite_btn")
                ) {
                    Icon(
                        if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color(0xFFB9436B)
                    )
                }
                Surface(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Text(
                        "  ✨  OFFLINE RECIPE  ",
                        Modifier.padding(vertical = 7.dp),
                        fontSize = 10.sp,
                        color = Color(0xFF9E3F61),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    recipe.category.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    recipe.title,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 34.sp
                )
                Text(
                    recipe.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill("⏱", "${recipe.prepMinutes + recipe.bakeMinutes} min")
                    InfoPill("♨", recipe.difficulty)
                    InfoPill("♧", "${recipe.servings} servings")
                }
                Button(
                    onClick = onCook,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_cooking_btn"),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start baking mode", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onCalculator,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_calculator_btn"),
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Serving calculator")
                }
            }
        }
        item {
            HorizontalDivider(
                Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Ingredients",
                        Modifier.weight(1f),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${checked.size}/${recipe.ingredientLines().size} prepared",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                recipe.ingredientLines().forEachIndexed { index, ingredient ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                checked = if (index in checked) checked - index else checked + index
                            }
                            .padding(vertical = 7.dp)
                            .testTag("ingredient_item_$index"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index in checked,
                            onCheckedChange = {
                                checked = if (it) checked + index else checked - index
                            }
                        )
                        Text(
                            ingredient,
                            Modifier.weight(1f),
                            fontSize = 14.sp,
                            color = if (index in checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        item {
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("How to make it", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                recipe.stepLines().forEachIndexed { index, step ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            step,
                            Modifier
                                .weight(1f)
                                .padding(top = 5.dp),
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        Modifier.padding(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("💡", fontSize = 22.sp)
                        Column {
                            Text("Baker's note", fontWeight = FontWeight.Bold)
                            Text(recipe.tip, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPill(emoji: String, text: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 13.sp)
        Spacer(Modifier.width(5.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CookingScreen(
    recipe: Recipe,
    step: Int,
    onStep: (Int) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val steps = recipe.stepLines()
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .testTag("cooking_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("cooking_close_btn")) {
                Icon(Icons.Default.Close, contentDescription = "Exit baking mode")
            }
            Column {
                Text(
                    "BAKING MODE",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
        LinearProgressIndicator(
            progress = { (step + 1).toFloat() / steps.size.coerceAtLeast(1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
        )
        Text(
            "STEP ${step + 1} OF ${steps.size}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(if (step == 0) "👩‍🍳" else "🥣", fontSize = 72.sp)
                Text(
                    steps.getOrElse(step) { "All done! Your dessert is ready to enjoy." },
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 31.sp
                )
                Text(
                    "Take your time. Great baking is made one small step at a time.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { if (step > 0) onStep(step - 1) },
                enabled = step > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("cooking_prev_btn"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Previous")
            }
            Button(
                onClick = {
                    if (step < steps.lastIndex) onStep(step + 1) else onDone()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("cooking_next_btn"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (step < steps.lastIndex) "Next step" else "Finish 🎉")
            }
        }
    }
}

@Composable
private fun SearchScreen(
    recipes: List<Recipe>,
    query: String,
    onQuery: (String) -> Unit,
    onBack: () -> Unit,
    onOpen: (Recipe) -> Unit,
    onFavorite: (Recipe) -> Unit
) {
    val filtered = recipes.filter {
        query.isBlank() ||
                it.title.contains(query, true) ||
                it.category.contains(query, true) ||
                it.ingredients.contains(query, true) ||
                it.description.contains(query, true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .testTag("search_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Search recipes", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQuery,
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Try 'chocolate', 'flour', 'cookie'...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQuery("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "${filtered.size} recipes found",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) {
                RecipeCard(it, onClick = { onOpen(it) }, onFavorite = { onFavorite(it) })
            }
            if (filtered.isEmpty()) {
                item {
                    EmptyState("Nothing sweet found", "Try another recipe name or ingredient.", "🔎")
                }
            }
        }
    }
}

@Composable
private fun CalculatorScreen(
    recipe: Recipe,
    servings: Int,
    onServings: (Int) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("calculator_screen")
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("calc_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Serving calculator", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DessertTile(recipe, Modifier.size(76.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(recipe.title, fontWeight = FontWeight.Bold)
                    Text(
                        "Original recipe: ${recipe.servings} servings",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("How many servings?", fontWeight = FontWeight.SemiBold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { onServings((servings - 1).coerceAtLeast(1)) },
                            modifier = Modifier.testTag("servings_minus_btn")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease servings")
                        }
                        Text("$servings", fontSize = 35.sp, fontWeight = FontWeight.ExtraBold)
                        FilledTonalIconButton(
                            onClick = { onServings((servings + 1).coerceAtMost(48)) },
                            modifier = Modifier.testTag("servings_plus_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase servings")
                        }
                    }
                    Text("Quantities scale automatically", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Text("Adjusted ingredients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        items(recipe.ingredientLines()) { ingredient ->
            val amountRegex = Regex("^(\\d+(?:[./]\\d+)?(?:\\s*½|\\s*¼|\\s*¾)?)")
            val match = amountRegex.find(ingredient)
            val factor = servings.toDouble() / recipe.servings
            val scaled = if (match != null) {
                "${match.value} × ${"%.2f".format(factor)}${ingredient.removePrefix(match.value)}"
            } else {
                ingredient
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(scaled, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun MoreScreen(
    darkMode: Boolean,
    onDarkMode: (Boolean) -> Unit,
    onShopping: () -> Unit,
    onRecent: () -> Unit,
    onCalculator: () -> Unit,
    onAbout: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.testTag("more_screen_list")
    ) {
        item {
            Text("A little more", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text("Your kitchen, your way.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🧁", fontSize = 44.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Sweet Bites", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Your offline baking companion", fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Text(
                "YOUR KITCHEN",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
        item {
            SettingsRow(
                icon = Icons.Default.ShoppingBasket,
                title = "Shopping list",
                subtitle = "Tick off what you need",
                onClick = onShopping,
                testTag = "row_shopping"
            )
        }
        item {
            SettingsRow(
                icon = Icons.Default.History,
                title = "Recently viewed",
                subtitle = "Jump back into a recipe",
                onClick = onRecent,
                testTag = "row_recent"
            )
        }
        item {
            SettingsRow(
                icon = Icons.Default.Calculate,
                title = "Serving calculator",
                subtitle = "Scale a recipe up or down",
                onClick = onCalculator,
                testTag = "row_calculator"
            )
        }
        item {
            Text(
                "PREFERENCES",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Dark mode", fontWeight = FontWeight.SemiBold)
                    Text("A softer look for late-night baking", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkMode,
                    modifier = Modifier.testTag("dark_mode_switch")
                )
            }
        }
        item {
            SettingsRow(
                icon = Icons.Default.Info,
                title = "About Sweet Bites",
                subtitle = "Version 1.0 · Made with love",
                onClick = onAbout,
                testTag = "row_about"
            )
        }
        item {
            Text(
                "♡  Made for sweet moments — works 100% offline.",
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ShoppingScreen(recipes: List<Recipe>, onBack: () -> Unit) {
    val allIngredients = remember(recipes) {
        recipes.flatMap { it.ingredientLines() }
            .distinctBy { it.lowercase().substringAfterLast(' ') }
            .take(24)
    }
    var checked by remember { mutableStateOf(setOf<String>()) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .testTag("shopping_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("shopping_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text("Shopping list", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${checked.size} of ${allIngredients.size} items checked",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { checked.size.toFloat() / allIngredients.size.coerceAtLeast(1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(allIngredients) { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            checked = if (item in checked) checked - item else checked + item
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item in checked,
                        onCheckedChange = {
                            checked = if (it) checked + item else checked - item
                        }
                    )
                    Text(
                        item,
                        Modifier.weight(1f),
                        color = if (item in checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        OutlinedButton(
            onClick = { checked = emptySet() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_shopping_btn"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Reset checklist")
        }
        Text(
            "Tip: Open any recipe to check ingredients as you bake.",
            Modifier.padding(top = 12.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecentScreen(recipes: List<Recipe>, onBack: () -> Unit, onOpen: (Recipe) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("recent_screen")
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("recent_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Recently viewed", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (recipes.isEmpty()) {
            item {
                EmptyState("Your story starts here", "Open a recipe and it will appear here for quick access.", "📖")
            }
        }
        items(recipes, key = { it.id }) {
            RecipeCard(it, onClick = { onOpen(it) }, onFavorite = {})
        }
    }
}

@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("about_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("About the app", fontSize = 23.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text("🧁", fontSize = 86.sp)
        Text(
            "Sweet Bites",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Cake & Dessert Recipes", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(shape = RoundedCornerShape(22.dp)) {
            Column(
                Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Bake happiness with every bite.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Sweet Bites keeps your favourite cakes and desserts close at hand. Recipes, search, favourites and cooking steps are stored securely on your device with Room, so you can bake anywhere — even without Wi-Fi or mobile data.",
                    lineHeight = 23.sp
                )
                Text("Version 1.0.0", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
        Text(
            "No account needed. 100% offline. Just something sweet.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, emoji: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(emoji, fontSize = 54.sp)
        Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
