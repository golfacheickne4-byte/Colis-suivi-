package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Carrier
import com.example.data.model.PackageEntity
import com.example.data.model.PackageStatus
import com.example.ui.components.BarcodeCanvas
import com.example.ui.components.CarrierBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PackageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPackageScreen(
    viewModel: PackageViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateToHome()
    }

    val packageToEdit by viewModel.packageToEdit.collectAsState()
    val isEditing = packageToEdit != null

    var trackingNumber by remember { mutableStateOf(packageToEdit?.trackingNumber ?: "") }
    var title by remember { mutableStateOf(packageToEdit?.title ?: "") }
    var selectedCarrier by remember { mutableStateOf(packageToEdit?.carrier ?: Carrier.COLISSIMO) }
    var selectedStatus by remember { mutableStateOf(packageToEdit?.status ?: PackageStatus.IN_TRANSIT) }
    var sender by remember { mutableStateOf(packageToEdit?.sender ?: "") }
    var recipient by remember { mutableStateOf(packageToEdit?.recipient ?: "") }
    var destinationAddress by remember { mutableStateOf(packageToEdit?.destinationAddress ?: "") }
    var estimatedDeliveryDate by remember { mutableStateOf(packageToEdit?.estimatedDeliveryDate ?: "Dans 2 à 3 jours") }
    var selectedCategory by remember { mutableStateOf(packageToEdit?.category ?: "High-Tech") }
    var notes by remember { mutableStateOf(packageToEdit?.notes ?: "") }
    var weightText by remember { mutableStateOf(if ((packageToEdit?.weightKg ?: 0.0) > 0) packageToEdit?.weightKg.toString() else "") }

    val categories = listOf("High-Tech", "Mode & Sport", "Culture", "Accessoires", "Maison", "Cadeau", "Général")
    val datePresets = listOf("Aujourd'hui", "Demain", "Sous 48h", "Cette semaine", "En cours de confirmation")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Modifier le colis" else "Ajouter un colis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateToHome() },
                        modifier = Modifier.testTag("add_edit_back_button")
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
            // Tracking Number Card
            GlassCard(
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
                        text = "Numéro d'expédition",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = trackingNumber,
                        onValueChange = { input ->
                            trackingNumber = input
                            // Auto-detect carrier when user types
                            if (!isEditing && input.length >= 2) {
                                val detected = Carrier.detectFromTrackingNumber(input)
                                if (detected != Carrier.AUTRE) {
                                    selectedCarrier = detected
                                }
                            }
                        },
                        label = { Text("Numéro de suivi (ex: 6A..., FW..., 1Z...)") },
                        placeholder = { Text("6A928172901FR") },
                        leadingIcon = {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Slate400)
                        },
                        trailingIcon = {
                            if (!isEditing && trackingNumber.isNotBlank()) {
                                Surface(
                                    color = Slate100,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AutoFixHigh,
                                            contentDescription = null,
                                            tint = BlueAccent,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            "Auto: ${selectedCarrier.shortName}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BlueAccent
                                        )
                                    }
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tracking_number_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Barcode Preview
                    if (trackingNumber.isNotBlank()) {
                        BarcodeCanvas(
                            code = trackingNumber,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Carrier Selection Card
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                tint = Color.White.copy(alpha = 0.82f),
                elevation = 2.dp,
                borderAlpha = 0.8f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Transporteur",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Carrier.values().forEach { carrier ->
                            val isSelected = selectedCarrier == carrier
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCarrier = carrier },
                                label = {
                                    CarrierBadge(carrier = carrier, useShortName = true)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueAccent.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Package Details Card
            GlassCard(
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
                        text = "Détails du colis",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Nom du colis / Commande *") },
                        placeholder = { Text("Ex: iPhone 16 Pro, Chaussures Nike...") },
                        leadingIcon = {
                            Icon(Icons.Default.Title, contentDescription = null, tint = Slate400)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("package_title_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category selection
                    Text(
                        text = "Catégorie",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueAccent.copy(alpha = 0.15f),
                                    selectedLabelColor = BlueAccent
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = sender,
                        onValueChange = { sender = it },
                        label = { Text("Expéditeur / Marchand") },
                        placeholder = { Text("Ex: Fnac, Amazon, Vinted, Zara...") },
                        leadingIcon = {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Slate400)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = destinationAddress,
                        onValueChange = { destinationAddress = it },
                        label = { Text("Destination / Point relais") },
                        placeholder = { Text("Ex: Domicile Lyon ou Point Relais Tabac Presse") },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Slate400)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it },
                        label = { Text("Destinataire") },
                        placeholder = { Text("Votre nom ou destinataire du cadeau") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Slate400)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Delivery Date & Status Card
            GlassCard(
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
                        text = "Statut et estimation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = estimatedDeliveryDate,
                        onValueChange = { estimatedDeliveryDate = it },
                        label = { Text("Date de livraison estimée") },
                        placeholder = { Text("Ex: Demain avant 13h, 5 Octobre 2026") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate400)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        datePresets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate100,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 11.sp,
                                    color = BlueAccent,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("date_preset_$preset")
                                )
                            }
                        }
                    }

                    Text(
                        text = "Statut actuel de départ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PackageStatus.values().forEach { status ->
                            val isSelected = selectedStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStatus = status },
                                label = { Text(status.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(status.colorHex).copy(alpha = 0.15f),
                                    selectedLabelColor = Color(status.colorHex)
                                )
                            )
                        }
                    }
                }
            }

            // Notes and Weight Card
            GlassCard(
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
                        text = "Informations complémentaires",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Poids du colis (kg)") },
                        placeholder = { Text("Ex: 1.2") },
                        leadingIcon = {
                            Icon(Icons.Default.Scale, contentDescription = null, tint = Slate400)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes personnelles (code digicode, instructions...)") },
                        placeholder = { Text("Ex: Code porte 1234, laisser au gardien si absent...") },
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = Slate400)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    val finalTrackingNumber = trackingNumber.trim().ifBlank {
                        "COLIS-${System.currentTimeMillis().toString().takeLast(6)}"
                    }
                    val finalTitle = title.trim().ifBlank {
                        "Colis ${selectedCarrier.shortName}"
                    }
                    val weight = weightText.replace(",", ".").toDoubleOrNull() ?: 0.0

                    val packageEntity = PackageEntity(
                        id = packageToEdit?.id ?: 0L,
                        trackingNumber = finalTrackingNumber,
                        title = finalTitle,
                        carrier = selectedCarrier,
                        status = selectedStatus,
                        sender = sender.trim(),
                        recipient = recipient.trim(),
                        destinationAddress = destinationAddress.trim(),
                        estimatedDeliveryDate = estimatedDeliveryDate.trim(),
                        category = selectedCategory,
                        notes = notes.trim(),
                        weightKg = weight,
                        isPinned = packageToEdit?.isPinned ?: false,
                        isArchived = packageToEdit?.isArchived ?: false,
                        createdAt = packageToEdit?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    viewModel.savePackage(packageEntity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_package_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Mettre à jour le colis" else "Enregistrer et suivre le colis",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
