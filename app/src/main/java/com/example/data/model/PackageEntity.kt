package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingNumber: String,
    val title: String,
    val carrier: Carrier,
    val status: PackageStatus,
    val sender: String = "",
    val recipient: String = "",
    val destinationAddress: String = "",
    val estimatedDeliveryDate: String = "",
    val category: String = "Général",
    val notes: String = "",
    val weightKg: Double = 0.0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
