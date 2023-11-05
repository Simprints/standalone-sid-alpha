package com.simprints.infra.config.store

import com.simprints.infra.config.store.models.DeviceConfiguration
import com.simprints.infra.config.store.models.PrivacyNoticeResult
import com.simprints.infra.config.store.models.Project
import com.simprints.infra.config.store.models.ProjectConfiguration
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {

    suspend fun refreshProject(projectId: String): Project
    suspend fun getProject(projectId: String): Project
    suspend fun getConfiguration(): ProjectConfiguration
    suspend fun refreshConfiguration(projectId: String): ProjectConfiguration
    suspend fun getDeviceConfiguration(): DeviceConfiguration
    suspend fun updateDeviceConfiguration(update: suspend (t: DeviceConfiguration) -> DeviceConfiguration)
    suspend fun clearData()
    suspend fun getPrivacyNotice(projectId: String, language: String): Flow<PrivacyNoticeResult>
}