package com.simprints.id.data.db.analytics

import com.simprints.id.model.Callout


interface AnalyticsManager {

    fun logError(error: Error)

    fun logAlert(alertName: String, apiKey: String, moduleId: String, userId: String,
                 deviceId: String)

    fun logSafeException(exception: RuntimeException)

    fun logUserProperties(userId: String, apiKey: String, moduleId: String, deviceId: String)

    fun logScannerProperties(macAddress: String, scannerId: String)

    fun logLogin(callout: Callout)

    fun logGuidSelectionService(apiKey: String, sessionId: String, selectedGuid: String,
                                callbackSent: Boolean, androidId: String)

    fun logConnectionStateChange(connected: Boolean, apiKey: String,
                                 androidId: String, sessionId: String)

    fun logAuthStateChange(authenticated: Boolean, apiKey: String,
                           androidId: String, sessionId: String)

}