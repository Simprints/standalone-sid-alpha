package com.simprints.face.infra.basebiosdk.matching

abstract class FaceMatcher {
    /**
     * The matching SDK name
     */
    abstract val matcherName: String
    abstract val supportedTemplateFormat: String

    /**
     * Returns a comparison score of two templates  from 0.0 - 100.0
     */
    abstract suspend fun getComparisonScore(
        probe: ByteArray,
        matchAgainst: ByteArray,
    ): Float

    /**
     * Get highest comparison score for matching candidate template against all probes
     *
     * @param probes
     * @param candidate
     * @return the highest comparison score
     */
    suspend fun getHighestComparisonScoreForCandidate(
        probes: List<FaceSample>,
        candidate: FaceIdentity,
    ): Float = probes
        .flatMap { probe ->
            candidate.faces.map { face -> getComparisonScore(probe.template, face.template) }
        }.max()
}
