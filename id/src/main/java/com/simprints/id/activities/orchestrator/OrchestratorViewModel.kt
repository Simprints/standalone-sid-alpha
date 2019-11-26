package com.simprints.id.activities.orchestrator

import android.content.Intent
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.id.data.analytics.eventdata.controllers.domain.SessionEventsManager
import com.simprints.id.domain.modality.Modality
import com.simprints.id.domain.moduleapi.app.DomainToModuleApiAppResponse
import com.simprints.id.domain.moduleapi.app.requests.AppRequest
import com.simprints.id.orchestrator.OrchestratorManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class OrchestratorViewModel(
    private val orchestratorManager: OrchestratorManager,
    private val orchestratorEventsHelper: OrchestratorEventsHelper,
    private val modalities: List<Modality>,
    private val sessionEventsManager: SessionEventsManager,
    private val domainToModuleApiConverter: DomainToModuleApiAppResponse
) : ViewModel() {

    val ongoingStep = orchestratorManager.ongoingStep

    val appResponse = Transformations.map(orchestratorManager.appResponse) {
        it?.let {
            orchestratorEventsHelper.addCallbackEventInSessions(it)
            domainToModuleApiConverter.fromDomainModuleApiAppResponse(it)
        }
    }

    fun startModalityFlow(appRequest: AppRequest) {
        runBlocking {
            orchestratorManager.initialise(
                modalities,
                appRequest,
                getCurrentSessionId()) //TODO: consider to pass sessionId as parameter from previous Activities. Currently blocking UI
        }
    }

    private fun getCurrentSessionId(): String =
        sessionEventsManager.getCurrentSession().map { it.id }.blockingGet()

    fun onModalStepRequestDone(appRequest: AppRequest, requestCode: Int, resultCode: Int, data: Intent?) {
        viewModelScope.launch {
            orchestratorManager.handleIntentResult(appRequest, requestCode, resultCode, data)
        }
    }

    fun restoreState() {
        runBlocking {
            orchestratorManager.restoreState()
        }
    }

    fun clearState() {
        orchestratorManager.clearState()
    }

}