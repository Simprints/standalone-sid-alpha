package com.simprints.feature.clientapi.usecases

import com.simprints.core.tools.time.TimeHelper
import com.simprints.feature.clientapi.models.CommCareConstants
import com.simprints.infra.orchestration.data.ActionConstants
import com.simprints.feature.clientapi.models.OdkConstants
import com.simprints.infra.events.EventRepository
import com.simprints.infra.events.event.domain.models.IntentParsingEvent
import com.simprints.infra.logging.LoggingConstants
import com.simprints.infra.logging.Simber
import javax.inject.Inject

internal class CreateSessionIfRequiredUseCase @Inject constructor(
    private val coreEventRepository: EventRepository,
    private val timeHelper: TimeHelper,
) {

    suspend operator fun invoke(action: String) {
        val actionName = action.substringAfterLast('.')
        if (actionName == ActionConstants.ACTION_CONFIRM_IDENTITY || actionName == ActionConstants.ACTION_ENROL_LAST_BIOMETRICS) {
            return
        }
        val integrationInfo = when (action.substringBeforeLast('.')) {
            OdkConstants.PACKAGE_NAME -> IntentParsingEvent.IntentParsingPayload.IntegrationInfo.ODK
            CommCareConstants.PACKAGE_NAME -> IntentParsingEvent.IntentParsingPayload.IntegrationInfo.COMMCARE
            else -> IntentParsingEvent.IntentParsingPayload.IntegrationInfo.STANDARD
        }

        coreEventRepository.createSession()
            .also { Simber.tag(LoggingConstants.CrashReportingCustomKeys.SESSION_ID, true).i(it.id) }
        coreEventRepository.addOrUpdateEvent(IntentParsingEvent(timeHelper.now(), integrationInfo))
    }
}