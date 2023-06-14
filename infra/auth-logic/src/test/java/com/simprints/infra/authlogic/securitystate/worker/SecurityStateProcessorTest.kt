package com.simprints.infra.authlogic.securitystate.worker

import com.simprints.infra.authlogic.authenticator.SignerManager
import com.simprints.infra.authlogic.securitystate.models.SecurityState
import com.simprints.infra.authlogic.securitystate.models.SecurityState.Status.COMPROMISED
import com.simprints.infra.authlogic.securitystate.models.SecurityState.Status.PROJECT_ENDED
import com.simprints.infra.authlogic.securitystate.models.SecurityState.Status.RUNNING
import com.simprints.infra.authlogic.securitystate.models.UpSyncEnrolmentRecords
import com.simprints.infra.enrolment.records.EnrolmentRecordManager
import com.simprints.infra.events.EventRepository
import com.simprints.infra.images.ImageRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class SecurityStateProcessorTest {

    @MockK
    lateinit var mockImageRepository: ImageRepository

    @MockK
    lateinit var mockSignerManager: SignerManager

    @MockK
    lateinit var enrolmentRecordManager: EnrolmentRecordManager

    @MockK
    lateinit var eventRepository: EventRepository

    private lateinit var securityStateProcessor: SecurityStateProcessor

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        securityStateProcessor = SecurityStateProcessor(
            mockImageRepository,
            enrolmentRecordManager,
            eventRepository,
            mockSignerManager,
        )
    }

    @Test
    fun `when there is an instruction it should schedule a work to upload the enrolment records`() = runTest {
        val securityState =
            SecurityState(DEVICE_ID, RUNNING, UpSyncEnrolmentRecords("id", listOf("subject1")))

        securityStateProcessor.processSecurityState(securityState)

        coVerify(exactly = 1) { enrolmentRecordManager.upload("id", listOf("subject1")) }
    }

    @Test
    fun withRunningSecurityState_shouldDoNothing() = runTest {
        val status = RUNNING
        val securityState = SecurityState(DEVICE_ID, status)

        securityStateProcessor.processSecurityState(securityState)

        coVerify(exactly = 0) { mockImageRepository.deleteStoredImages() }
        coVerify(exactly = 0) { enrolmentRecordManager.deleteAll() }
        coVerify(exactly = 0) { eventRepository.deleteAll() }
        coVerify(exactly = 0) { mockSignerManager.signOut() }
    }

    @Test
    fun withCompromisedSecurityState_shouldDeleteLocalDataAndSignOut() = runTest {
        val status = COMPROMISED
        val securityState = SecurityState(DEVICE_ID, status)

        securityStateProcessor.processSecurityState(securityState)

        coVerify(exactly = 1) { mockImageRepository.deleteStoredImages() }
        coVerify(exactly = 1) { enrolmentRecordManager.deleteAll() }
        coVerify(exactly = 1) { eventRepository.deleteAll() }
        coVerify(exactly = 1) { mockSignerManager.signOut() }
    }

    @Test
    fun withProjectEndedSecurityState_shouldDeleteLocalDataAndSignOut() = runTest {
        val status = PROJECT_ENDED
        val securityState = SecurityState(DEVICE_ID, status)

        securityStateProcessor.processSecurityState(securityState)

        coVerify(exactly = 1) { mockImageRepository.deleteStoredImages() }
        coVerify(exactly = 1) { enrolmentRecordManager.deleteAll() }
        coVerify(exactly = 1) { eventRepository.deleteAll() }
        coVerify(exactly = 1) { mockSignerManager.signOut() }
    }

    private companion object {
        const val DEVICE_ID = "device-id"
    }

}