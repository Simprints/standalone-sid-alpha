package com.simprints.id.services

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.simprints.id.Application
import com.simprints.id.commontesttools.DefaultTestConstants.DEFAULT_PROJECT_ID
import com.simprints.id.commontesttools.DefaultTestConstants.DEFAULT_PROJECT_SECRET
import com.simprints.id.commontesttools.DefaultTestConstants.DEFAULT_REALM_KEY
import com.simprints.id.commontesttools.DefaultTestConstants.DEFAULT_USER_ID
import com.simprints.id.commontesttools.di.TestAppModule
import com.simprints.id.commontesttools.state.LoginStateMocker
import com.simprints.id.commontesttools.state.mockSessionEventsManager
import com.simprints.id.commontesttools.state.setupRandomGeneratorToGenerateKey
import com.simprints.id.data.analytics.eventdata.controllers.domain.SessionEventsManager
import com.simprints.id.data.analytics.eventdata.controllers.local.SessionEventsLocalDbManager
import com.simprints.id.data.analytics.eventdata.models.domain.events.GuidSelectionEvent
import com.simprints.id.data.analytics.eventdata.models.domain.session.SessionEvents
import com.simprints.id.data.db.local.models.LocalDbKey
import com.simprints.id.data.db.remote.RemoteDbManager
import com.simprints.id.data.loginInfo.LoginInfoManager
import com.simprints.id.data.prefs.PreferencesManagerImpl
import com.simprints.id.data.secure.SecureDataManager
import com.simprints.id.domain.moduleapi.app.requests.AppIdentityConfirmationRequest
import com.simprints.id.testtools.AndroidTestConfig
import com.simprints.id.tools.RandomGenerator
import com.simprints.testtools.common.di.DependencyRule
import com.simprints.testtools.common.syntax.awaitAndAssertSuccess
import com.simprints.testtools.common.syntax.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

//TODO: Test only GuidSelectionManager. Currently it's testing SessionEventsLocalDbManager.
//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class GuidSelectionManagerTest {
//
//    private val app = ApplicationProvider.getApplicationContext<Application>()
//
//    private val module by lazy {
//        TestAppModule(app,
//            randomGeneratorRule = DependencyRule.ReplaceRule { mock<RandomGenerator>().apply { setupRandomGeneratorToGenerateKey(this) } },
//            sessionEventsLocalDbManagerRule = DependencyRule.MockRule,
//            crashReportManagerRule = DependencyRule.MockRule,
//            secureDataManagerRule = DependencyRule.SpyRule,
//            remoteDbManagerRule = DependencyRule.SpyRule)
//    }
//
//    @Inject lateinit var sessionEventsManager: SessionEventsManager
//    @Inject lateinit var loginInfoManager: LoginInfoManager
//    @Inject lateinit var realmSessionEventsManagerMock: SessionEventsLocalDbManager
//    @Inject lateinit var guidSelectionManager: GuidSelectionManager
//    @Inject lateinit var secureDataManagerSpy: SecureDataManager
//    @Inject lateinit var remoteDbManagerSpy: RemoteDbManager
//
//    private var sessionsInFakeDb = mutableListOf<SessionEvents>()
//
//    @Before
//    fun setUp() {
//        AndroidTestConfig(this, module).fullSetup()
//
//        LoginStateMocker.setupLoginStateFullyToBeSignedIn(
//            app.getSharedPreferences(PreferencesManagerImpl.PREF_FILE_NAME, PreferencesManagerImpl.PREF_MODE),
//            secureDataManagerSpy,
//            remoteDbManagerSpy,
//            DEFAULT_PROJECT_ID,
//            DEFAULT_USER_ID,
//            DEFAULT_PROJECT_SECRET,
//            LocalDbKey(
//                DEFAULT_PROJECT_ID,
//                DEFAULT_REALM_KEY),
//            "token")
//
//        mockSessionEventsManager(realmSessionEventsManagerMock, sessionsInFakeDb)
//    }
//
//    @Test
//    fun testWithStartedService() {
//        var session = sessionEventsManager.createSession("").blockingGet()
//
//        sessionEventsManager.updateSession {
//            it.projectId = loginInfoManager.getSignedInProjectIdOrEmpty()
//        }.blockingGet()
//
//        val request = AppIdentityConfirmationRequest(
//            DEFAULT_PROJECT_ID,
//            session.id,
//            "some_guid_confirmed")
//
//        guidSelectionManager
//            .handleIdentityConfirmationRequest(request)
//            .test()
//            .awaitAndAssertSuccess()
//
//        session = realmSessionEventsManagerMock.loadSessionById(session.id).blockingGet()
//        val potentialGuidSelectionEvent = session.events.filterIsInstance(GuidSelectionEvent::class.java).firstOrNull()
//        Assert.assertNotNull(potentialGuidSelectionEvent)
//        Assert.assertEquals(potentialGuidSelectionEvent?.selectedId, "some_guid_confirmed")
//    }
//}