package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Carrier
import com.example.data.model.PackageEntity
import com.example.data.model.PackageStatus
import com.example.data.model.PackageWithEvents
import com.example.data.model.TrackingEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [PackageEntity::class, TrackingEventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun packageDao(): PackageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "colistrack_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.packageDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: PackageDao) {
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val day = 24 * hour

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

            // Parcel 1: Chronopost - Out for delivery
            val pkg1Id = dao.insertPackage(
                PackageEntity(
                    trackingNumber = "FW849201948FR",
                    title = "Casque Audio Sony WH-1000XM5",
                    carrier = Carrier.CHRONOPOST,
                    status = PackageStatus.OUT_FOR_DELIVERY,
                    sender = "Boulanger Logistique",
                    recipient = "Alexandre Martin",
                    destinationAddress = "14 rue de la République, 69002 Lyon",
                    estimatedDeliveryDate = "Aujourd'hui entre 11h30 et 13h30",
                    category = "High-Tech",
                    notes = "Code d'entrée immeuble: B429, 3ème étage droite",
                    weightKg = 0.85,
                    isPinned = true,
                    isArchived = false,
                    createdAt = now - 2 * day,
                    updatedAt = now - 1 * hour
                )
            )

            dao.insertEvents(
                listOf(
                    TrackingEventEntity(
                        packageId = pkg1Id,
                        timestamp = now - 1 * hour,
                        dateFormatted = dateFormat.format(Date(now - 1 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 1 * hour)),
                        status = PackageStatus.OUT_FOR_DELIVERY,
                        title = "En cours de livraison",
                        location = "Agence Chronopost Lyon Sud",
                        description = "Le livreur a chargé votre colis à bord de son véhicule de tournée."
                    ),
                    TrackingEventEntity(
                        packageId = pkg1Id,
                        timestamp = now - 4 * hour,
                        dateFormatted = dateFormat.format(Date(now - 4 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 4 * hour)),
                        status = PackageStatus.IN_TRANSIT,
                        title = "Arrivée agence de distribution",
                        location = "Hub Chronopost Lyon",
                        description = "Colis trié et orienté vers l'agence locale de livraison."
                    ),
                    TrackingEventEntity(
                        packageId = pkg1Id,
                        timestamp = now - 18 * hour,
                        dateFormatted = dateFormat.format(Date(now - 18 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 18 * hour)),
                        status = PackageStatus.IN_TRANSIT,
                        title = "En transit - Centre de tri",
                        location = "Plateforme Chilly-Mazarin (91)",
                        description = "Acheminement inter-régional en cours."
                    ),
                    TrackingEventEntity(
                        packageId = pkg1Id,
                        timestamp = now - 2 * day,
                        dateFormatted = dateFormat.format(Date(now - 2 * day)),
                        timeFormatted = timeFormat.format(Date(now - 2 * day)),
                        status = PackageStatus.INFO_RECEIVED,
                        title = "Prise en charge expéditeur",
                        location = "Boulanger Entrepôt Nord (59)",
                        description = "Colis préparé et remis au réseau Chronopost."
                    )
                )
            )

            // Parcel 2: Colissimo - In Transit
            val pkg2Id = dao.insertPackage(
                PackageEntity(
                    trackingNumber = "6A20491823901",
                    title = "Commande Livres & Disques Vinyles",
                    carrier = Carrier.COLISSIMO,
                    status = PackageStatus.IN_TRANSIT,
                    sender = "Fnac.com",
                    recipient = "Alexandre Martin",
                    destinationAddress = "Boîte aux lettres normalisée",
                    estimatedDeliveryDate = "Demain avant 18h00",
                    category = "Culture",
                    notes = "Colis standard sans signature",
                    weightKg = 1.4,
                    isPinned = false,
                    isArchived = false,
                    createdAt = now - 1 * day,
                    updatedAt = now - 6 * hour
                )
            )

            dao.insertEvents(
                listOf(
                    TrackingEventEntity(
                        packageId = pkg2Id,
                        timestamp = now - 6 * hour,
                        dateFormatted = dateFormat.format(Date(now - 6 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 6 * hour)),
                        status = PackageStatus.IN_TRANSIT,
                        title = "En transit sur le réseau postal",
                        location = "Plateforme Colis Briarde (77)",
                        description = "Votre colis est pris en charge et chemine vers votre bureau distributeur."
                    ),
                    TrackingEventEntity(
                        packageId = pkg2Id,
                        timestamp = now - 26 * hour,
                        dateFormatted = dateFormat.format(Date(now - 26 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 26 * hour)),
                        status = PackageStatus.INFO_RECEIVED,
                        title = "Numéro de suivi généré",
                        location = "Fnac Logistique Massy",
                        description = "Votre colis est en préparation chez l'expéditeur."
                    )
                )
            )

            // Parcel 3: Mondial Relay - Pickup available
            val pkg3Id = dao.insertPackage(
                PackageEntity(
                    trackingNumber = "83910284",
                    title = "Sneakers Running Trail",
                    carrier = Carrier.MONDIAL_RELAY,
                    status = PackageStatus.AVAILABLE_FOR_PICKUP,
                    sender = "Vendeur Vinted (Thomas D.)",
                    recipient = "Alexandre Martin",
                    destinationAddress = "Point Relais Tabac Presse Saint-Jean, 12 rue Saint-Jean, Lyon",
                    estimatedDeliveryDate = "Disponible jusqu'au 15 Octobre",
                    category = "Mode & Sport",
                    notes = "Code PIN de retrait reçu par email/SMS",
                    weightKg = 0.95,
                    isPinned = true,
                    isArchived = false,
                    createdAt = now - 3 * day,
                    updatedAt = now - 2 * hour
                )
            )

            dao.insertEvents(
                listOf(
                    TrackingEventEntity(
                        packageId = pkg3Id,
                        timestamp = now - 2 * hour,
                        dateFormatted = dateFormat.format(Date(now - 2 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 2 * hour)),
                        status = PackageStatus.AVAILABLE_FOR_PICKUP,
                        title = "Disponible au Point Relais",
                        location = "Tabac Presse Saint-Jean (Lyon)",
                        description = "Votre colis est disponible. Présentez une pièce d'identité pour le retirer (gardé 8 jours)."
                    ),
                    TrackingEventEntity(
                        packageId = pkg3Id,
                        timestamp = now - 8 * hour,
                        dateFormatted = dateFormat.format(Date(now - 8 * hour)),
                        timeFormatted = timeFormat.format(Date(now - 8 * hour)),
                        status = PackageStatus.OUT_FOR_DELIVERY,
                        title = "En cours d'acheminement vers le relais",
                        location = "Agence Mondial Relay Rhône",
                        description = "Votre colis est dans la navette de distribution locale."
                    ),
                    TrackingEventEntity(
                        packageId = pkg3Id,
                        timestamp = now - 2 * day,
                        dateFormatted = dateFormat.format(Date(now - 2 * day)),
                        timeFormatted = timeFormat.format(Date(now - 2 * day)),
                        status = PackageStatus.IN_TRANSIT,
                        title = "Prise en charge Hub Mondial Relay",
                        location = "Hub régional Hem (59)",
                        description = "Colis enregistré sur le réseau de tri."
                    )
                )
            )

            // Parcel 4: Amazon - Delivered
            val pkg4Id = dao.insertPackage(
                PackageEntity(
                    trackingNumber = "TBA93817290123",
                    title = "Chargeur Rapide GaN 65W & Câbles USB-C",
                    carrier = Carrier.AMAZON,
                    status = PackageStatus.DELIVERED,
                    sender = "Amazon EU SARL",
                    recipient = "Alexandre Martin",
                    destinationAddress = "Boîte aux lettres",
                    estimatedDeliveryDate = "Hier avant 20h00",
                    category = "Accessoires",
                    notes = "Livré sans contact dans la boîte aux lettres",
                    weightKg = 0.32,
                    isPinned = false,
                    isArchived = false,
                    createdAt = now - 4 * day,
                    updatedAt = now - 1 * day
                )
            )

            dao.insertEvents(
                listOf(
                    TrackingEventEntity(
                        packageId = pkg4Id,
                        timestamp = now - 1 * day,
                        dateFormatted = dateFormat.format(Date(now - 1 * day)),
                        timeFormatted = "15:42",
                        status = PackageStatus.DELIVERED,
                        title = "Colis livré",
                        location = "Lyon (Boîte aux lettres)",
                        description = "Votre colis a été déposé dans votre boîte aux lettres par le livreur Amazon."
                    ),
                    TrackingEventEntity(
                        packageId = pkg4Id,
                        timestamp = now - 1 * day - 6 * hour,
                        dateFormatted = dateFormat.format(Date(now - 1 * day)),
                        timeFormatted = "09:15",
                        status = PackageStatus.OUT_FOR_DELIVERY,
                        title = "En cours de livraison",
                        location = "Centre de distribution Amazon Satolas",
                        description = "Colis en cours de livraison par le chauffeur."
                    ),
                    TrackingEventEntity(
                        packageId = pkg4Id,
                        timestamp = now - 2 * day,
                        dateFormatted = dateFormat.format(Date(now - 2 * day)),
                        timeFormatted = "18:20",
                        status = PackageStatus.IN_TRANSIT,
                        title = "En transit",
                        location = "Centre logistique Amazon Saran",
                        description = "Le colis a quitté le centre logistique."
                    )
                )
            )
        }
    }
}
