package com.simprints.id.data.db.event.remote.models.subject

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.simprints.id.data.db.event.domain.models.subject.BiometricReference
import com.simprints.id.data.db.event.domain.models.subject.FaceTemplate
import com.simprints.id.data.db.event.domain.models.subject.FingerIdentifier
import com.simprints.id.data.db.event.domain.models.subject.FingerprintTemplate
import com.simprints.id.data.db.event.remote.models.subject.ApiBiometricReferenceType.FaceReference
import com.simprints.id.data.db.event.remote.models.subject.ApiBiometricReferenceType.FingerprintReference
import java.util.*
import com.simprints.id.data.db.event.domain.models.subject.FaceReference as DomainFaceReference
import com.simprints.id.data.db.event.domain.models.subject.FingerprintReference as DomainFingerprintReference

private const val FACE_REFERENCE_KEY = "FaceReference"
private const val FINGERPRINT_REFERENCE_KEY = "FingerprintReference"

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = ApiFaceReference::class, name = FACE_REFERENCE_KEY),
    JsonSubTypes.Type(value = ApiFingerprintReference::class, name = FINGERPRINT_REFERENCE_KEY)
)
interface ApiBiometricReference {
    val type: ApiBiometricReferenceType
    val id: String
}

@Keep
data class ApiFaceReference(
    override val id: String = UUID.randomUUID().toString(),
    val templates: List<ApiFaceTemplate>,
    val metadata: HashMap<String, String>? = null) : ApiBiometricReference {
    override val type: ApiBiometricReferenceType = FaceReference
}

@Keep
data class ApiFingerprintReference(
    override val id: String = UUID.randomUUID().toString(),
    val templates: List<ApiFingerprintTemplate>,
    val metadata: HashMap<String, String>? = null) : ApiBiometricReference {
    override val type: ApiBiometricReferenceType = FingerprintReference
}

@Keep
enum class ApiBiometricReferenceType {
    // a constant key is required to serialise/deserialize
    // ApiBiometricReference correctly with Jackson (see annotation in ApiBiometricReference).
    // Add a key in the companion object for each enum value

    /* key added: FACE_REFERENCE_KEY */
    FaceReference,

    /* key added: FINGERPRINT_REFERENCE_KEY */
    FingerprintReference;
}

fun BiometricReference.fromDomainToApi() = when (this) {
    is DomainFaceReference -> {
        ApiFaceReference(id, templates.map { it.fromDomainToApi() }, metadata)
    }
    is DomainFingerprintReference -> {
        ApiFingerprintReference(id, templates.map { it.fromDomainToApi() }, metadata)
    }
}

fun FaceTemplate.fromDomainToApi() = ApiFaceTemplate(template)

fun FingerprintTemplate.fromDomainToApi() =
    ApiFingerprintTemplate(quality, template, finger.fromDomainToApi())

fun FingerIdentifier.fromDomainToApi() = when (this) {
    FingerIdentifier.RIGHT_5TH_FINGER -> ApiFingerIdentifier.RIGHT_5TH_FINGER
    FingerIdentifier.RIGHT_4TH_FINGER -> ApiFingerIdentifier.RIGHT_4TH_FINGER
    FingerIdentifier.RIGHT_3RD_FINGER -> ApiFingerIdentifier.RIGHT_3RD_FINGER
    FingerIdentifier.RIGHT_INDEX_FINGER -> ApiFingerIdentifier.RIGHT_INDEX_FINGER
    FingerIdentifier.RIGHT_THUMB -> ApiFingerIdentifier.RIGHT_THUMB
    FingerIdentifier.LEFT_THUMB -> ApiFingerIdentifier.LEFT_THUMB
    FingerIdentifier.LEFT_INDEX_FINGER -> ApiFingerIdentifier.LEFT_INDEX_FINGER
    FingerIdentifier.LEFT_3RD_FINGER -> ApiFingerIdentifier.LEFT_3RD_FINGER
    FingerIdentifier.LEFT_4TH_FINGER -> ApiFingerIdentifier.LEFT_4TH_FINGER
    FingerIdentifier.LEFT_5TH_FINGER -> ApiFingerIdentifier.LEFT_5TH_FINGER
}


