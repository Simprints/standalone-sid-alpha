package com.simprints.matcher.usecases

import com.simprints.core.DispatcherBG
import com.simprints.face.infra.basebiosdk.matching.FaceIdentity
import com.simprints.face.infra.basebiosdk.matching.FaceMatcher
import com.simprints.face.infra.basebiosdk.matching.FaceSample
import com.simprints.face.infra.biosdkresolver.ResolveFaceBioSdkUseCase
import com.simprints.infra.config.store.models.Project
import com.simprints.infra.enrolment.records.repository.EnrolmentRecordRepository
import com.simprints.infra.enrolment.records.repository.domain.models.BiometricDataSource
import com.simprints.infra.enrolment.records.repository.domain.models.SubjectQuery
import com.simprints.infra.logging.LoggingConstants
import com.simprints.infra.logging.Simber
import com.simprints.infra.templateprotection.TemplateProtection
import com.simprints.matcher.FaceMatchResult
import com.simprints.matcher.MatchParams
import com.simprints.matcher.usecases.MatcherUseCase.MatcherState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

internal class FaceMatcherUseCase @Inject constructor(
    private val enrolmentRecordRepository: EnrolmentRecordRepository,
    private val resolveFaceBioSdk: ResolveFaceBioSdkUseCase,
    private val createRanges: CreateRangesUseCase,
    private val templateProtection: TemplateProtection,
    @DispatcherBG private val dispatcher: CoroutineDispatcher,
) : MatcherUseCase {
    private lateinit var faceMatcher: FaceMatcher
    override val crashReportTag = LoggingConstants.CrashReportTag.FACE_MATCHING

    override suspend operator fun invoke(
        matchParams: MatchParams,
        project: Project,
    ): Flow<MatcherState> = channelFlow {
        Simber.i("Initialising matcher", tag = crashReportTag)
        faceMatcher = resolveFaceBioSdk().matcher
        if (matchParams.probeFaceSamples.isEmpty()) {
            send(MatcherState.Success(emptyList(), 0, faceMatcher.matcherName))
            return@channelFlow
        }
        val samples = mapSamples(matchParams.probeFaceSamples)

        val queryWithSupportedFormat = matchParams.queryForCandidates.copy(
            faceSampleFormat = faceMatcher.supportedTemplateFormat,
        )
        val expectedCandidates = enrolmentRecordRepository.count(
            queryWithSupportedFormat,
            dataSource = matchParams.biometricDataSource,
        )
        if (expectedCandidates == 0) {
            send(MatcherState.Success(emptyList(), 0, faceMatcher.matcherName))
            return@channelFlow
        }

        Simber.i("Matching candidates", tag = crashReportTag)
        send(MatcherState.LoadingStarted(expectedCandidates))
        // When using local DB loadedCandidates = expectedCandidates
        // However, when using CommCare as data source, loadedCandidates < expectedCandidates
        // as it's count function does not take into account filtering criteria
        var loadedCandidates = 0
        val resultItems = coroutineScope {
            createRanges(expectedCandidates)
                .map { range ->
                    async(dispatcher) {
                        val batchCandidates = getCandidates(
                            queryWithSupportedFormat,
                            range,
                            project = project,
                            dataSource = matchParams.biometricDataSource,
                        ) {
                            // When a candidate is loaded
                            loadedCandidates++
                            trySend(MatcherState.CandidateLoaded)
                        }
                        match(batchCandidates, samples)
                    }
                }.awaitAll()
                .reduce { acc, subSet -> acc.addAll(subSet) }
                .toList()
        }

        Simber.i("Matched $loadedCandidates candidates", tag = crashReportTag)

        send(MatcherState.Success(resultItems, loadedCandidates, faceMatcher.matcherName))
    }

    private fun mapSamples(probes: List<MatchParams.FaceSample>) = probes.map { FaceSample(it.faceId, it.template) }

    private suspend fun getCandidates(
        query: SubjectQuery,
        range: IntRange,
        dataSource: BiometricDataSource = BiometricDataSource.Simprints,
        project: Project,
        onCandidateLoaded: () -> Unit,
    ) = enrolmentRecordRepository
        .loadFaceIdentities(query, range, dataSource, project, onCandidateLoaded)
        .map {
            FaceIdentity(
                it.subjectId,
                it.faces.map { face -> FaceSample(face.id, face.template) },
            )
        }

    private suspend fun match(
        batchCandidates: List<FaceIdentity>,
        samples: List<FaceSample>,
    ) = batchCandidates.fold(MatchResultSet<FaceMatchResult.Item>()) { acc, item ->
        val candidateAux = templateProtection.getAuxData(item.subjectId)

        // TODO this is a very inefficient 1:N implementation with protected templates
        val encodedSamples = if (candidateAux != null) {
            samples.map { face ->
                face.copy(template = templateProtection.encodeTemplate(face.template, candidateAux))
            }
        } else {
            samples
        }

        acc.add(
            FaceMatchResult.Item(
                item.subjectId,
                faceMatcher.getHighestComparisonScoreForCandidate(encodedSamples, item),
            ),
        )
    }
}
