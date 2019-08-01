package com.simprints.id.data.analytics.eventdata.models.remote.events

import androidx.annotation.Keep
import com.simprints.id.data.analytics.eventdata.models.domain.events.CompletionCheckEvent

@Keep
class ApiCompletionCheckEvent(val relativeStartTime: Long,
                              val completed: Boolean) : ApiEvent(ApiEventType.COMPLETION_CHECK) {

    constructor(completionCheckEvent: CompletionCheckEvent) :
        this(completionCheckEvent.relativeStartTime ?: 0, completionCheckEvent.completed)
}