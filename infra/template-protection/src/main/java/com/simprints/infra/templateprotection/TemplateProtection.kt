package com.simprints.infra.templateprotection

import com.simprints.biometrics.polyprotect.AuxData
import com.simprints.biometrics.polyprotect.PolyProtect
import com.simprints.infra.templateprotection.database.AuxDataDao
import com.simprints.infra.templateprotection.database.fromDbToDomain
import com.simprints.infra.templateprotection.database.fromDomainToDb
import javax.inject.Inject

// TODO add potential corruption handling
class TemplateProtection @Inject internal constructor(
    private val auxDao: AuxDataDao,
    private val polyprotect: PolyProtect,
) {
    fun createAuxData(): AuxData = polyprotect.generateAuxData()

    suspend fun getAuxData(subjectId: String): AuxData? = auxDao.getAuxData(subjectId)?.fromDbToDomain()

    suspend fun saveAuxData(
        subjectId: String,
        templateAuxData: AuxData,
    ) {
        auxDao.saveAuxData(templateAuxData.fromDomainToDb(subjectId))
    }

    suspend fun deleteAuxData() {
        auxDao.deleteAll()
    }

    fun encodeTemplate(
        template: ByteArray,
        auxData: AuxData,
    ): ByteArray = polyprotect.transformTemplate(template, auxData)
}
