package com.simprints.clientapi.di

import com.simprints.clientapi.activities.commcare.CommCareContract
import com.simprints.clientapi.activities.commcare.CommCarePresenter
import com.simprints.clientapi.activities.errors.ErrorContract
import com.simprints.clientapi.activities.errors.ErrorPresenter
import com.simprints.clientapi.activities.libsimprints.LibSimprintsContract
import com.simprints.clientapi.activities.libsimprints.LibSimprintsPresenter
import com.simprints.clientapi.activities.odk.OdkContract
import com.simprints.clientapi.activities.odk.OdkPresenter
import com.simprints.clientapi.controllers.core.crashreport.ClientApiCrashReportManager
import com.simprints.clientapi.controllers.core.crashreport.ClientApiCrashReportManagerImpl
import com.simprints.clientapi.controllers.core.eventData.ClientApiSessionEventsManager
import com.simprints.clientapi.controllers.core.eventData.ClientApiSessionEventsManagerImpl
import com.simprints.clientapi.data.sharedpreferences.SharedPreferencesManager
import com.simprints.clientapi.data.sharedpreferences.SharedPreferencesManagerImpl
import com.simprints.clientapi.tools.ClientApiTimeHelper
import com.simprints.clientapi.tools.ClientApiTimeHelperImpl
import com.simprints.id.Application
import com.simprints.id.data.analytics.crashreport.CoreCrashReportManager
import com.simprints.id.data.analytics.eventdata.controllers.domain.SessionEventsManager
import com.simprints.id.tools.TimeHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

class KoinInjector {

    companion object {

        var koinModule: Module? = null

        @Synchronized
        fun loadClientApiKoinModules() {
            try {
                buildKoinModule().let {
                    loadKoinModules(it)
                    koinModule = it
                }
            } catch (t: DefinitionOverrideException) {
                // guarding for multiple loadKoinModules calls
                Timber.d("DefinitionOverrideException: modules already loaded")
            }
        }

        fun unloadClientApiKoinModules() {
            try {
                koinModule?.let {
                    unloadKoinModules(it)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        private fun buildKoinModule() =
            module(override = true) {

                // Core Builders
                factory { SessionEventsManager.build(androidApplication() as Application) }
                factory { CoreCrashReportManager.build(androidApplication() as Application) }
                factory { TimeHelper.build(androidApplication() as Application) }

                // Domain
                factory<ClientApiSessionEventsManager> { ClientApiSessionEventsManagerImpl(get(), get()) }
                factory<ClientApiCrashReportManager> { ClientApiCrashReportManagerImpl(get()) }
                factory<ClientApiTimeHelper> { ClientApiTimeHelperImpl(get()) }
                factory<SharedPreferencesManager> { SharedPreferencesManagerImpl(androidContext()) }

                // Presenters
                factory<ErrorContract.Presenter> { (view: ErrorContract.View) ->
                    ErrorPresenter(view, get())
                }
                factory<LibSimprintsContract.Presenter> { (view: LibSimprintsContract.View, action: String?) ->
                    LibSimprintsPresenter(view, action, get(), get())
                }
                factory<OdkContract.Presenter> { (view: OdkContract.View, action: String?) ->
                    OdkPresenter(view, action, get(), get())
                }
                factory<CommCareContract.Presenter> { (view: CommCareContract.View, action: String?) ->
                    CommCarePresenter(view, action, get(), get(), get())
                }

            }
    }
}