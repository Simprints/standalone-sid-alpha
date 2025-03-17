package com.simprints.face.infra.biosdkresolver

import com.simprints.face.infra.simfacewrapper.detection.SimFaceDetector
import com.simprints.face.infra.simfacewrapper.initialization.SimFaceInitializer
import com.simprints.face.infra.simfacewrapper.matching.SimFaceMatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimFaceBioSdk @Inject constructor(
    override val initializer: SimFaceInitializer,
    override val detector: SimFaceDetector,
    override val matcher: SimFaceMatcher,
) : FaceBioSDK {
    override val version: String = "1"
}
