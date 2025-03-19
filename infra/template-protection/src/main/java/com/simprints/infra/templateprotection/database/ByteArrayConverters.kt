package com.simprints.infra.templateprotection.database

import androidx.room.TypeConverter

internal class ByteArrayConverters {
    @TypeConverter
    fun fromByteArrayToString(type: ByteArray?): String? = type?.joinToString(DELIMITER)

    @TypeConverter
    fun fromStringToByteArray(items: String?): ByteArray? = items
        ?.split(DELIMITER)
        ?.map { it.toByte() }
        ?.toByteArray()

    companion object {
        private const val DELIMITER = "|"
    }
}
