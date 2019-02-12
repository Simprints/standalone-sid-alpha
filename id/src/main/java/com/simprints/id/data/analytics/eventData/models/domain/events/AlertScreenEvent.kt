package com.simprints.id.data.analytics.eventData.models.domain.events

import com.simprints.id.data.analytics.eventData.models.domain.EventType
import com.simprints.id.domain.ALERT_TYPE

class AlertScreenEvent(val relativeStartTime: Long,
                       val alertType: ALERT_TYPE) : Event(EventType.ALERT_SCREEN)