package com.simprints.infra.templateprotection.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simprints.biometrics.polyprotect.AuxData

@Entity
internal data class DbAuxData(
    @PrimaryKey val subjectId: String,
    // SQLite does not support array types, so storing as combined string instead
    val exponents: ByteArray,
    val coefficients: ByteArray,
)

internal fun DbAuxData.fromDbToDomain(): AuxData = AuxData(
    exponents = exponents,
    coefficients = coefficients,
)

internal fun AuxData.fromDomainToDb(subjectId: String): DbAuxData = DbAuxData(
    subjectId = subjectId,
    exponents = exponents,
    coefficients = coefficients,
)
