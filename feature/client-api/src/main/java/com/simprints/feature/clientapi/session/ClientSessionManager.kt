package com.simprints.feature.clientapi.session

import com.simprints.core.ExternalScope
import com.simprints.core.tools.time.TimeHelper
import com.simprints.core.tools.utils.SimNetworkUtils
import com.simprints.feature.clientapi.models.ActionRequest
import com.simprints.feature.clientapi.models.CommCareConstants
import com.simprints.feature.clientapi.models.OdkConstants
import com.simprints.infra.events.EventRepository
import com.simprints.infra.events.event.domain.models.AuthorizationEvent
import com.simprints.infra.events.event.domain.models.AuthorizationEvent.AuthorizationPayload.AuthorizationResult
import com.simprints.infra.events.event.domain.models.CompletionCheckEvent
import com.simprints.infra.events.event.domain.models.ConnectivitySnapshotEvent
import com.simprints.infra.events.event.domain.models.IntentParsingEvent
import com.simprints.infra.events.event.domain.models.InvalidIntentEvent
import com.simprints.infra.events.event.domain.models.SuspiciousIntentEvent
import com.simprints.infra.events.event.domain.models.callback.IdentificationCallbackEvent
import com.simprints.infra.events.event.domain.models.callout.ConfirmationCalloutEvent
import com.simprints.infra.events.event.domain.models.callout.EnrolmentCalloutEvent
import com.simprints.infra.events.event.domain.models.callout.EnrolmentLastBiometricsCalloutEvent
import com.simprints.infra.events.event.domain.models.callout.IdentificationCalloutEvent
import com.simprints.infra.events.event.domain.models.callout.VerificationCalloutEvent
import com.simprints.infra.logging.LoggingConstants
import com.simprints.infra.logging.Simber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ClientSessionManager @Inject constructor(
    private val coreEventRepository: EventRepository,
    private val timeHelper: TimeHelper,
    private val simNetworkUtils: SimNetworkUtils,
    @ExternalScope private val externalScope: CoroutineScope
) {

    suspend fun createSession(integrationInfo: IntentParsingEvent.IntentParsingPayload.IntegrationInfo) {
        val sessionEvent = coreEventRepository.createSession()
        coreEventRepository.addOrUpdateEvent(IntentParsingEvent(timeHelper.now(), integrationInfo))
        Simber.tag(LoggingConstants.CrashReportingCustomKeys.SESSION_ID, true).i(sessionEvent.id)
    }

    suspend fun getCurrentSessionId(): String = coreEventRepository.getCurrentCaptureSessionEvent().id

    suspend fun isCurrentSessionAnIdentificationOrEnrolment(): Boolean = getCurrentSessionId()
        .let { coreEventRepository.observeEventsFromSession(it).toList() }
        .any { it is IdentificationCalloutEvent || it is EnrolmentCalloutEvent }

    suspend fun sessionHasIdentificationCallback(sessionId: String): Boolean = coreEventRepository
        .observeEventsFromSession(sessionId)
        .toList()
        .any { it is IdentificationCallbackEvent }

    fun addUnknownExtrasEvent(unknownExtras: Map<String, Any?>) {
        if (unknownExtras.isNotEmpty()) {
            externalScope.launch {
                coreEventRepository.addOrUpdateEvent(SuspiciousIntentEvent(timeHelper.now(), unknownExtras))
            }
        }
    }

    suspend fun addConnectivityStateEvent() {
        coreEventRepository.addOrUpdateEvent(ConnectivitySnapshotEvent(timeHelper.now(), simNetworkUtils.connectionsStates))
    }

    suspend fun addRequestActionEvent(request: ActionRequest) {
        val startTime = timeHelper.now()
        val event = with(request) {
            when (this) {
                is ActionRequest.EnrolActionRequest -> EnrolmentCalloutEvent(startTime, projectId, userId, moduleId, metadata)
                is ActionRequest.IdentifyActionRequest -> IdentificationCalloutEvent(startTime, projectId, userId, moduleId, metadata)
                is ActionRequest.VerifyActionRequest -> VerificationCalloutEvent(startTime, projectId, userId, moduleId, verifyGuid, metadata)
                is ActionRequest.ConfirmActionRequest -> ConfirmationCalloutEvent(startTime, projectId, selectedGuid, sessionId)
                is ActionRequest.EnrolLastBiometricActionRequest -> EnrolmentLastBiometricsCalloutEvent(startTime, projectId, userId, moduleId, metadata, sessionId)
            }
        }
        coreEventRepository.addOrUpdateEvent(event)

    }

    fun addInvalidIntentEvent(action: String, extras: Map<String, Any>) {
        externalScope.launch {
            coreEventRepository.addOrUpdateEvent(InvalidIntentEvent(timeHelper.now(), action, extras))
        }
    }

    fun addCompletionCheckEvent(flowCompleted: Boolean) {
        externalScope.launch {
            coreEventRepository.addOrUpdateEvent(CompletionCheckEvent(timeHelper.now(), flowCompleted))
        }
    }

    suspend fun addAuthorizationEvent(request: ActionRequest, authorized: Boolean) {
        val userInfo = request
            .takeIf { authorized }
            ?.let { AuthorizationEvent.AuthorizationPayload.UserInfo(it.projectId, it.userId) }
        val result = if (authorized) AuthorizationResult.AUTHORIZED else AuthorizationResult.NOT_AUTHORIZED

        coreEventRepository.addOrUpdateEvent(AuthorizationEvent(timeHelper.now(), result, userInfo))
    }

    suspend fun closeCurrentSessionNormally() {
        coreEventRepository.closeCurrentSession()
    }

    fun deleteSessionEvents(sessionId: String) {
        externalScope.launch {
            coreEventRepository.deleteSessionEvents(sessionId)
        }
    }
}