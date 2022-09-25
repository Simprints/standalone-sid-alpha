package com.simprints.id.enrolmentrecords.remote.models.fingerprint

import androidx.annotation.Keep
import com.simprints.core.domain.fingerprint.FingerprintSample
import com.simprints.core.domain.fingerprint.concatTemplates
import com.simprints.core.tools.utils.EncodingUtils
import com.simprints.id.enrolmentrecords.remote.models.ApiBiometricReference
import java.util.*

@Keep
data class ApiFingerprintReference(
    val id: String,
    val templates: List<ApiFingerprintTemplate>,
    val format: ApiFingerprintTemplateFormat,
    val metadata: HashMap<String, String>? = null
) : ApiBiometricReference(ApiBiometricReferenceType.FingerprintReference) {
}

fun List<FingerprintSample>.toApi(encoder: EncodingUtils): ApiFingerprintReference? =
    if (isNotEmpty()) {
        ApiFingerprintReference(
            UUID.nameUUIDFromBytes(concatTemplates()).toString(),
            map {
                ApiFingerprintTemplate(
                    it.templateQualityScore,
                    encoder.byteArrayToBase64(it.template),
                    it.fingerIdentifier.toApi()
                )
            },
            first().format.toApi()
        )
    } else {
        null
    }