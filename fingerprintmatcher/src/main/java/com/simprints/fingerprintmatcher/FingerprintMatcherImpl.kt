package com.simprints.fingerprintmatcher

import com.simprints.fingerprintmatcher.algorithms.simafis.SimAfisMatcher
import com.simprints.fingerprintmatcher.domain.FingerprintIdentity
import com.simprints.fingerprintmatcher.domain.MatchResult
import com.simprints.fingerprintmatcher.domain.MatchingAlgorithm
import kotlinx.coroutines.flow.Flow

internal class FingerprintMatcherImpl(
    private val simAfisMatcher: SimAfisMatcher
) : FingerprintMatcher {

    override suspend fun match(
        probe: FingerprintIdentity,
        candidates: Flow<FingerprintIdentity>,
        matchingAlgorithm: MatchingAlgorithm
    ): Flow<MatchResult> =
        when (matchingAlgorithm) {
            MatchingAlgorithm.SIM_AFIS -> simAfisMatch(probe, candidates)
        }

    private suspend fun simAfisMatch(probe: FingerprintIdentity, candidates: Flow<FingerprintIdentity>): Flow<MatchResult> {
        return simAfisMatcher.match(probe, candidates)
    }
}