package com.simprints.id.data.db.session.remote.events

import androidx.annotation.Keep
import com.simprints.id.data.db.session.domain.models.events.MatchEntry

@Keep
class ApiMatchEntry(val candidateId: String, val score: Float) {
    constructor(matchEntry: MatchEntry):
        this(matchEntry.candidateId, matchEntry.score)
}