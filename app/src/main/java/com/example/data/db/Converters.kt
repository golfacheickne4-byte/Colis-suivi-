package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.Carrier
import com.example.data.model.PackageStatus

class Converters {
    @TypeConverter
    fun fromCarrier(carrier: Carrier?): String? = carrier?.name

    @TypeConverter
    fun toCarrier(value: String?): Carrier =
        value?.let {
            try {
                Carrier.valueOf(it)
            } catch (_: Exception) {
                Carrier.AUTRE
            }
        } ?: Carrier.AUTRE

    @TypeConverter
    fun fromStatus(status: PackageStatus?): String? = status?.name

    @TypeConverter
    fun toStatus(value: String?): PackageStatus =
        value?.let {
            try {
                PackageStatus.valueOf(it)
            } catch (_: Exception) {
                PackageStatus.INFO_RECEIVED
            }
        } ?: PackageStatus.INFO_RECEIVED
}
