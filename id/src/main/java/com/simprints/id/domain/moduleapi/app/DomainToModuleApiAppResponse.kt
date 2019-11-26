package com.simprints.id.domain.moduleapi.app

import android.os.Parcelable
import com.simprints.id.domain.moduleapi.app.responses.*
import com.simprints.id.domain.moduleapi.app.responses.AppErrorResponse.Reason.*
import com.simprints.id.domain.moduleapi.app.responses.AppResponseType.*
import com.simprints.id.domain.moduleapi.app.responses.entities.MatchResult
import com.simprints.id.domain.moduleapi.app.responses.entities.Tier
import com.simprints.moduleapi.app.responses.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

object DomainToModuleApiAppResponse {

    fun fromDomainModuleApiAppResponse(response: AppResponse): IAppResponse =
        when (response.type) {
            ENROL -> fromDomainToModuleApiAppEnrolResponse(response as AppEnrolResponse)
            IDENTIFY -> fromDomainToModuleApiAppIdentifyResponse(response as AppIdentifyResponse)
            REFUSAL -> fromDomainToModuleApiAppRefusalFormResponse(response as AppRefusalFormResponse)
            VERIFY -> fromDomainToModuleApiAppVerifyResponse(response as AppVerifyResponse)
            CONFIRMATION -> fromDomainToModuleApiAppIdentityConfirmationResponse(response as AppConfirmationResponse)
            ERROR -> fromDomainToModuleApiAppErrorResponse(response as AppErrorResponse)
        }

    fun fromDomainToModuleApiAppErrorResponse(response: AppErrorResponse): IAppErrorResponse =
        IAppErrorResponseImpl(fromDomainToModuleApiAppErrorReason(response.reason))

    private fun fromDomainToModuleApiAppErrorReason(reason: AppErrorResponse.Reason): IAppErrorReason =
        when (reason) {
            DIFFERENT_PROJECT_ID_SIGNED_IN -> IAppErrorReason.DIFFERENT_PROJECT_ID_SIGNED_IN
            DIFFERENT_USER_ID_SIGNED_IN -> IAppErrorReason.DIFFERENT_USER_ID_SIGNED_IN
            GUID_NOT_FOUND_ONLINE -> IAppErrorReason.GUID_NOT_FOUND_ONLINE
            UNEXPECTED_ERROR -> IAppErrorReason.UNEXPECTED_ERROR
            BLUETOOTH_NOT_SUPPORTED -> IAppErrorReason.BLUETOOTH_NOT_SUPPORTED
            LOGIN_NOT_COMPLETE -> IAppErrorReason.LOGIN_NOT_COMPLETE
        }

    private fun fromDomainToModuleApiAppEnrolResponse(enrol: AppEnrolResponse): IAppEnrolResponse = IAppEnrolResponseImpl(enrol.guid)

    private fun fromDomainToModuleApiAppVerifyResponse(verify: AppVerifyResponse): IAppVerifyResponse =
        with(verify.matchingResult) {
            IAppVerifyResponseImpl(IAppMatchResultImpl(guidFound, confidence, fromDomainToAppIAppResponseTier(tier)))
        }

    private fun fromDomainToModuleApiAppIdentifyResponse(identify: AppIdentifyResponse): IAppIdentifyResponse =
        IAppIdentifyResponseImpl(identify.identifications.map { fromDomainToModuleApiAppMatchResult(it) }, identify.sessionId)

    private fun fromDomainToModuleApiAppRefusalFormResponse(refusaResponse: AppRefusalFormResponse): IAppRefusalFormResponse =
        IAppRefusalFormResponseImpl(refusaResponse.answer.reason.toString(), refusaResponse.answer.optionalText)

    private fun fromDomainToModuleApiAppMatchResult(result: MatchResult): IAppMatchResult =
        IAppMatchResultImpl(result.guidFound, result.confidence, fromDomainToAppIAppResponseTier(result.tier))

    private fun fromDomainToModuleApiAppIdentityConfirmationResponse(response: AppConfirmationResponse) =
        IAppConfirmationResponseImpl(response.identificationOutcome)

    private fun fromDomainToAppIAppResponseTier(tier: Tier): IAppResponseTier =
        when (tier) {
            Tier.TIER_1 -> IAppResponseTier.TIER_1
            Tier.TIER_2 -> IAppResponseTier.TIER_2
            Tier.TIER_3 -> IAppResponseTier.TIER_3
            Tier.TIER_4 -> IAppResponseTier.TIER_4
            Tier.TIER_5 -> IAppResponseTier.TIER_5
        }
}

@Parcelize
private class IAppEnrolResponseImpl(override val guid: String) : IAppEnrolResponse {
    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.ENROL
}

@Parcelize
private class IAppErrorResponseImpl(override val reason: IAppErrorReason) : IAppErrorResponse {
    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.ERROR
}

@Parcelize
private class IAppIdentifyResponseImpl(override val identifications: List<IAppMatchResult>,
                                       override val sessionId: String) : IAppIdentifyResponse {

    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.IDENTIFY
}

@Parcelize
private class IAppRefusalFormResponseImpl(
    override val reason: String,
    override val extra: String) : IAppRefusalFormResponse {
    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.REFUSAL
}

@Parcelize
private class IAppVerifyResponseImpl(override val matchResult: IAppMatchResult) : IAppVerifyResponse {
    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.VERIFY
}

@Parcelize
private data class IAppMatchResultImpl(
    override val guid: String,
    override val confidence: Int,
    override val tier: IAppResponseTier) : Parcelable, IAppMatchResult

@Parcelize
private data class IAppConfirmationResponseImpl(
    override val identificationOutcome: Boolean
) : IAppConfirmationResponse {
    @IgnoredOnParcel override val type: IAppResponseType = IAppResponseType.CONFIRMATION
}