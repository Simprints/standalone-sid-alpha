package com.simprints.face.infra.biosdkresolver

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveFaceBioSdkUseCase @Inject constructor(
    private val simFaceBioSdk: SimFaceBioSdk,
) {
    suspend operator fun invoke(): FaceBioSDK = simFaceBioSdk
}
