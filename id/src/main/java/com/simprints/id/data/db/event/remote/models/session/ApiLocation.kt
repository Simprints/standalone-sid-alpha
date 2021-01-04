package com.simprints.id.data.db.event.remote.models.session

import androidx.annotation.Keep
import com.simprints.id.data.db.event.domain.models.session.Location

@Keep
data class ApiLocation(var latitude: Double = 0.0,
                       var longitude: Double = 0.0) {

    constructor(location: Location) :
        this(location.latitude, location.longitude)
}