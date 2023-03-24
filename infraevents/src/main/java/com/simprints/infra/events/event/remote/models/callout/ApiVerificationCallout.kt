package com.simprints.infra.events.remote.models.callout

import androidx.annotation.Keep

@Keep
data class ApiVerificationCallout(val projectId: String,
                             val userId: String,
                             val moduleId: String,
                             val metadata: String,
                             val verifyGuid: String): ApiCallout(ApiCalloutType.Verification)