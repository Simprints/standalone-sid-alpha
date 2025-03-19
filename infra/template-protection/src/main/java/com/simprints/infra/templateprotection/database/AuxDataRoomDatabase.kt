package com.simprints.infra.templateprotection.database

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simprints.infra.templateprotection.BuildConfig
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [DbAuxData::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(ByteArrayConverters::class)
@Keep
internal abstract class AuxDataRoomDatabase : RoomDatabase() {
    abstract val auxDataDao: AuxDataDao

    companion object {
        fun getDatabase(
            context: Context,
            factory: SupportFactory,
            dbName: String,
        ): AuxDataRoomDatabase {
            val builder = Room.databaseBuilder(context, AuxDataRoomDatabase::class.java, dbName)

            if (BuildConfig.DB_ENCRYPTION) {
                builder.openHelperFactory(factory)
            }

            return builder.build()
        }
    }
}
