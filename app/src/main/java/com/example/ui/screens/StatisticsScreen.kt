package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PackageStatus
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PackageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: PackageViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateToHome()
    }

    val packages by viewModel.allPackages.collectAsState()

    val total = packages.size
    val delivered = packages.count { it.packageItem.status.isDelivered }
    val inTransit = packages.count { it.packageItem.status.isInProgress }
    val pickup = packages.count { it.packageItem.status == PackageStatus.AVAILABLE_FOR_PICKUP }
    val archived = packages.count { it.packageItem.isArchived }

    val successRate = if (total > 0) (delivered.toFloat() / total.toFloat()) else 0f

    // Carrier breakdown
    val carrierCounts = packages
        .groupBy { it.packageItem.carrier }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    // Category breakdown
    val categoryCounts = packages
        .groupBy { it.packageItem.category.ifBlank { "Autre" } }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistiques d'acheminement",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateToHome() },
                        modifier = Modifier.testTag("stats_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Slate800
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.72f)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Success Rate Card
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 3.dp,
                borderAlpha = 0.85f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Taux de livraison réussi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${(successRate * 100).toInt()}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldSuccess
                            )
                            Text(
                                text = "$delivered sur $total colis acheminés avec succès",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = EmeraldSuccess.copy(alpha = 0.12f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { successRate },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldSuccess,
                        trackColor = Slate200,
                    )
                }
            }

            // Quick Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricMiniCard(
                    title = "Total suivis",
                    value = total.toString(),
                    color = BlueAccent,
                    icon = Icons.Default.Inventory,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "En cours",
                    value = inTransit.toString(),
                    color = AmberExpress,
                    icon = Icons.Default.LocalShipping,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricMiniCard(
                    title = "Au relais",
                    value = pickup.toString(),
                    color = CyanPickup,
                    icon = Icons.Default.Place,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Archivés",
                    value = archived.toString(),
                    color = Slate600,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            // Carrier Distribution Card
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Répartition par transporteur",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (carrierCounts.isEmpty()) {
                        Text("Aucune donnée", color = Slate400, fontSize = 13.sp)
                    } else {
                        carrierCounts.forEach { (carrier, count) ->
                            val percentage = if (total > 0) count.toFloat() / total.toFloat() else 0f
                            val carrierColor = Color(carrier.brandColorHex)

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = carrier.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate800
                                    )
                                    Text(
                                        text = "$count (${(percentage * 100).toInt()}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Slate500
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { percentage },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (carrier.brandColorHex == 0xFFFFCC00.toLong()) BlueAccent else carrierColor,
                                    trackColor = Slate100,
                                )
                            }
                        }
                    }
                }
            }

            // Categories Breakdown Card
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Répartition par catégorie",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (categoryCounts.isEmpty()) {
                        Text("Aucune donnée", color = Slate400, fontSize = 13.sp)
                    } else {
                        categoryCounts.forEach { (cat, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    color = Slate700
                                )
                                Surface(
                                    color = Slate100,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "$count colis",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate700,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Reset sample data action
            OutlinedButton(
                onClick = { viewModel.resetSampleData() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_samples_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Recharger les colis de démonstration")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tint = Color.White.copy(alpha = 0.82f),
        elevation = 2.dp,
        borderAlpha = 0.8f
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = Slate500
                )
            }
        }
    }
}
