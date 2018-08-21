package com.simprints.id.services.scheduledSync.sessionSync

import androidx.work.Worker
import com.simprints.id.Application
import com.simprints.id.data.analytics.AnalyticsManager
import com.simprints.id.data.analytics.eventData.SessionEventsManager
import com.simprints.id.data.loginInfo.LoginInfoManager
import com.simprints.id.exceptions.safe.session.NoSessionsFoundException
import com.simprints.id.tools.TimeHelper
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

class ScheduledSessionsSync : Worker() {

    @Inject lateinit var sessionEventsManager: SessionEventsManager
    @Inject lateinit var loginInfoManager: LoginInfoManager
    @Inject lateinit var analyticsManager: AnalyticsManager
    @Inject lateinit var timeHelper: TimeHelper

    override fun doWork(): Result {
        val result = LinkedBlockingQueue<Result>()

        if (applicationContext is Application) {
            (applicationContext as Application).component.inject(this)
            uploadSessions(result)
        }

        return result.take()
    }

    private fun uploadSessions(result: LinkedBlockingQueue<Worker.Result>) {
        val signedInProjectId = loginInfoManager.getSignedInProjectIdOrEmpty()

        if (signedInProjectId.isNotEmpty()) {
            sessionEventsManager.syncSessions(signedInProjectId).subscribeBy(onComplete = {
                result.put(Result.SUCCESS)
            }, onError = {
                handleError(it, result)
            })
        }
    }

    private fun handleError(it: Throwable, result: LinkedBlockingQueue<Result>) =
        when (it) {
            is NoSessionsFoundException -> {
            }
            else -> {
                it.printStackTrace()
                analyticsManager.logThrowable(it)
                result.put(Result.FAILURE)
            }
        }
}