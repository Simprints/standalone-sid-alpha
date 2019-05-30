package com.simprints.fingerprint.data.domain.consent

import android.content.Context
import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.simprints.fingerprint.data.domain.moduleapi.fingerprint.requests.FingerprintIdentifyRequest
import com.simprints.fingerprint.data.domain.moduleapi.fingerprint.requests.FingerprintRequest
import com.simprints.fingerprint.data.domain.moduleapi.fingerprint.requests.FingerprintVerifyRequest
import com.simprints.fingerprint.R

@Keep
data class GeneralConsent(
    @SerializedName("consent_enrol_only") var consentEnrolOnly: Boolean = false,
    @SerializedName("consent_enrol") var consentEnrol: Boolean = true,
    @SerializedName("consent_id_verify") var consentIdVerify: Boolean = true,
    @SerializedName("consent_share_data_no") var consentShareDataNo: Boolean = true,
    @SerializedName("consent_share_data_yes") var consentShareDataYes: Boolean = false,
    @SerializedName("consent_collect_yes") var consentCollectYes: Boolean = false,
    @SerializedName("consent_privacy_rights") var consentPrivacyRights: Boolean = true,
    @SerializedName("consent_confirmation") var consentConfirmation: Boolean = true
) {

    fun assembleText(context: Context, fingerprint: FingerprintRequest, programName: String, organisationName: String) = StringBuilder().apply {
        when (fingerprint) {
            is FingerprintIdentifyRequest, is FingerprintVerifyRequest -> {
                if (consentIdVerify) append(context.getString(R.string.consent_id_verify).format(programName))
            }
            else -> {
                if (consentEnrolOnly) append(context.getString(R.string.consent_enrol_only).format(programName))
                if (consentEnrol) append(context.getString(R.string.consent_enrol).format(programName))
            }
        }
        if (consentShareDataNo) append(context.getString(R.string.consent_share_data_no))
        if (consentShareDataYes) append(context.getString(R.string.consent_share_data_yes).format(organisationName))
        if (consentCollectYes) append(context.getString(R.string.consent_collect_yes))
        if (consentPrivacyRights) append(context.getString(R.string.consent_privacy_rights))
        if (consentConfirmation) append(context.getString(R.string.consent_confirmation))
    }.toString()
}