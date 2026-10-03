package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracking_events",
    foreignKeys = [
        ForeignKey(
            entity = PackageEntity::class,
            parentColumns = ["id"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packageId")]
)
data class TrackingEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageId: Long,
    val timestamp: Long,
    val dateFormatted: String,
    val timeFormatted: String,
    val status: PackageStatus,
    val title: String,
    val location: String,
    val description: String
)
