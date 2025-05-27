package com.simprints.face.infra.simfacewrapper.matching

import com.simprints.face.infra.basebiosdk.matching.FaceMatcher
import com.simprints.simface.core.SimFace
import javax.inject.Inject

class SimFaceMatcher @Inject constructor(
    private val simFace: SimFace,
) : FaceMatcher() {
    override val matcherName: String
        get() = "SIM_FACE"

    override val supportedTemplateFormat: String
        get() = simFace.getTemplateVersion()

    override suspend fun getComparisonScore(
        probe: ByteArray,
        matchAgainst: ByteArray,
    ): Float {
        // SDK returns score in [0, 1] range, SID expects [0, 100]
        val baseScore = simFace.verificationScore(probe, matchAgainst).toFloat()
        // TODO: remove the random adjustment after we find out why the returned range is always [0.5;1]
        return (baseScore - 0.5).coerceAtLeast(0.0).toFloat() * 200f
    }
}
