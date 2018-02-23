package com.simprints.id.data.db.remote

import com.google.firebase.auth.FirebaseAuth
import com.simprints.libdata.AuthListener


interface RemoteDbAuthListenerManager {

    var isSignedIn: Boolean

    fun registerRemoteAuthListener(authListener: AuthListener)
    fun unregisterRemoteAuthListener(authListener: AuthListener)
    fun applyAuthListeners(firebaseAuth: FirebaseAuth, appName: String)
    fun removeAuthListeners(firebaseAuth: FirebaseAuth)
}