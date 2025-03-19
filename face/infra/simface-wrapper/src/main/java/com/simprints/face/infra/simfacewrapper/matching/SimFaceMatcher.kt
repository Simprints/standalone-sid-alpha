package com.simprints.face.infra.simfacewrapper.matching

import com.simprints.biometrics.polyprotect.computeSimilarityScore
import com.simprints.face.infra.basebiosdk.matching.FaceMatcher
import com.simprints.face.infra.simfacewrapper.detection.SimFaceDetector
import javax.inject.Inject

class SimFaceMatcher @Inject constructor() : FaceMatcher() {
    override val matcherName: String
        get() = "SIM_FACE"

    override val supportedTemplateFormat: String
        get() = SimFaceDetector.SIM_FACE_TEMPLATE

    override suspend fun getComparisonScore(
        probe: ByteArray,
        matchAgainst: ByteArray,
    ): Float {
        // SDK returns score in [0, 1] range, SID expects [0, 100]
        return computeSimilarityScore(probe, matchAgainst).toFloat() * 100f
    }
}
