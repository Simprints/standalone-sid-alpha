package com.simprints.infra.config.store.local

import androidx.datastore.core.DataStore
import com.simprints.core.domain.tokenization.TokenizableString
import com.simprints.core.tools.utils.LanguageHelper
import com.simprints.infra.authstore.AuthStore
import com.simprints.infra.config.store.AbsolutePath
import com.simprints.infra.config.store.local.models.ProtoDeviceConfiguration
import com.simprints.infra.config.store.local.models.ProtoProject
import com.simprints.infra.config.store.local.models.ProtoProjectConfiguration
import com.simprints.infra.config.store.local.models.toDomain
import com.simprints.infra.config.store.local.models.toProto
import com.simprints.infra.config.store.models.ConsentConfiguration
import com.simprints.infra.config.store.models.DecisionPolicy
import com.simprints.infra.config.store.models.DeviceConfiguration
import com.simprints.infra.config.store.models.DownSynchronizationConfiguration
import com.simprints.infra.config.store.models.DownSynchronizationConfiguration.Companion.DEFAULT_DOWN_SYNC_MAX_AGE
import com.simprints.infra.config.store.models.FaceConfiguration
import com.simprints.infra.config.store.models.GeneralConfiguration
import com.simprints.infra.config.store.models.IdentificationConfiguration
import com.simprints.infra.config.store.models.Project
import com.simprints.infra.config.store.models.ProjectConfiguration
import com.simprints.infra.config.store.models.SettingsPasswordConfig
import com.simprints.infra.config.store.models.SynchronizationConfiguration
import com.simprints.infra.config.store.models.TokenKeyType
import com.simprints.infra.config.store.models.UpSynchronizationConfiguration
import com.simprints.infra.config.store.tokenization.TokenizationProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class ConfigLocalDataSourceImpl @Inject constructor(
    @AbsolutePath private val absolutePath: String,
    private val projectDataStore: DataStore<ProtoProject>,
    private val configDataStore: DataStore<ProtoProjectConfiguration>,
    private val deviceConfigDataStore: DataStore<ProtoDeviceConfiguration>,
    private val tokenizationProcessor: TokenizationProcessor,
) : ConfigLocalDataSource {
    override suspend fun saveProject(project: Project) {
        projectDataStore.updateData { project.toProto() }
    }

    override suspend fun getProject(): Project = projectDataStore.data.first().toDomain()

    override suspend fun clearProject() {
        projectDataStore.updateData { it.toBuilder().clear().build() }
    }

    override suspend fun saveProjectConfiguration(config: ProjectConfiguration) {
        configDataStore.updateData { config.toProto() }
        // We need to update the device configuration only for the non overwritten fields
        deviceConfigDataStore.updateData { protoDeviceConfiguration ->
            protoDeviceConfiguration.let {
                val proto = it.toBuilder()
                if (!protoDeviceConfiguration.language.isOverwritten) {
                    proto
                        .setLanguage(
                            it.language.toBuilder().setLanguage(config.general.defaultLanguage),
                        ).build()
                    LanguageHelper.language = it.language.language
                }
                proto.build()
            }
        }
    }

    override suspend fun getProjectConfiguration(): ProjectConfiguration = configDataStore.data.first().toDomain()

    override fun watchProjectConfiguration(): Flow<ProjectConfiguration> = configDataStore.data.map(ProtoProjectConfiguration::toDomain)

    override suspend fun clearProjectConfiguration() {
        configDataStore.updateData { it.toBuilder().clear().build() }
    }

    override suspend fun getDeviceConfiguration(): DeviceConfiguration {
        val config = deviceConfigDataStore.data.first().toDomain()
        val tokenizedModules = config.selectedModules.map { moduleId ->
            when (moduleId) {
                is TokenizableString.Raw -> tokenizationProcessor.encrypt(
                    decrypted = moduleId,
                    tokenKeyType = TokenKeyType.ModuleId,
                    project = getProject(),
                )
                is TokenizableString.Tokenized -> moduleId
            }
        }
        config.selectedModules = tokenizedModules
        return config
    }

    override suspend fun updateDeviceConfiguration(update: suspend (t: DeviceConfiguration) -> DeviceConfiguration) {
        deviceConfigDataStore.updateData { currentData ->
            val updatedProto = update(currentData.toDomain()).toProto()
            val updatedProtoBuilder = updatedProto.toBuilder()
            if (updatedProto.language.language != currentData.language.language) {
                updatedProtoBuilder.language =
                    updatedProto.language
                        .toBuilder()
                        .setIsOverwritten(true)
                        .build()
                LanguageHelper.language = updatedProto.language.language
            }
            updatedProtoBuilder.build()
        }
    }

    override suspend fun clearDeviceConfiguration() {
        deviceConfigDataStore.updateData { it.toBuilder().clear().build() }
    }

    override fun storePrivacyNotice(
        projectId: String,
        language: String,
        content: String,
    ) {
        val projectDir = File(filePathForPrivacyNoticeDirectory(projectId))
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
        val privacyNoticeFile = fileForPrivacyNotice(projectId, language)
        privacyNoticeFile.writeText(content)
    }

    override fun getPrivacyNotice(
        projectId: String,
        language: String,
    ): String = fileForPrivacyNotice(projectId, language).readText()

    override fun hasPrivacyNoticeFor(
        projectId: String,
        language: String,
    ): Boolean = fileForPrivacyNotice(projectId, language).exists()

    override fun deletePrivacyNotices() {
        File("$absolutePath${File.separator}$PRIVACY_NOTICE_FOLDER").deleteRecursively()
    }

    private fun fileForPrivacyNotice(
        projectId: String,
        language: String,
    ): File = File(
        filePathForPrivacyNoticeDirectory(projectId),
        "$language.$FILE_TYPE",
    )

    private fun filePathForPrivacyNoticeDirectory(projectId: String): String =
        "$absolutePath${File.separator}$PRIVACY_NOTICE_FOLDER${File.separator}$projectId"

    companion object {
        val defaultProjectConfiguration: ProtoProjectConfiguration =
            ProjectConfiguration(
                id = "123",
                projectId = AuthStore.DEFAULT_PROJECT_ID,
                updatedAt = "",
                general = GeneralConfiguration(
                    modalities = listOf(GeneralConfiguration.Modality.FACE),
                    matchingModalities = listOf(GeneralConfiguration.Modality.FACE),
                    languageOptions = listOf(),
                    defaultLanguage = "en",
                    collectLocation = true,
                    duplicateBiometricEnrolmentCheck = false,
                    settingsPassword = SettingsPasswordConfig.NotSet,
                ),
                face = FaceConfiguration(
                    allowedSDKs = listOf(FaceConfiguration.BioSdk.RANK_ONE),
                    rankOne = FaceConfiguration.FaceSdkConfiguration(
                        nbOfImagesToCapture = 1,
                        qualityThreshold = 0f,
                        imageSavingStrategy = FaceConfiguration.ImageSavingStrategy.NEVER,
                        decisionPolicy = DecisionPolicy(
                            low = 30,
                            medium = 50,
                            high = 70,
                        ),
                        version = "1.0",
                    ),
                ),
                fingerprint = null,
                consent = ConsentConfiguration(
                    programName = "this program",
                    organizationName = "This organization",
                    collectConsent = true,
                    displaySimprintsLogo = true,
                    allowParentalConsent = false,
                    generalPrompt = ConsentConfiguration.ConsentPromptConfiguration(
                        enrolmentVariant = ConsentConfiguration.ConsentEnrolmentVariant.STANDARD,
                        dataSharedWithPartner = false,
                        dataUsedForRAndD = false,
                        privacyRights = true,
                        confirmation = true,
                    ),
                    parentalPrompt = null,
                ),
                identification = IdentificationConfiguration(
                    maxNbOfReturnedCandidates = 10,
                    poolType = IdentificationConfiguration.PoolType.PROJECT,
                ),
                synchronization = SynchronizationConfiguration(
                    frequency = SynchronizationConfiguration.Frequency.ONLY_PERIODICALLY_UP_SYNC,
                    up = UpSynchronizationConfiguration(
                        simprints = UpSynchronizationConfiguration.SimprintsUpSynchronizationConfiguration(
                            kind = UpSynchronizationConfiguration.UpSynchronizationKind.NONE,
                            batchSizes = UpSynchronizationConfiguration.UpSyncBatchSizes.default(),
                            imagesRequireUnmeteredConnection = false,
                        ),
                        coSync = UpSynchronizationConfiguration.CoSyncUpSynchronizationConfiguration(
                            kind = UpSynchronizationConfiguration.UpSynchronizationKind.NONE,
                        ),
                    ),
                    down = DownSynchronizationConfiguration(
                        partitionType = DownSynchronizationConfiguration.PartitionType.USER,
                        maxNbOfModules = 6,
                        moduleOptions = listOf(),
                        maxAge = DEFAULT_DOWN_SYNC_MAX_AGE,
                    ),
                ),
                custom = null,
            ).toProto()
        val defaultDeviceConfiguration: ProtoDeviceConfiguration = DeviceConfiguration(
            language = "",
            selectedModules = listOf(),
            lastInstructionId = "",
        ).toProto()

        private const val PRIVACY_NOTICE_FOLDER = "long-consents"
        private const val FILE_TYPE = "txt"
    }
}
