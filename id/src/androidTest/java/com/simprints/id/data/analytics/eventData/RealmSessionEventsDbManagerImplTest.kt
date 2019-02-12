package com.simprints.id.data.analytics.eventData

import androidx.test.InstrumentationRegistry
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth
import com.simprints.id.Application
import com.simprints.id.activities.checkLogin.openedByIntent.CheckLoginFromIntentActivity
import com.simprints.id.data.analytics.eventData.controllers.domain.SessionEventsManager
import com.simprints.id.data.analytics.eventData.controllers.local.RealmSessionEventsDbManagerImpl
import com.simprints.id.data.analytics.eventData.controllers.local.SessionEventsLocalDbManager
import com.simprints.id.data.analytics.eventData.models.domain.events.RefusalEvent
import com.simprints.id.data.analytics.eventData.models.domain.session.DatabaseInfo
import com.simprints.id.data.analytics.eventData.models.domain.session.Location
import com.simprints.id.data.db.remote.RemoteDbManager
import com.simprints.id.data.prefs.settings.SettingsPreferencesManager
import com.simprints.id.di.AppModuleForAndroidTests
import com.simprints.id.di.DaggerForAndroidTests
import com.simprints.id.shared.DefaultTestConstants.DEFAULT_REALM_KEY
import com.simprints.id.shared.DependencyRule
import com.simprints.id.shared.PreferencesModuleForAnyTests
import com.simprints.id.shared.whenever
import com.simprints.id.testSnippets.setupRandomGeneratorToGenerateKey
import com.simprints.id.testTemplates.FirstUseLocal
import com.simprints.id.tools.RandomGenerator
import com.simprints.id.tools.TimeHelper
import com.simprints.id.tools.delegates.lazyVar
import com.simprints.libsimprints.FingerIdentifier
import com.simprints.mockscanner.MockBluetoothAdapter
import io.realm.RealmConfiguration
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@SmallTest
class RealmSessionEventsDbManagerImplTest : DaggerForAndroidTests(), FirstUseLocal {

    override var peopleRealmConfiguration: RealmConfiguration? = null
    override var sessionsRealmConfiguration: RealmConfiguration? = null

    private val testProjectId1 = "test_project1"
    private val testProjectId2 = "test_project2"
    private val testProjectId3 = "test_project3"

    @Rule
    @JvmField
    val simprintsActionTestRule = ActivityTestRule(CheckLoginFromIntentActivity::class.java, false, false)

    @Inject lateinit var realmSessionEventsManager: SessionEventsLocalDbManager
    @Inject lateinit var sessionEventsManagerSpy: SessionEventsManager
    @Inject lateinit var settingsPreferencesManagerSpy: SettingsPreferencesManager
    @Inject lateinit var remoteDbManager: RemoteDbManager
    @Inject lateinit var timeHelper: TimeHelper
    @Inject lateinit var randomGeneratorMock: RandomGenerator

    override var preferencesModule: PreferencesModuleForAnyTests by lazyVar {
        PreferencesModuleForAnyTests(settingsPreferencesManagerRule = DependencyRule.SpyRule)
    }

    override var module by lazyVar {
        AppModuleForAndroidTests(
            app,
            randomGeneratorRule = DependencyRule.MockRule,
            localDbManagerRule = DependencyRule.SpyRule,
            remoteDbManagerRule = DependencyRule.SpyRule,
            sessionEventsManagerRule = DependencyRule.SpyRule,
            bluetoothComponentAdapterRule = DependencyRule.ReplaceRule { mockBluetoothAdapter }
        )
    }

    private lateinit var mockBluetoothAdapter: MockBluetoothAdapter
    private val realmForDataEvent
        get() = (realmSessionEventsManager as RealmSessionEventsDbManagerImpl).getRealmInstance().blockingGet()

    @Before
    override fun setUp() {
        app = InstrumentationRegistry.getTargetContext().applicationContext as Application
        super<DaggerForAndroidTests>.setUp()

        testAppComponent.inject(this)

        setupRandomGeneratorToGenerateKey(DEFAULT_REALM_KEY, randomGeneratorMock)
        sessionsRealmConfiguration = FirstUseLocal.defaultSessionRealmConfiguration
        peopleRealmConfiguration = FirstUseLocal.defaultPeopleRealmConfiguration
        app.initDependencies()
        super<FirstUseLocal>.setUp()

        signOut()

        whenever(settingsPreferencesManagerSpy.fingerStatus).thenReturn(hashMapOf(
            FingerIdentifier.LEFT_THUMB to true,
            FingerIdentifier.LEFT_INDEX_FINGER to true))
    }

    @Test
    fun deleteSessions_shouldCleanDb() {
        val sessionOpenProject1Id = createAndSaveOpenSession()
        val sessionCloseProject1Id = createAndSaveCloseSession()
        val sessionCloseProject2Id = createAndSaveCloseSession(testProjectId2)
        createAndSaveCloseSession(testProjectId3)
        createAndSaveCloseSession(testProjectId3)

        sessionEventsManagerSpy.updateSession {
            it.databaseInfo = DatabaseInfo(0)
            it.location = Location(0.0, 0.0)
            it.events.add(RefusalEvent(200, 200, RefusalEvent.Answer.OTHER, "fake_event"))
        }.blockingGet()

        verifyNumberOfSessionsInDb(5, realmForDataEvent)

        realmSessionEventsManager.deleteSessions(projectId = testProjectId3).blockingAwait()
        verifySessionsStoredInDb(sessionOpenProject1Id, sessionCloseProject1Id, sessionCloseProject2Id)

        realmSessionEventsManager.deleteSessions(openSession = true).blockingAwait()
        verifySessionsStoredInDb(sessionCloseProject1Id, sessionCloseProject2Id)

        realmSessionEventsManager.deleteSessions(openSession = false).blockingAwait()

        verifyNumberOfSessionsInDb(0, realmForDataEvent)
        verifyNumberOfEventsInDb(0, realmForDataEvent)
        verifyNumberOfDatabaseInfosInDb(0, realmForDataEvent)
        verifyNumberOfLocationsInDb(0, realmForDataEvent)
        verifyNumberOfDeviceInfosInDb(0, realmForDataEvent)
    }

    private fun verifySessionsStoredInDb(vararg sessionsIds: String) {
        with(realmSessionEventsManager.loadSessions().blockingGet()) {
            val ids = this.map { it.id }
            Truth.assertThat(ids).containsExactlyElementsIn(sessionsIds)
        }
    }

    private fun createAndSaveCloseSession(projectId: String = testProjectId1): String =
        createAndSaveCloseFakeSession(timeHelper, realmSessionEventsManager, projectId)

    private fun createAndSaveOpenSession(projectId: String = testProjectId1): String =
        createAndSaveOpenFakeSession(timeHelper, realmSessionEventsManager, projectId)

    private fun signOut() {
        remoteDbManager.signOutOfRemoteDb()
    }
}