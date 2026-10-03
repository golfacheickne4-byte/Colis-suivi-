package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.PackageDao
import com.example.data.model.Carrier
import com.example.data.model.PackageEntity
import com.example.data.model.PackageStatus
import com.example.data.model.PackageWithEvents
import com.example.data.model.TrackingEventEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PackageRepository(private val packageDao: PackageDao) {

    val activePackages: Flow<List<PackageWithEvents>> = packageDao.getActivePackagesWithEvents()
    val archivedPackages: Flow<List<PackageWithEvents>> = packageDao.getArchivedPackagesWithEvents()
    val allPackages: Flow<List<PackageWithEvents>> = packageDao.getAllPackagesWithEvents()

    fun getPackageById(id: Long): Flow<PackageWithEvents?> = packageDao.getPackageWithEventsById(id)

    suspend fun addPackage(
        packageEntity: PackageEntity,
        initialEvents: List<TrackingEventEntity> = emptyList()
    ): Long {
        val id = packageDao.insertPackage(packageEntity)
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

        val eventsToInsert = if (initialEvents.isEmpty()) {
            listOf(
                TrackingEventEntity(
                    packageId = id,
                    timestamp = now,
                    dateFormatted = dateFormat.format(Date(now)),
                    timeFormatted = timeFormat.format(Date(now)),
                    status = packageEntity.status,
                    title = when (packageEntity.status) {
                        PackageStatus.ORDER_CONFIRMED -> "Commande validée"
                        PackageStatus.INFO_RECEIVED -> "Étiquette de transport générée"
                        PackageStatus.IN_TRANSIT -> "Prise en charge par le transporteur"
                        PackageStatus.OUT_FOR_DELIVERY -> "En cours de livraison"
                        PackageStatus.AVAILABLE_FOR_PICKUP -> "Disponible au point de retrait"
                        PackageStatus.DELIVERED -> "Colis livré avec succès"
                        PackageStatus.EXCEPTION -> "Incident signalé"
                    },
                    location = if (packageEntity.sender.isNotBlank()) packageEntity.sender else "Centre de tri initial",
                    description = packageEntity.status.description
                )
            )
        } else {
            initialEvents.map { it.copy(packageId = id) }
        }

        packageDao.insertEvents(eventsToInsert)
        return id
    }

    suspend fun updatePackage(packageEntity: PackageEntity) {
        packageDao.updatePackage(packageEntity.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deletePackage(id: Long) {
        packageDao.deletePackageById(id)
    }

    suspend fun setPinned(id: Long, isPinned: Boolean) {
        packageDao.setPinned(id, isPinned)
    }

    suspend fun setArchived(id: Long, isArchived: Boolean) {
        packageDao.setArchived(id, isArchived)
    }

    suspend fun addTrackingEvent(
        packageId: Long,
        title: String,
        location: String,
        description: String,
        status: PackageStatus
    ) {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

        packageDao.insertEvent(
            TrackingEventEntity(
                packageId = packageId,
                timestamp = now,
                dateFormatted = dateFormat.format(Date(now)),
                timeFormatted = timeFormat.format(Date(now)),
                status = status,
                title = title,
                location = location,
                description = description
            )
        )
        packageDao.updatePackageStatus(packageId, status, now)
    }

    suspend fun simulateNextStep(pkgWithEvents: PackageWithEvents): Boolean {
        val pkg = pkgWithEvents.packageItem
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

        val nextStatus = when (pkg.status) {
            PackageStatus.ORDER_CONFIRMED -> PackageStatus.INFO_RECEIVED
            PackageStatus.INFO_RECEIVED -> PackageStatus.IN_TRANSIT
            PackageStatus.IN_TRANSIT -> {
                if (pkg.carrier == Carrier.MONDIAL_RELAY || pkg.carrier == Carrier.RELAIS_COLIS) {
                    PackageStatus.AVAILABLE_FOR_PICKUP
                } else {
                    PackageStatus.OUT_FOR_DELIVERY
                }
            }
            PackageStatus.OUT_FOR_DELIVERY, PackageStatus.AVAILABLE_FOR_PICKUP -> PackageStatus.DELIVERED
            PackageStatus.DELIVERED -> return false // already delivered
            PackageStatus.EXCEPTION -> PackageStatus.IN_TRANSIT
        }

        val eventTitle = when (nextStatus) {
            PackageStatus.INFO_RECEIVED -> "Étiquette d'expédition prête"
            PackageStatus.IN_TRANSIT -> "Pris en charge sur le réseau ${pkg.carrier.shortName}"
            PackageStatus.OUT_FOR_DELIVERY -> "Colis confié au livreur pour distribution"
            PackageStatus.AVAILABLE_FOR_PICKUP -> "Arrivé au point relais de retrait"
            PackageStatus.DELIVERED -> "Colis livré et réceptionné"
            else -> "Mise à jour d'acheminement"
        }

        val location = when (nextStatus) {
            PackageStatus.INFO_RECEIVED -> "Plateforme logistique ${pkg.carrier.shortName}"
            PackageStatus.IN_TRANSIT -> "Hub régional de transit"
            PackageStatus.OUT_FOR_DELIVERY -> "Tournée de livraison locale"
            PackageStatus.AVAILABLE_FOR_PICKUP -> pkg.destinationAddress.ifBlank { "Point Relais partenaire" }
            PackageStatus.DELIVERED -> pkg.destinationAddress.ifBlank { "Adresse du destinataire" }
            else -> "Réseau de transport"
        }

        packageDao.insertEvent(
            TrackingEventEntity(
                packageId = pkg.id,
                timestamp = now,
                dateFormatted = dateFormat.format(Date(now)),
                timeFormatted = timeFormat.format(Date(now)),
                status = nextStatus,
                title = eventTitle,
                location = location,
                description = nextStatus.description
            )
        )
        packageDao.updatePackageStatus(pkg.id, nextStatus, now)
        return true
    }

    suspend fun resetWithSampleData() {
        AppDatabase.populateInitialData(packageDao)
    }

    suspend fun checkAndSeedInitialData() {
        if (packageDao.getPackageCount() == 0) {
            AppDatabase.populateInitialData(packageDao)
        }
    }
}
