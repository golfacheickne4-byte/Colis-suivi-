package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PackageStatus
import com.example.ui.components.GlassCard
import com.example.ui.components.PackageCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterTab
import com.example.ui.viewmodel.PackageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PackageViewModel,
    modifier: Modifier = Modifier
) {
    val allPackages by viewModel.allPackages.collectAsState()
    val packages by viewModel.filteredPackages.collectAsState()
    val activeTab by viewModel.filterTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }

    // Statistics counts
    val activeCount = allPackages.count { !it.packageItem.isArchived && it.packageItem.status.isInProgress }
    val deliveredCount = allPackages.count { !it.packageItem.isArchived && it.packageItem.status.isDelivered }
    val pickupCount = allPackages.count { !it.packageItem.isArchived && it.packageItem.status == PackageStatus.AVAILABLE_FOR_PICKUP }

    val categories = listOf("High-Tech", "Mode & Sport", "Culture", "Accessoires", "Maison", "Cadeau")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BlueAccent,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ColisTrack",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Suivi de colis en direct",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = Slate700
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateToStatistics() },
                        modifier = Modifier.testTag("statistics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Statistiques",
                            tint = Slate700
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateToCarriers() },
                        modifier = Modifier.testTag("carriers_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Transporteurs",
                            tint = Slate700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.72f)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateToAdd() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nouveau colis", fontWeight = FontWeight.SemiBold) },
                containerColor = BlueAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("add_package_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input Row (collapsible)
            item {
                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("N° de suivi, nom du colis, transporteur...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Slate400)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = Slate400)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_field"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.88f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.72f),
                                focusedIndicatorColor = BlueAccent,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Hero Graphic & Quick Stats Banner
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Quick Stats Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "En cours",
                            count = activeCount,
                            color = BlueAccent,
                            icon = Icons.Default.LocalShipping,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setFilterTab(FilterTab.IN_PROGRESS) }
                        )
                        StatCard(
                            title = "En relais",
                            count = pickupCount,
                            color = CyanPickup,
                            icon = Icons.Default.Place,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setFilterTab(FilterTab.PICKUP) }
                        )
                        StatCard(
                            title = "Livrés",
                            count = deliveredCount,
                            color = EmeraldSuccess,
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setFilterTab(FilterTab.DELIVERED) }
                        )
                    }

                    // Hero Banner Image (Compact logistics graphic)
                    if (activeTab == FilterTab.ALL && searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_delivery_hero),
                                    contentDescription = "Suivi de colis logistique",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                colors = listOf(
                                                    NavyDark.copy(alpha = 0.85f),
                                                    NavyDark.copy(alpha = 0.35f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                        .padding(14.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column {
                                        Text(
                                            text = "Acheminement en direct",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "$activeCount colis en mouvement vers vous",
                                            color = AmberExpress,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = activeTab.ordinal,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                            color = BlueAccent
                        )
                    }
                ) {
                    FilterTab.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.setFilterTab(tab) },
                            text = {
                                Text(
                                    text = tab.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) BlueAccent else Slate500
                                )
                            },
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.setSelectedCategory(null) },
                            label = { Text("Toutes catégories", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueAccent.copy(alpha = 0.12f),
                                selectedLabelColor = BlueAccent
                            )
                        )
                    }
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueAccent.copy(alpha = 0.12f),
                                selectedLabelColor = BlueAccent
                            )
                        )
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${packages.size} ${if (packages.size > 1) "colis trouvés" else "colis trouvé"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500
                    )
                }
            }

            // Package List or Empty State
            if (packages.isEmpty()) {
                item {
                    EmptyStateView(
                        tab = activeTab,
                        hasSearch = searchQuery.isNotBlank(),
                        onAddClick = { viewModel.navigateToAdd() },
                        onResetSampleClick = { viewModel.resetSampleData() }
                    )
                }
            } else {
                items(packages, key = { it.packageItem.id }) { packageWithEvents ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        PackageCard(
                            packageWithEvents = packageWithEvents,
                            onClick = { viewModel.navigateToDetail(packageWithEvents.packageItem.id) },
                            onTogglePin = {
                                viewModel.togglePin(
                                    packageWithEvents.packageItem.id,
                                    packageWithEvents.packageItem.isPinned
                                )
                            },
                            onToggleArchive = {
                                viewModel.toggleArchive(
                                    packageWithEvents.packageItem.id,
                                    packageWithEvents.packageItem.isArchived
                                )
                            },
                            onDelete = { viewModel.deletePackage(packageWithEvents.packageItem.id) },
                            onSimulateNextStep = { viewModel.simulateNextStep(packageWithEvents) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        tint = Color.White.copy(alpha = 0.82f),
        elevation = 2.dp,
        borderAlpha = 0.8f
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate600
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    tab: FilterTab,
    hasSearch: Boolean,
    onAddClick: () -> Unit,
    onResetSampleClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        shape = RoundedCornerShape(20.dp),
        tint = Color.White.copy(alpha = 0.82f),
        elevation = 3.dp,
        borderAlpha = 0.85f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Slate100,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (hasSearch) Icons.Outlined.Search else Icons.Default.Inventory,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasSearch) {
                    "Aucun colis ne correspond à votre recherche"
                } else when (tab) {
                    FilterTab.IN_PROGRESS -> "Aucun colis en cours de livraison"
                    FilterTab.DELIVERED -> "Aucun colis livré dans cette section"
                    FilterTab.PICKUP -> "Aucun colis en attente au point relais"
                    FilterTab.ARCHIVED -> "Aucun colis archivé"
                    FilterTab.ALL -> "Aucun colis enregistré"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hasSearch) {
                    "Vérifiez l'orthographe du numéro de suivi ou du nom."
                } else {
                    "Ajoutez un numéro de suivi pour suivre son acheminement en temps réel."
                },
                fontSize = 13.sp,
                color = Slate500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(10.dp),
                    color = BlueAccent
                ) {
                    Text(
                        text = "+ Ajouter un colis",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                Surface(
                    onClick = onResetSampleClick,
                    shape = RoundedCornerShape(10.dp),
                    color = Slate100
                ) {
                    Text(
                        text = "Charger exemples",
                        color = Slate700,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
