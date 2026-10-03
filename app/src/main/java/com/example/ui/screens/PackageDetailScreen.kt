package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PackageStatus
import com.example.ui.components.BarcodeCanvas
import com.example.ui.components.CarrierBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusChip
import com.example.ui.components.TrackingProgressBar
import com.example.ui.components.TrackingTimeline
import com.example.ui.theme.*
import com.example.ui.viewmodel.PackageViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailScreen(
    viewModel: PackageViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateToHome()
    }

    val selectedPackageWithEvents by viewModel.selectedPackage.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    // Dialog state for adding a custom tracking event
    var newEventTitle by remember { mutableStateOf("") }
    var newEventLocation by remember { mutableStateOf("") }
    var newEventDescription by remember { mutableStateOf("") }

    val pkgWithEvents = selectedPackageWithEvents
    if (pkgWithEvents == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Colis introuvable", color = Slate500)
        }
        return
    }

    val pkg = pkgWithEvents.packageItem

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Détails du colis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateToHome() },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Slate800
                        )
                    }
                },
                actions = {
                    // Pin button
                    IconButton(
                        onClick = { viewModel.togglePin(pkg.id, pkg.isPinned) }
                    ) {
                        Icon(
                            imageVector = if (pkg.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Épingler",
                            tint = if (pkg.isPinned) AmberExpress else Slate600
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Suivi Colis : ${pkg.title}"
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Suivi ${pkg.carrier.displayName} : ${pkg.title}\n" +
                                            "N° de suivi : ${pkg.trackingNumber}\n" +
                                            "Statut actuel : ${pkg.status.label}\n" +
                                            "Lien : ${pkg.carrier.buildTrackingUrl(pkg.trackingNumber)}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partager le suivi"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = Slate700
                        )
                    }

                    // Edit button
                    IconButton(
                        onClick = { viewModel.navigateToEdit(pkg) },
                        modifier = Modifier.testTag("edit_package_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = Slate700
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("delete_package_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
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
            // Main Overview Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 3.dp,
                borderAlpha = 0.85f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CarrierBadge(carrier = pkg.carrier, useShortName = false)
                        StatusChip(status = pkg.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = pkg.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "N° ${pkg.trackingNumber}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("tracking_number", pkg.trackingNumber)
                                clipboard.setPrimaryClip(clip)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Numéro de suivi copié !")
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier",
                                tint = Slate400,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Barcode graphic
                    BarcodeCanvas(
                        code = pkg.trackingNumber,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    TrackingProgressBar(status = pkg.status)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons row: Simulate / Advance step
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.simulateNextStep(pkgWithEvents)
                            },
                            enabled = !pkg.status.isDelivered,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("advance_step_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (pkg.status.isDelivered) "Colis déjà livré" else "Actualiser statut",
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showAddEventDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_custom_event_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter étape", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Delivery Details Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informations d'expédition",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    if (pkg.estimatedDeliveryDate.isNotBlank()) {
                        DetailRow(
                            icon = Icons.Default.CheckCircle,
                            iconColor = EmeraldSuccess,
                            label = "Date estimée",
                            value = pkg.estimatedDeliveryDate
                        )
                    }

                    if (pkg.destinationAddress.isNotBlank()) {
                        DetailRow(
                            icon = Icons.Default.Place,
                            iconColor = BlueAccent,
                            label = "Destination / Retrait",
                            value = pkg.destinationAddress
                        )
                    }

                    if (pkg.sender.isNotBlank()) {
                        DetailRow(
                            icon = Icons.Default.Storefront,
                            iconColor = AmberExpress,
                            label = "Expéditeur",
                            value = pkg.sender
                        )
                    }

                    if (pkg.recipient.isNotBlank()) {
                        DetailRow(
                            icon = Icons.Default.Person,
                            iconColor = Slate600,
                            label = "Destinataire",
                            value = pkg.recipient
                        )
                    }

                    if (pkg.weightKg > 0.0) {
                        DetailRow(
                            icon = Icons.Default.Scale,
                            iconColor = Slate600,
                            label = "Poids estimé",
                            value = "${pkg.weightKg} kg"
                        )
                    }

                    if (pkg.notes.isNotBlank()) {
                        HorizontalDivider(color = Slate200, thickness = 1.dp)
                        DetailRow(
                            icon = Icons.Default.Notes,
                            iconColor = Slate500,
                            label = "Notes personnelles",
                            value = pkg.notes
                        )
                    }
                }
            }

            // Carrier Quick Links & Actions Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Service Transporteur : ${pkg.carrier.displayName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Open Official Web Tracking
                        OutlinedButton(
                            onClick = {
                                val url = pkg.carrier.buildTrackingUrl(pkg.trackingNumber)
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(browserIntent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_web_tracking_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Suivi en ligne", fontSize = 12.sp)
                        }

                        // Call Customer Service if phone available
                        if (pkg.carrier.customerServicePhone.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val dialIntent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${pkg.carrier.customerServicePhone}")
                                    )
                                    context.startActivity(dialIntent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Service client", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Tracking History / Timeline Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historique d'acheminement",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )

                        Text(
                            text = "${pkgWithEvents.events.size} étapes",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TrackingTimeline(events = pkgWithEvents.events)
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Supprimer ce colis ?") },
            text = { Text("Ce colis et tout son historique d'acheminement seront définitivement supprimés.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deletePackage(pkg.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Custom Tracking Event Dialog
    if (showAddEventDialog) {
        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Ajouter une étape au suivi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newEventTitle,
                        onValueChange = { newEventTitle = it },
                        label = { Text("Titre de l'étape") },
                        placeholder = { Text("Ex: Pris en charge par le livreur") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newEventLocation,
                        onValueChange = { newEventLocation = it },
                        label = { Text("Lieu / Centre de tri") },
                        placeholder = { Text("Ex: Agence Lyon Nord") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newEventDescription,
                        onValueChange = { newEventDescription = it },
                        label = { Text("Description complémentaire") },
                        placeholder = { Text("Ex: Arrivé au centre de distribution.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEventTitle.isNotBlank()) {
                            viewModel.addManualTrackingEvent(
                                packageId = pkg.id,
                                title = newEventTitle,
                                location = newEventLocation,
                                description = newEventDescription,
                                status = pkg.status
                            )
                            newEventTitle = ""
                            newEventLocation = ""
                            newEventDescription = ""
                            showAddEventDialog = false
                        }
                    },
                    enabled = newEventTitle.isNotBlank()
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate500
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate800
            )
        }
    }
}
