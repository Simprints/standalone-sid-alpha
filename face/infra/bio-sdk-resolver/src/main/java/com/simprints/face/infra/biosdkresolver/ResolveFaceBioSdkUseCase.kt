package com.simprints.face.infra.biosdkresolver

import com.simprints.infra.config.store.ConfigRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveFaceBioSdkUseCase @Inject constructor(
    private val rocV1BioSdk: RocV1BioSdk,
) {
    suspend operator fun invoke(): FaceBioSDK {
        val version = configRepository
            .getProjectConfiguration()
            .face
            ?.rankOne
            ?.version
            ?.takeIf { it.isNotBlank() } // Ensures version is not null or empty
        requireNotNull(version) { "FaceBioSDK version is null or empty" }
        return if (version == rocV3BioSdk.version) rocV3BioSdk else rocV1BioSdk
    }

    suspend operator fun invoke(): FaceBioSDK =
        rocV1BioSdk

}
