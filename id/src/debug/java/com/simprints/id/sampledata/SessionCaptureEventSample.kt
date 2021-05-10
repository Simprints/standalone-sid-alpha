package com.simprints.id.sampledata

import android.os.Build
import com.simprints.id.data.db.event.domain.models.EventLabels
import com.simprints.id.data.db.event.domain.models.session.DatabaseInfo
import com.simprints.id.data.db.event.domain.models.session.Device
import com.simprints.id.data.db.event.domain.models.session.Location
import com.simprints.id.data.db.event.domain.models.session.SessionCaptureEvent
import com.simprints.id.domain.modality.Modes
import com.simprints.id.sampledata.SampleDefaults.CREATED_AT
import com.simprints.id.sampledata.SampleDefaults.ENDED_AT
import com.simprints.id.sampledata.SampleDefaults.GUID1

object SessionCaptureEventSample : SampleEvent() {

    override fun getEvent(
        labels: EventLabels,
        isClosed: Boolean
    ): SessionCaptureEvent {
        val appVersionNameArg = "appVersionName"
        val libSimprintsVersionNameArg = "libSimprintsVersionName"
        val languageArg = "language"
        val deviceArg = Device(
            Build.VERSION.SDK_INT.toString(),
            Build.MANUFACTURER + "_" + Build.MODEL,
            labels.deviceId!!
        )

        val databaseInfoArg = DatabaseInfo(2, recordCount = 2)
        val locationArg = Location(0.0, 0.0)

        val event = SessionCaptureEvent(
            labels.sessionId!!,
            labels.projectId!!,
            CREATED_AT,
            listOf(Modes.FINGERPRINT, Modes.FACE),
            appVersionNameArg,
            libSimprintsVersionNameArg,
            languageArg,
            deviceArg,
            databaseInfoArg
        )
        event.payload.location = locationArg
        event.payload.analyticsId = GUID1
        event.payload.endedAt = ENDED_AT
        event.payload.sessionIsClosed = isClosed

        return event
    }

}