package com.simprints.eventsystem.sampledata

import com.simprints.eventsystem.event.domain.models.EventLabels
import com.simprints.eventsystem.event.domain.models.callback.EnrolmentCallbackEvent
import com.simprints.eventsystem.sampledata.SampleDefaults.CREATED_AT
import com.simprints.eventsystem.sampledata.SampleDefaults.GUID1

object EnrolmentCallbackEventSample : SampleEvent() {
    override fun getEvent(
        labels: EventLabels,
        isClosed: Boolean
    ): EnrolmentCallbackEvent {
        return EnrolmentCallbackEvent(CREATED_AT, GUID1, labels)
    }
}