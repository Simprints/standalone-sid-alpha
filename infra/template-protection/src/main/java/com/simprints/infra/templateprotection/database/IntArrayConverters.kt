package com.simprints.infra.templateprotection.database

import androidx.room.TypeConverter

internal class IntArrayConverters {
    @TypeConverter
    fun fromIntArrayToString(type: IntArray?): String? = type?.joinToString(DELIMITER)

    @TypeConverter
    fun fromStringToIntArray(items: String?): IntArray? = items
        ?.split(DELIMITER)
        ?.map { it.toInt() }
        ?.toIntArray()

    companion object {
        private const val DELIMITER = "|"
    }
}
