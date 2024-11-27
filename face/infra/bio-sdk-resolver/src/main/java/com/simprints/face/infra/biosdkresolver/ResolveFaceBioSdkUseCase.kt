package com.simprints.face.infra.biosdkresolver

import com.simprints.infra.config.store.ConfigRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveFaceBioSdkUseCase @Inject constructor(
    private val rocV1BioSdk: RocV1BioSdk,
) {

    suspend operator fun invoke(): FaceBioSDK =
        rocV1BioSdk

}
