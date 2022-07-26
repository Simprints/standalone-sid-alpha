package com.simprints.id.testtools.di

import android.content.Context
import com.google.android.gms.safetynet.SafetyNetClient
import com.simprints.core.login.LoginInfoManager
import com.simprints.core.network.SimApiClientFactory
import com.simprints.infra.security.keyprovider.SecureLocalDbKeyProvider
import com.simprints.core.sharedpreferences.PreferencesManager
import com.simprints.core.tools.json.JsonHelper
import com.simprints.core.tools.time.TimeHelper
import com.simprints.eventsystem.event.EventRepository
import com.simprints.id.activities.login.tools.LoginActivityHelper
import com.simprints.id.data.consent.longconsent.LongConsentRepository
import com.simprints.id.data.db.common.RemoteDbManager
import com.simprints.id.data.db.project.ProjectRepository
import com.simprints.id.data.db.project.remote.ProjectRemoteDataSource
import com.simprints.id.data.prefs.IdPreferencesManager
import com.simprints.id.data.prefs.RemoteConfigWrapper
import com.simprints.id.di.SecurityModule
import com.simprints.infra.network.url.BaseUrlProvider
import com.simprints.id.secure.*
import com.simprints.id.secure.securitystate.local.SecurityStateLocalDataSource
import com.simprints.id.secure.securitystate.remote.SecurityStateRemoteDataSource
import com.simprints.id.secure.securitystate.repository.SecurityStateRepository
import com.simprints.id.services.securitystate.SecurityStateScheduler
import com.simprints.id.services.sync.SyncManager
import com.simprints.id.services.sync.events.master.EventSyncManager
import com.simprints.testtools.common.di.DependencyRule
import com.simprints.testtools.common.di.DependencyRule.RealRule
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TestSecurityModule(
    private val loginActivityHelperRule: DependencyRule = RealRule,
    private val projectAuthenticatorRule: DependencyRule = RealRule,
    private val authenticationHelperRule: DependencyRule = RealRule,
    private val safetyNetClientRule: DependencyRule = RealRule,
    private val signerManagerRule: DependencyRule = RealRule,
    private val securityStateRepositoryRule: DependencyRule = RealRule
) : SecurityModule() {

    override fun provideSignerManager(
        projectRepository: ProjectRepository,
        remoteDbManager: RemoteDbManager,
        loginInfoManager: LoginInfoManager,
        preferencesManager: PreferencesManager,
        eventSyncManager: EventSyncManager,
        syncManager: SyncManager,
        securityStateScheduler: SecurityStateScheduler,
        longConsentRepository: LongConsentRepository,
        eventRepository: EventRepository,
        baseUrlProvider: BaseUrlProvider,
        remoteConfigWrapper: RemoteConfigWrapper
    ): SignerManager = signerManagerRule.resolveDependency {
        super.provideSignerManager(
            projectRepository,
            remoteDbManager,
            loginInfoManager,
            preferencesManager,
            eventSyncManager,
            syncManager,
            securityStateScheduler,
            longConsentRepository,
            eventRepository,
            baseUrlProvider,
            remoteConfigWrapper
        )
    }

    override fun provideLoginActivityHelper(
        securityStateRepository: SecurityStateRepository,
        jsonHelper: JsonHelper
    ): LoginActivityHelper {
        return loginActivityHelperRule.resolveDependency {
            super.provideLoginActivityHelper(securityStateRepository, jsonHelper)
        }
    }

    override fun provideProjectAuthenticator(
        authManager: AuthManager,
        projectSecretManager: ProjectSecretManager,
        loginInfoManager: LoginInfoManager,
        simApiClientFactory: SimApiClientFactory,
        baseUrlProvider: BaseUrlProvider,
        safetyNetClient: SafetyNetClient,
        secureDataManager: SecureLocalDbKeyProvider,
        projectRepository: ProjectRepository,
        projectRemoteDataSource: ProjectRemoteDataSource,
        signerManager: SignerManager,
        longConsentRepository: LongConsentRepository,
        preferencesManager: IdPreferencesManager,
        attestationManager: AttestationManager,
        authenticationDataManager: AuthenticationDataManager
    ): ProjectAuthenticator {
        return projectAuthenticatorRule.resolveDependency {
            super.provideProjectAuthenticator(
                authManager,
                projectSecretManager,
                loginInfoManager,
                simApiClientFactory,
                baseUrlProvider,
                safetyNetClient,
                secureDataManager,
                projectRepository,
                projectRemoteDataSource,
                signerManager,
                longConsentRepository,
                preferencesManager,
                attestationManager,
                authenticationDataManager
            )
        }
    }

    override fun provideAuthenticationHelper(
        loginInfoManager: LoginInfoManager,
        timeHelper: TimeHelper,
        projectAuthenticator: ProjectAuthenticator,
        eventRepository: EventRepository
    ): AuthenticationHelper {
        return authenticationHelperRule.resolveDependency {
            super.provideAuthenticationHelper(
                loginInfoManager,
                timeHelper,
                projectAuthenticator,
                eventRepository
            )
        }
    }

    override fun provideSafetyNetClient(context: Context): SafetyNetClient {
        return safetyNetClientRule.resolveDependency {
            super.provideSafetyNetClient(context)
        }
    }

    @ExperimentalCoroutinesApi
    override fun provideSecurityStateRepository(
        remoteDataSource: SecurityStateRemoteDataSource,
        localDataSource: SecurityStateLocalDataSource
    ): SecurityStateRepository = securityStateRepositoryRule.resolveDependency {
        super.provideSecurityStateRepository(remoteDataSource, localDataSource)
    }

}