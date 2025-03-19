package com.simprints.infra.templateprotection.database

import android.content.Context
import com.simprints.infra.logging.Simber
import com.simprints.infra.security.SecurityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuxDataDatabaseFactory @Inject constructor(
    @ApplicationContext val ctx: Context,
    private val securityManager: SecurityManager,
) {
    private lateinit var auxDataDatabase: AuxDataRoomDatabase

    fun get(): AuxDataRoomDatabase {
        if (!::auxDataDatabase.isInitialized) {
            build()
        }
        return auxDataDatabase
    }

    private fun build() {
        try {
            val key = getOrCreateKey(DB_NAME)
            val passphrase: ByteArray = SQLiteDatabase.getBytes(key)
            val factory = SupportFactory(passphrase)
            auxDataDatabase = AuxDataRoomDatabase.getDatabase(
                ctx,
                factory,
                DB_NAME,
            )
        } catch (t: Throwable) {
            Simber.e("Failed to create event database", t)
            throw t
        }
    }

    private fun getOrCreateKey(
        @Suppress("SameParameterValue") dbName: String,
    ): CharArray = try {
        securityManager.getLocalDbKeyOrThrow(dbName)
    } catch (t: Throwable) {
        t.message?.let { Simber.d(it) }
        securityManager.createLocalDatabaseKeyIfMissing(dbName)
        securityManager.getLocalDbKeyOrThrow(dbName)
    }.value.decodeToString().toCharArray()

    companion object {
        private const val DB_NAME = "dbaux"
    }
}
