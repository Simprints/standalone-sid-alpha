package com.simprints.id.testtools.state

import com.simprints.id.data.secure.keystore.KeystoreManager
import com.simprints.testtools.common.syntax.anyNotNull
import com.simprints.testtools.common.syntax.whenever
import org.mockito.Mockito
import org.mockito.stubbing.Answer

fun setupFakeKeyStore(): KeystoreManager = Mockito.mock(KeystoreManager::class.java).also { keystoreManager ->
    val encryptAnswer = Answer<String> {
        "enc_" + it.arguments[0] as String
    }
    whenever { keystoreManager.encryptString(anyNotNull()) } thenAnswer encryptAnswer

    val decryptAnswer = Answer<String> {
        (it.arguments[0] as String).replace("enc_", "")
    }
    whenever { keystoreManager.decryptString(anyNotNull()) } thenAnswer decryptAnswer

}