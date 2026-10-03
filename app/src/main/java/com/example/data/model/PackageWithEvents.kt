package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PackageWithEvents(
    @Embedded
    val packageItem: PackageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "packageId"
    )
    val events: List<TrackingEventEntity>
) {
    val sortedEvents: List<TrackingEventEntity>
        get() = events.sortedByDescending { it.timestamp }

    val latestEvent: TrackingEventEntity?
        get() = sortedEvents.firstOrNull()
}
