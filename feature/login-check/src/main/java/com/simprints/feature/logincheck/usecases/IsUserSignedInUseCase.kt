package com.simprints.feature.logincheck.usecases

import com.simprints.infra.authstore.AuthStore
import com.simprints.infra.orchestration.data.ActionRequest
import com.simprints.infra.security.SecurityManager
import javax.inject.Inject

internal class IsUserSignedInUseCase @Inject constructor(
    private val authStore: AuthStore,
    private val secureDataManager: SecurityManager,
) {
    operator fun invoke(action: ActionRequest): SignedInState {
       // Always return SIGNED_IN for now to avoid login screen

        return SignedInState.SIGNED_IN
    }

    enum class SignedInState {
        SIGNED_IN,
        NOT_SIGNED_IN,
        MISMATCHED_PROJECT_ID,
    }
}
