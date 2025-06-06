package com.simprints.feature.consent.screens.consent.helpers

import android.content.Context
import com.simprints.feature.consent.ConsentType
import com.simprints.infra.config.store.models.ConsentConfiguration
import com.simprints.infra.config.store.models.GeneralConfiguration.Modality
import com.simprints.infra.resources.R

internal data class GeneralConsentTextHelper(
    private val config: ConsentConfiguration,
    private val modalities: List<Modality>,
    private val consentType: ConsentType,
) {
    // TODO All the `getString(id).format(arg,arg)` calls should be `getString(id,arg,arg)` one strings are fixed

    // First argument in consent text should always be program name, second is modality specific access/use case text
    fun assembleText(context: Context) = StringBuilder()
        .apply {
            val modalityUseCase = getModalitySpecificUseCaseText(context, modalities)
            val modalityAccess = getModalitySpecificAccessText(context, modalities)

            filterAppRequestForConsent(context, consentType, config, modalityUseCase)
            filterForDataSharingOptions(context, config, modalityUseCase, modalityAccess)
        }.toString()

    private fun StringBuilder.filterAppRequestForConsent(
        context: Context,
        consentType: ConsentType,
        config: ConsentConfiguration,
        modalityUseCase: String,
    ) {
        when (consentType) {
            ConsentType.ENROL -> appendTextForConsentEnrol(context, config.generalPrompt, config.programName, modalityUseCase)
            ConsentType.IDENTIFY, ConsentType.VERIFY -> appendTextForConsentVerifyOrIdentify(context, config.programName, modalityUseCase)
        }
    }

    private fun StringBuilder.appendTextForConsentEnrol(
        context: Context,
        config: ConsentConfiguration.ConsentPromptConfiguration?,
        programName: String,
        modalityUseCase: String,
    ) = when (config?.enrolmentVariant) {
        ConsentConfiguration.ConsentEnrolmentVariant.ENROLMENT_ONLY -> appendSentence(
            context.getString(R.string.consent_enrol_only).format(programName, modalityUseCase),
        )

        ConsentConfiguration.ConsentEnrolmentVariant.STANDARD -> appendSentence(
            context.getString(R.string.consent_enrol).format(programName, modalityUseCase),
        )

        else -> this
    }

    private fun StringBuilder.appendTextForConsentVerifyOrIdentify(
        context: Context,
        programName: String,
        modalityUseCase: String,
    ) = appendSentence(
        context.getString(R.string.consent_id_verify).format(programName, modalityUseCase),
    )

    private fun StringBuilder.filterForDataSharingOptions(
        context: Context,
        config: ConsentConfiguration,
        modalityUseCase: String,
        modalityAccess: String,
    ) {
        if (config.generalPrompt?.dataSharedWithPartner == true) {
            appendSentence(
                context
                    .getString(R.string.consent_share_data_yes)
                    .format(config.organizationName, modalityAccess),
            )
        } else {
            appendSentence(
                context
                    .getString(R.string.consent_share_data_no)
                    .format(modalityAccess),
            )
        }
        if (config.generalPrompt?.dataUsedForRAndD == true) {
            appendSentence(context.getString(R.string.consent_collect_yes))
        }
        if (config.generalPrompt?.privacyRights == true) {
            appendSentence(context.getString(R.string.consent_privacy_rights))
        }
        if (config.generalPrompt?.confirmation == true) {
            appendSentence(
                context.getString(R.string.consent_confirmation).format(modalityUseCase),
            )
        }
    }

    private fun getModalitySpecificUseCaseText(
        context: Context,
        modalities: List<Modality>,
    ) = if (modalities.size == 1) {
        getSingleModalitySpecificUseCaseText(context, modalities)
    } else {
        getConcatenatedModalitiesUseCaseText(context)
    }

    private fun getConcatenatedModalitiesUseCaseText(context: Context) = listOf(
        context.getString(R.string.consent_biometrics_general_fingerprint),
        context.getString(R.string.consent_biometric_concat_modalities),
        context.getString(R.string.consent_biometric_general_face),
    ).joinToString(" ")

    private fun getSingleModalitySpecificUseCaseText(
        context: Context,
        modalities: List<Modality>,
    ) = when (modalities.first()) {
        Modality.FACE -> context.getString(R.string.consent_biometric_general_face)
        Modality.FINGERPRINT -> context.getString(R.string.consent_biometrics_general_fingerprint)
    }

    private fun getModalitySpecificAccessText(
        context: Context,
        modalities: List<Modality>,
    ) = if (modalities.size == 1) {
        getSingleModalityAccessText(context, modalities)
    } else {
        getConcatenatedModalitiesAccessText(context)
    }

    private fun getConcatenatedModalitiesAccessText(context: Context) =
        context.getString(R.string.consent_biometrics_access_fingerprint_face)

    private fun getSingleModalityAccessText(
        context: Context,
        modalities: List<Modality>,
    ) = when (modalities.first()) {
        Modality.FACE -> context.getString(R.string.consent_biometrics_access_face)
        Modality.FINGERPRINT -> context.getString(R.string.consent_biometrics_access_fingerprint)
    }
}
