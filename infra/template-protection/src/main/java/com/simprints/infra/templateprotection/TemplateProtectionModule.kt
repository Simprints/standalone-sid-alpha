package com.simprints.infra.templateprotection

import com.simprints.biometrics.polyprotect.PolyProtect
import com.simprints.infra.templateprotection.database.AuxDataDao
import com.simprints.infra.templateprotection.database.AuxDataDatabaseFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TemplateProtectionModule {
    @Provides
    internal fun provideAuxDataDao(databaseFactory: AuxDataDatabaseFactory): AuxDataDao = databaseFactory.get().auxDataDao

    @Provides
    internal fun provideTemplateProtection(): PolyProtect = PolyProtect(
        // TODO set defaults
    )
}
