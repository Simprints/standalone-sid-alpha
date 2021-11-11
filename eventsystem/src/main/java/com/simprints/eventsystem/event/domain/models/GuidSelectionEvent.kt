package com.simprints.eventsystem.event.domain.models

import androidx.annotation.Keep
import com.simprints.eventsystem.event.domain.models.EventType.GUID_SELECTION
import java.util.*

@Keep
data class GuidSelectionEvent(
    override val id: String = UUID.randomUUID().toString(),
    override var labels: EventLabels,
    override val payload: GuidSelectionPayload,
    override val type: EventType
) : Event() {

    constructor(
        createdAt: Long,
        selectedId: String,
        labels: EventLabels = EventLabels()
    ) : this(
        UUID.randomUUID().toString(),
        labels,
        GuidSelectionPayload(createdAt, EVENT_VERSION, selectedId),
        GUID_SELECTION)

    @Keep
    data class GuidSelectionPayload(override val createdAt: Long,
                                    override val eventVersion: Int,
                                    val selectedId: String,
                                    override val type: EventType = GUID_SELECTION,
                                    override val endedAt: Long = 0) : EventPayload()

    companion object {
        const val EVENT_VERSION = 1
    }
}