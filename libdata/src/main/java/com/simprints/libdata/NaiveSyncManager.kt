package com.simprints.libdata

import com.google.firebase.FirebaseApp
import com.simprints.libcommon.Progress
import com.simprints.libdata.models.realm.RealmConfig
import com.simprints.libdata.tools.Utils
import io.reactivex.Emitter

class NaiveSyncManager(firebaseApp: FirebaseApp,
                       private val apiKey: String) {

    private val db = Utils.getDatabase(firebaseApp)
    private val realmConfig = RealmConfig.get(apiKey)

    fun syncUser(userId: String, isInterrupted: () -> Boolean, emitter: Emitter<Progress>) =
            sync(userId, isInterrupted, emitter)

    fun syncGlobal(isInterrupted: () -> Boolean, emitter: Emitter<Progress>) =
            sync("", isInterrupted, emitter)

    private fun sync(userId: String, isInterrupted: () -> Boolean, emitter: Emitter<Progress>) =
            NaiveSync(isInterrupted,
                    emitter,
                    userId,
                    realmConfig,
                    getProjRef(),
                    getUsersRef(),
                    getPatientsRef()).sync()

    private fun getProjRef() =
            db.getReference("projects/$apiKey")

    private fun getUsersRef() =
            db.getReference("projects/$apiKey/users")

    private fun getPatientsRef() =
            db.getReference("projects/$apiKey/patients")

}