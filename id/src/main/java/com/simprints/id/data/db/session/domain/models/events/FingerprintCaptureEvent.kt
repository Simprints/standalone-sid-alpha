package com.simprints.id.data.db.session.domain.models.events

import androidx.annotation.Keep
import com.simprints.id.data.db.person.domain.FingerIdentifier

@Keep
class FingerprintCaptureEvent(
    startTime: Long,
    endTime: Long,
    val finger: FingerIdentifier,
    val qualityThreshold: Int,
    val result: Result,
    val fingerprint: Fingerprint?,
    id: String
) : Event(EventType.FINGERPRINT_CAPTURE, startTime, endTime, id) {

    @Keep
    class Fingerprint(val finger: FingerIdentifier, val quality: Int, val template: String)

    @Keep
    enum class Result {
        GOOD_SCAN,
        BAD_QUALITY,
        NO_FINGER_DETECTED,
        SKIPPED,
        FAILURE_TO_ACQUIRE;
    }

}