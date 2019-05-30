package com.simprints.moduleapi.fingerprint.requests

import android.os.Parcelable


interface IFingerprintRequest : Parcelable {

    companion object {
        const val BUNDLE_KEY = "FingerprintRequestBundleKey"
    }

    val projectId: String
    val userId: String
    val moduleId: String
    val metadata: String
    val language: String
    val fingerStatus: Map<IFingerIdentifier, Boolean>
    val logoExists: Boolean
    val programName: String
    val organizationName: String
}