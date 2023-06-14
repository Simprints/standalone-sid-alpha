package com.simprints.infra.authlogic.authenticator.remote.models

import androidx.annotation.Keep
import com.simprints.infra.authstore.domain.models.AuthRequest

@Keep
internal data class ApiAuthRequestBody(
    var encryptedProjectSecret: String = "",
    var integrityToken: String = "",
    var deviceId: String
) {
    companion object {
        fun fromDomain(authRequest: AuthRequest): ApiAuthRequestBody =
            ApiAuthRequestBody(
                authRequest.encryptedProjectSecret,
                authRequest.integrityToken,
                authRequest.deviceId,
            )
    }
}