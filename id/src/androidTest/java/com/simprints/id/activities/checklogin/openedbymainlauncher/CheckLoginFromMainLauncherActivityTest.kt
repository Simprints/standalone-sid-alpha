package com.simprints.id.activities.checklogin.openedbymainlauncher

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.simprints.id.activities.checkLogin.openedByMainLauncher.CheckLoginFromMainLauncherActivity
import com.simprints.id.activities.checkLogin.openedByMainLauncher.CheckLoginFromMainLauncherPresenter
import com.simprints.id.activities.dashboard.DashboardActivity
import com.simprints.id.activities.requestLogin.RequestLoginActivity
import com.simprints.infra.login.LoginManager
import com.simprints.infra.security.SecurityManager
import com.simprints.testtools.unit.robolectric.assertActivityStarted
import com.simprints.testtools.unit.robolectric.createActivity
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class CheckLoginFromMainLauncherActivityTest {

    @Inject
    lateinit var secureDataManager: SecurityManager

    val loginManager: LoginManager = mockk()

    @Test
    fun appNotSignedInFirebase_shouldRequestLoginActComeUp() {
        every { loginManager.isSignedIn(any(), any()) } returns false
        startCheckLoginAndCheckNextActivity(RequestLoginActivity::class.java)
    }

    @Test
    fun projectIdEmpty_shouldRequestLoginActComeUp() {
        every { loginManager.getSignedInProjectIdOrEmpty() } returns ""
        startCheckLoginAndCheckNextActivity(RequestLoginActivity::class.java)
    }

    @Test
    fun userIdEmpty_shouldRequestLoginActComeUp() {
        every { loginManager.getSignedInUserIdOrEmpty() } returns ""
        startCheckLoginAndCheckNextActivity(RequestLoginActivity::class.java)
    }

    @Test
    fun userIsLogged_shouldDashboardActComeUp() {
        every { loginManager.isSignedIn(any(), any()) } returns true
        every { loginManager.getSignedInProjectIdOrEmpty() } returns "project"
        every { loginManager.getSignedInUserIdOrEmpty() } returns "user"


        startCheckLoginAndCheckNextActivity(DashboardActivity::class.java)
    }

    private fun startCheckLoginAndCheckNextActivity(clazzNextActivity: Class<out Activity>) {
        val controller = createActivity<CheckLoginFromMainLauncherActivity>()
        val activity = controller.get()
        (activity.viewPresenter as CheckLoginFromMainLauncherPresenter).loginManager = loginManager
        controller.resume().visible()
        assertActivityStarted(clazzNextActivity, activity)
    }
}