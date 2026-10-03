package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.PackageEntity
import com.example.data.model.PackageStatus
import com.example.data.model.PackageWithEvents
import com.example.data.model.TrackingEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {

    @Transaction
    @Query("SELECT * FROM packages WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getActivePackagesWithEvents(): Flow<List<PackageWithEvents>>

    @Transaction
    @Query("SELECT * FROM packages WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedPackagesWithEvents(): Flow<List<PackageWithEvents>>

    @Transaction
    @Query("SELECT * FROM packages ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllPackagesWithEvents(): Flow<List<PackageWithEvents>>

    @Transaction
    @Query("SELECT * FROM packages WHERE id = :id LIMIT 1")
    fun getPackageWithEventsById(id: Long): Flow<PackageWithEvents?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: PackageEntity): Long

    @Update
    suspend fun updatePackage(pkg: PackageEntity)

    @Query("DELETE FROM packages WHERE id = :id")
    suspend fun deletePackageById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<TrackingEventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TrackingEventEntity): Long

    @Query("UPDATE packages SET isPinned = :isPinned WHERE id = :packageId")
    suspend fun setPinned(packageId: Long, isPinned: Boolean)

    @Query("UPDATE packages SET isArchived = :isArchived WHERE id = :packageId")
    suspend fun setArchived(packageId: Long, isArchived: Boolean)

    @Query("UPDATE packages SET status = :status, updatedAt = :updatedAt WHERE id = :packageId")
    suspend fun updatePackageStatus(packageId: Long, status: PackageStatus, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM packages")
    suspend fun getPackageCount(): Int
}
