package com.simprints.id.orchestrator.responsebuilders

import com.simprints.id.domain.modality.Modality
import com.simprints.id.domain.moduleapi.app.requests.AppRequest
import com.simprints.id.domain.moduleapi.app.responses.AppIdentifyResponse
import com.simprints.id.domain.moduleapi.app.responses.AppResponse
import com.simprints.id.domain.moduleapi.app.responses.entities.MatchResult
import com.simprints.id.domain.moduleapi.app.responses.entities.Tier
import com.simprints.id.domain.moduleapi.face.responses.FaceMatchResponse
import com.simprints.id.domain.moduleapi.face.responses.entities.FaceMatchResult
import com.simprints.id.domain.moduleapi.fingerprint.responses.FingerprintMatchResponse
import com.simprints.id.orchestrator.steps.Step

class AppResponseBuilderForIdentify : BaseAppResponseBuilder() {

    override suspend fun buildAppResponse(modalities: List<Modality>,
                                          appRequest: AppRequest,
                                          steps: List<Step>,
                                          sessionId: String): AppResponse {
        super.getErrorOrRefusalResponseIfAny(steps)?.let {
            return it
        }

        val results = steps.map { it.getResult() }
        val faceResponse = getFaceResponseForIdentify(results)
        val fingerprintResponse = getFingerprintResponseForMatching(results)

        return when {
            fingerprintResponse != null && faceResponse != null -> {
                buildAppIdentifyResponseForFaceAndFinger(faceResponse, fingerprintResponse, sessionId)
            }
            fingerprintResponse != null -> {
                buildAppIdentifyResponseForFingerprint(fingerprintResponse, sessionId)
            }
            faceResponse != null -> {
                buildAppIdentifyResponseForFace(faceResponse, sessionId)
            }
            else -> throw Throwable("All responses are null")
        }
    }

    private fun getFaceResponseForIdentify(results: List<Step.Result?>): FaceMatchResponse? =
        results.filterIsInstance(FaceMatchResponse::class.java).lastOrNull()

    private fun getFingerprintResponseForMatching(results: List<Step.Result?>): FingerprintMatchResponse? =
        results.filterIsInstance(FingerprintMatchResponse::class.java).lastOrNull()

    private fun buildAppIdentifyResponseForFaceAndFinger(faceResponse: FaceMatchResponse,
                                                         fingerprintResponse: FingerprintMatchResponse,
                                                         sessionId: String) =
        AppIdentifyResponse(
            getSortedIdentificationsForFingerprint(fingerprintResponse),
            sessionId)

    private fun getSortedIdentificationsForFingerprint(fingerprintResponse: FingerprintMatchResponse) =
        fingerprintResponse.result.map { MatchResult(it.personId, it.confidenceScore.toInt(), Tier.computeTier(it.confidenceScore)) }

    private fun buildAppIdentifyResponseForFingerprint(fingerprintResponse: FingerprintMatchResponse,
                                                       sessionId: String): AppIdentifyResponse {
        val resultSortedByConfidence = fingerprintResponse.result.sortedBy {
            it.confidenceScore
        }

        return AppIdentifyResponse(resultSortedByConfidence.map {
            MatchResult(it.personId, it.confidenceScore.toInt(), Tier.computeTier(it.confidenceScore))
        }, sessionId)
    }

    private fun buildAppIdentifyResponseForFace(faceResponse: FaceMatchResponse,
                                                sessionId: String): AppIdentifyResponse {
        val resultsSortedByConfidence = faceResponse.result.sortedBy { it.confidence }

        return AppIdentifyResponse(
            getSortedIdentificationsForFace(resultsSortedByConfidence),
            sessionId
        )
    }

    private fun getSortedIdentificationsForFace(resultsSortedByConfidence: List<FaceMatchResult>) =
        resultsSortedByConfidence.map {
            MatchResult(it.guidFound, it.confidence.toInt(), Tier.computeTier(it.confidence))
        }
}