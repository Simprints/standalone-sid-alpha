package com.simprints.face.controllers.core.events.model

import com.simprints.id.data.db.session.domain.models.events.FaceCaptureRetryEvent as CoreFaceCaptureRetryEvent

class FaceCaptureRetryEvent(
    startTime: Long,
    endTime: Long
) : Event(EventType.FACE_CAPTURE_RETRY, startTime, endTime) {
    fun fromDomainToCore(): CoreFaceCaptureRetryEvent = CoreFaceCaptureRetryEvent(startTime, endTime)
}