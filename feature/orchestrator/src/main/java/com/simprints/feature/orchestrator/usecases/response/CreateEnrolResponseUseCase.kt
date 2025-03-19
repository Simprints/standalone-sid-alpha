package com.simprints.feature.orchestrator.usecases.response

import com.simprints.core.domain.response.AppErrorReason
import com.simprints.face.capture.FaceCaptureResult
import com.simprints.fingerprint.capture.FingerprintCaptureResult
import com.simprints.infra.config.store.models.Project
import com.simprints.infra.eventsync.sync.down.tasks.SubjectFactory
import com.simprints.infra.logging.LoggingConstants.CrashReportTag.ORCHESTRATION
import com.simprints.infra.logging.Simber
import com.simprints.infra.orchestration.data.ActionRequest
import com.simprints.infra.orchestration.data.responses.AppEnrolResponse
import com.simprints.infra.orchestration.data.responses.AppErrorResponse
import com.simprints.infra.orchestration.data.responses.AppResponse
import com.simprints.infra.templateprotection.TemplateProtection
import java.io.Serializable
import java.util.UUID
import javax.inject.Inject

internal class CreateEnrolResponseUseCase @Inject constructor(
    private val subjectFactory: SubjectFactory,
    private val enrolSubject: EnrolSubjectUseCase,
    private val templateProtection: TemplateProtection,
) {
    suspend operator fun invoke(
        request: ActionRequest.EnrolActionRequest,
        results: List<Serializable>,
        project: Project,
    ): AppResponse {
        val subjectId = UUID.randomUUID().toString()
        val auxData = templateProtection.createAuxData()

        val fingerprintCapture = results.filterIsInstance<FingerprintCaptureResult>().lastOrNull()
        val faceCapture = results.filterIsInstance<FaceCaptureResult>().lastOrNull()

        return try {
            val subject = subjectFactory.buildSubjectFromCaptureResults(
                subjectId = subjectId,
                projectId = request.projectId,
                attendantId = request.userId,
                moduleId = request.moduleId,
                fingerprintResponse = fingerprintCapture,
                faceResponse = faceCapture?.let { capture ->
                    // Deep copy to replace the templates in samples
                    capture.copy(
                        results = capture.results.map { result ->
                            result.copy(
                                sample = result.sample?.let { sample ->
                                    sample.copy(
                                        template = templateProtection.encodeTemplate(
                                            template = sample.template,
                                            auxData = auxData,
                                        ),
                                    )
                                },
                            )
                        },
                    )
                },
            )
            templateProtection.saveAuxData(subjectId, auxData)
            enrolSubject(subject, project)

            AppEnrolResponse(subject.subjectId)
        } catch (e: Exception) {
            Simber.e("Error creating enrol response", e, tag = ORCHESTRATION)
            AppErrorResponse(AppErrorReason.UNEXPECTED_ERROR)
        }
    }
}
