package com.simprints.id.data.db.event.local

import android.content.Context
import androidx.room.Room
import com.simprints.id.data.secure.SecureLocalDbKeyProvider
import net.sqlcipher.database.SQLiteDatabase.getBytes
import net.sqlcipher.database.SupportFactory
import timber.log.Timber

interface EventDatabaseFactory {
    fun build(): EventRoomDatabase
}

@OptIn(ExperimentalStdlibApi::class)
class DbEventDatabaseFactoryImpl(
    val ctx: Context,
    private val secureLocalDbKeyProvider: SecureLocalDbKeyProvider
) : EventDatabaseFactory {

    override fun build(): EventRoomDatabase {
        try {
            val key = getOrCreateKey(DB_NAME)
            //val key = "test".toCharArray() //Use com.amitshekhar.android:debug-db

            val passphrase: ByteArray = getBytes(key)
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(ctx, EventRoomDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .build()
        } catch (t: Throwable) {
            Timber.e(t)
            throw t
        }
    }

    private fun getOrCreateKey(dbName: String): CharArray {
        return try {
            secureLocalDbKeyProvider.getLocalDbKeyOrThrow(dbName)
        } catch (t: Throwable) {
            Timber.d(t.message)
            secureLocalDbKeyProvider.setLocalDatabaseKey(dbName)
            secureLocalDbKeyProvider.getLocalDbKeyOrThrow(dbName)
        }.value.decodeToString().toCharArray()
    }

    companion object {
        private const val DB_NAME = "dbevents"
    }
}