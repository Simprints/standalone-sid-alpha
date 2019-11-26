package com.simprints.id.moduleselection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simprints.id.data.analytics.crashreport.CrashReportManager
import com.simprints.id.data.analytics.crashreport.CrashReportTag
import com.simprints.id.data.analytics.crashreport.CrashReportTrigger
import com.simprints.id.data.prefs.PreferencesManager
import com.simprints.id.moduleselection.model.Module

class ModuleRepositoryImpl(
    val preferencesManager: PreferencesManager,
    val crashReportManager: CrashReportManager
): ModuleRepository {

    private val modules = MutableLiveData<List<Module>>()

    override fun getModules(): LiveData<List<Module>> = modules.apply {
        value = buildModulesList()
    }

    override fun updateModules(modules: List<Module>) {
        this.modules.value = modules
        setSelectedModules(modules.filter { it.isSelected })
    }

    override fun getMaxNumberOfModules(): Int = preferencesManager.maxNumberOfModules

    private fun buildModulesList() = preferencesManager.moduleIdOptions.map {
        Module(it, isModuleSelected(it))
    }

    private fun isModuleSelected(moduleName: String): Boolean {
        return preferencesManager.selectedModules.contains(moduleName)
    }

    private fun setSelectedModules(selectedModules: List<Module>) {
        preferencesManager.selectedModules = selectedModules.map { it.name }.toSet()
        logMessageForCrashReport("Modules set to ${preferencesManager.selectedModules}")
        setCrashlyticsKeyForModules()
    }

    private fun setCrashlyticsKeyForModules() {
        crashReportManager.setModuleIdsCrashlyticsKey(preferencesManager.selectedModules)
    }

    private fun logMessageForCrashReport(message: String) {
        crashReportManager.logMessageForCrashReport(
            CrashReportTag.SETTINGS, CrashReportTrigger.UI, message = message
        )
    }

}