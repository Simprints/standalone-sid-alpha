package com.simprints.id.activities.checkLogin.openedByIntent

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.simprints.id.activities.IntentKeys
import com.simprints.id.activities.alert.AlertActivity
import com.simprints.id.activities.checkLogin.openedByIntent.CheckLoginFromIntentActivity.Companion.LOGIN_REQUEST_CODE
import com.simprints.id.activities.launch.LaunchActivity
import com.simprints.id.activities.login.LoginActivity
import com.simprints.id.commontesttools.di.DependencyRule.MockRule
import com.simprints.id.commontesttools.di.DependencyRule.ReplaceRule
import com.simprints.id.commontesttools.di.TestAppModule
import com.simprints.id.data.analytics.crashreport.CrashReportManager
import com.simprints.id.data.analytics.eventdata.controllers.local.SessionEventsLocalDbManager
import com.simprints.id.data.db.DbManager
import com.simprints.id.data.db.local.LocalDbManager
import com.simprints.id.data.db.remote.RemoteDbManager
import com.simprints.id.data.prefs.PreferencesManager
import com.simprints.id.data.prefs.PreferencesManagerImpl
import com.simprints.id.testtools.TestApplication
import com.simprints.id.testtools.UnitTestConfig
import com.simprints.id.testtools.state.RobolectricTestMocker
import com.simprints.id.testtools.state.RobolectricTestMocker.setUserLogInState
import com.simprints.id.testtools.state.setupFakeKeyStore
import com.simprints.testtools.common.syntax.anyNotNull
import com.simprints.testtools.unit.robolectric.ShadowAndroidXMultiDex
import com.simprints.testtools.unit.robolectric.assertActivityStarted
import com.simprints.testtools.unit.robolectric.createActivity
import com.simprints.testtools.unit.robolectric.getSharedPreferences
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric.buildActivity
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import javax.inject.Inject

// for O_MR1 = 27, Roboletric hangs around after it calls SharedPrefs.'apply' and then it accesses to
// the SharedPrefs again. https://github.com/robolectric/robolectric/issues/3641
@RunWith(AndroidJUnit4::class)
@Config(
    application = TestApplication::class,
    sdk = [Build.VERSION_CODES.N_MR1], shadows = [ShadowAndroidXMultiDex::class])
class CheckLoginFromIntentActivityTest {

    companion object {
        const val DEFAULT_ACTION = "com.simprints.id.REGISTER"
        const val DEFAULT_PROJECT_ID = "some_project_id"
        const val DEFAULT_PROJECT_SECRET = "some_project_secret"
        const val DEFAULT_REALM_KEY = "enc_some_key"
        const val DEFAULT_USER_ID = "some_user_id"
        const val DEFAULT_MODULE_ID = "some_module_id"
        const val DEFAULT_LEGACY_API_KEY = "96307ff9-873b-45e0-8ef0-b2efd5bef12d"
    }

    private val app = ApplicationProvider.getApplicationContext() as TestApplication

    private lateinit var sharedPrefs: SharedPreferences

    @Inject lateinit var sessionEventsLocalDbManagerMock: SessionEventsLocalDbManager
    @Inject lateinit var remoteDbManagerMock: RemoteDbManager
    @Inject lateinit var localDbManagerMock: LocalDbManager
    @Inject lateinit var crashReportManagerMock: CrashReportManager
    @Inject lateinit var preferences: PreferencesManager
    @Inject lateinit var dbManager: DbManager

    private val module by lazy {
        TestAppModule(app,
            crashReportManagerRule = MockRule,
            localDbManagerRule = MockRule,
            remoteDbManagerRule = MockRule,
            scheduledSessionsSyncManagerRule = MockRule,
            sessionEventsLocalDbManagerRule = MockRule,
            keystoreManagerRule = ReplaceRule { setupFakeKeyStore() })
    }

    @Before
    fun setUp() {
        UnitTestConfig(this, module).fullSetup()

        sharedPrefs = getSharedPreferences(PreferencesManagerImpl.PREF_FILE_NAME)

        RobolectricTestMocker
            .setupLocalAndRemoteManagersForApiTesting(localDbManagerMock, remoteDbManagerMock, sessionEventsLocalDbManagerMock)
            .initLogInStateMock(sharedPrefs, remoteDbManagerMock)

        dbManager.initialiseDb()
    }

    @Test
    fun unknownCallingAppSource_shouldLogEvent() {
        buildActivity(CheckLoginFromIntentActivityWithInvalidCallingPackage::class.java).setup()
        verifyExceptionWasThrownAlongWithNoSessionException(2)
    }

    @Test
    fun knownCallingAppSource_shouldNotLogEvent() {
        val pm = app.packageManager
        pm.setInstallerPackageName("com.app.installed.from.playstore", "com.android.vending")

        buildActivity(CheckLoginFromIntentActivityWithValidCallingPackage::class.java).setup()
        verifyExceptionWasThrownAlongWithNoSessionException(1)
    }

    private fun verifyExceptionWasThrownAlongWithNoSessionException(times: Int) {
        Mockito.verify(crashReportManagerMock, Mockito.times(times)).logExceptionOrThrowable(anyNotNull())
    }

    @Test
    fun validIntentParams_extractParamsForLogin() {

        val intent = createACallingAppIntentWithProjectId()

        val controller = createRoboCheckLoginFromIntentViewActivity(intent).start()
        controller.resume().visible()

        Assert.assertEquals(DEFAULT_PROJECT_ID, preferences.projectId)
        Assert.assertEquals(DEFAULT_USER_ID, preferences.userId)
    }

    @Test
    fun invalidModuleIdInParams_shouldAlertActComeUp() {

        val intent = createACallingAppIntentWithProjectId(moduleId = "invalid module ID with pipes |")

        val controller = createRoboCheckLoginFromIntentViewActivity(intent).start()
        val activity = controller.get() as CheckLoginFromIntentActivity
        controller.visible()

        assertActivityStarted(AlertActivity::class.java, activity)
    }

    @Test
    fun invalidParams_shouldAlertActComeUp() {

        val controller = createRoboCheckLoginFromIntentViewActivity(null).start()
        val activity = controller.get() as CheckLoginFromIntentActivity
        controller.visible()

        assertActivityStarted(AlertActivity::class.java, activity)
    }

    @Test
    fun userIsLogged_shouldLaunchActComeUp() {

        setUserLogInState(true, sharedPrefs)
        val checkLoginIntent = createACallingAppIntentWithLegacyApiKey()
        val activity = startCheckLoginFromIntentActivity(checkLoginIntent)

        assertActivityStarted(LaunchActivity::class.java, activity)
    }

    @Test
    fun userIsNotLogged_shouldLoginActComeUp() {

        setUserLogInState(false, sharedPrefs)
        val checkLoginIntent = createACallingAppIntentWithProjectId()
        val activity = startCheckLoginFromIntentActivity(checkLoginIntent)

        assertActivityStarted(LoginActivity::class.java, activity)
    }

    @Test
    fun userIsNotLoggedAndCallingAppUsesApiLegacyKey_shouldLoginActWithLegacyApiKeyComeUp() {

        setUserLogInState(false, sharedPrefs)
        val checkLoginIntent = createACallingAppIntentWithLegacyApiKey()
        val activity = startCheckLoginFromIntentActivity(checkLoginIntent)
        val sActivity = shadowOf(activity)
        val intent = sActivity.nextStartedActivity
        assertActivityStarted(LoginActivity::class.java, intent)
        assertEquals(DEFAULT_LEGACY_API_KEY, intent.getStringExtra(IntentKeys.loginActivityLegacyProjectIdKey))
    }

    @Test
    fun normalFlowReturnsAResult_shouldForwardItBackToCallingApp() {
        setUserLogInState(true, sharedPrefs)
        val loginIntent = createACallingAppIntentWithProjectId()
        val activity = startCheckLoginFromIntentActivity(loginIntent)
        val sActivity = shadowOf(activity)

        assertFalse(activity.isFinishing)
        val intent = sActivity.nextStartedActivity
        assertActivityStarted(LaunchActivity::class.java, intent)

        sActivity.receiveResult(
            intent,
            Activity.RESULT_OK,
            Intent().putExtra("result", "some_result"))

        assertTrue(activity.isFinishing)
        assertEquals(sActivity.resultCode, Activity.RESULT_OK)
        assertEquals(sActivity.resultIntent.getStringExtra("result"), "some_result")
    }

    @Test
    fun loginSucceed_shouldOpenLaunchActivity() {
        setUserLogInState(false, sharedPrefs)
        val loginIntent = createACallingAppIntentWithProjectId()
        val controller = createRoboCheckLoginFromIntentViewActivity(loginIntent).start()
        val activity = controller.get() as CheckLoginFromIntentActivity
        controller.resume().visible()
        val sActivity = shadowOf(activity)

        assertFalse(activity.isFinishing)
        val intent = sActivity.nextStartedActivity
        assertActivityStarted(LoginActivity::class.java, intent)

        setUserLogInState(true, sharedPrefs)

        sActivity.receiveResult(
            intent,
            LOGIN_REQUEST_CODE,
            Intent())

        controller.resume()
        assertActivityStarted(LaunchActivity::class.java, sActivity)
        assertFalse(activity.isFinishing)
    }

    @Test
    fun loginFailed_shouldCloseApp() {
        setUserLogInState(false, sharedPrefs)
        val loginIntent = createACallingAppIntentWithProjectId()
        val controller = createRoboCheckLoginFromIntentViewActivity(loginIntent).start()
        val activity = controller.get() as CheckLoginFromIntentActivity
        controller.resume().visible()
        val sActivity = shadowOf(activity)

        assertFalse(activity.isFinishing)
        val intent = sActivity.nextStartedActivity
        assertActivityStarted(LoginActivity::class.java, intent)

        sActivity.receiveResult(
            intent,
            LOGIN_REQUEST_CODE,
            Intent())

        controller.resume()
        assertTrue(activity.isFinishing)
    }

    private fun startCheckLoginFromIntentActivity(intent: Intent): CheckLoginFromIntentActivity {

        val controller = createRoboCheckLoginFromIntentViewActivity(intent).start()
        val activity = controller.get() as CheckLoginFromIntentActivity
        controller.resume().visible()
        return activity
    }

    private fun createRoboCheckLoginFromIntentViewActivity(intent: Intent?) =
        createActivity<CheckLoginFromIntentActivity>(intent)

    private fun createACallingAppIntentWithLegacyApiKey(actionString: String = DEFAULT_ACTION,
                                                        legacyApiKey: String = DEFAULT_LEGACY_API_KEY,
                                                        userId: String = DEFAULT_USER_ID,
                                                        moduleId: String = DEFAULT_MODULE_ID): Intent {

        return createACallingAppIntent(actionString, userId, moduleId).also { it.putExtra("apiKey", legacyApiKey) }
    }

    private fun createACallingAppIntentWithProjectId(actionString: String = DEFAULT_ACTION,
                                                     projectId: String = DEFAULT_PROJECT_ID,
                                                     userId: String = DEFAULT_USER_ID,
                                                     moduleId: String = DEFAULT_MODULE_ID): Intent {

        return createACallingAppIntent(actionString, userId, moduleId).also { it.putExtra("projectId", projectId) }
    }

    private fun createACallingAppIntent(actionString: String,
                                        userId: String = "",
                                        moduleId: String = ""): Intent {

        return Intent().apply {
            action = actionString
            putExtra("userId", userId)
            putExtra("moduleId", moduleId)
        }
    }

    class CheckLoginFromIntentActivityWithInvalidCallingPackage : CheckLoginFromIntentActivity() {

        override fun getCallingPackageName(): String {
            return "com.app.installed.manually"
        }
    }

    class CheckLoginFromIntentActivityWithValidCallingPackage : CheckLoginFromIntentActivity() {

        override fun getCallingPackageName(): String {
            return "com.app.installed.from.playstore"
        }
    }
}