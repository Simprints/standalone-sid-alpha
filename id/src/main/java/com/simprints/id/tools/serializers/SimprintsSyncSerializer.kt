package com.simprints.id.tools.serializers

import com.simprints.id.data.prefs.settings.SettingsPreferencesManagerImpl
import com.simprints.id.domain.SimprintsSyncSetting

class SimprintsSyncSerializer : Serializer<SimprintsSyncSetting> {

    override fun serialize(value: SimprintsSyncSetting) = value.name

    override fun deserialize(string: String): SimprintsSyncSetting =
        when (string.replace(Regex("[ \n\r\t]"), "")) {
            ALL -> SimprintsSyncSetting.SIM_SYNC_ALL
            ONLY_BIOMETRICS -> SimprintsSyncSetting.SIM_SYNC_ONLY_BIOMETRICS
            ONLY_ANALYTICS -> SimprintsSyncSetting.SIM_SYNC_ONLY_ANALYTICS
            NONE -> SimprintsSyncSetting.SIM_SYNC_NONE
            else -> SettingsPreferencesManagerImpl.SIMPRINTS_SYNC_SETTINGS_DEFAULT
        }

    companion object {
        private const val ALL = "ALL"
        private const val ONLY_BIOMETRICS = "ONLY_BIOMETRICS"
        private const val ONLY_ANALYTICS = "ONLY_ANALYTICS"
        private const val NONE = "NONE"
    }
}