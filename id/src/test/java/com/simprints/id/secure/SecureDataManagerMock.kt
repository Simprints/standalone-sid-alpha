package com.simprints.id.secure

import com.simprints.id.data.prefs.improvedSharedPreferences.ImprovedSharedPreferences
import com.simprints.id.data.secure.SecureDataManager

class SecureDataManagerMock : SecureDataManager {

    override fun getSignedInProjectIdOrEmpty(): String {
        throw RuntimeException("Not mocked!!!")
    }

    override fun isProjectIdSignedIn(projectId: String): Boolean {
        throw RuntimeException("Not mocked!!!")
    }

    override fun cleanCredentials() {
        throw RuntimeException("Not mocked!!!")
    }

    override var encryptedProjectSecret: String
        get() = "encrypted_project_secret"
        set(value) {}

    override var signedInProjectId: String
        get() = "project_id"
        set(value) {}

    override var prefs: ImprovedSharedPreferences
        get() = throw RuntimeException("Not mocked!!!")
        set(value) {}

    override fun getEncryptedProjectSecretOrEmpty(): String {
        throw RuntimeException("Not mocked!!!")
    }
}