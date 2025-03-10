package com.simprints.face.infra.biosdkresolver

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveFaceBioSdkUseCase @Inject constructor(
    private val faceNetBioSdk: FaceNetBioSdk,
) {
    suspend operator fun invoke(): FaceBioSDK = faceNetBioSdk
}
