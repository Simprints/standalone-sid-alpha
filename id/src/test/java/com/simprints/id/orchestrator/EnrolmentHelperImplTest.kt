package com.simprints.id.orchestrator

import com.simprints.id.commontesttools.DefaultTestConstants.DEFAULT_PROJECT_ID
import com.simprints.id.commontesttools.DefaultTestConstants.defaultSubject
import com.simprints.id.commontesttools.events.CREATED_AT
import com.simprints.id.data.db.event.EventRepository
import com.simprints.id.data.db.event.domain.models.EnrolmentEvent
import com.simprints.id.data.db.event.domain.models.subject.EnrolmentRecordCreationEvent
import com.simprints.id.data.db.events_sync.up.domain.LocalEventQuery
import com.simprints.id.data.db.subject.SubjectRepository
import com.simprints.id.data.db.subject.domain.SubjectAction
import com.simprints.id.data.loginInfo.LoginInfoManager
import com.simprints.id.data.prefs.PreferencesManager
import com.simprints.id.domain.modality.Modality.FINGER
import com.simprints.id.domain.modality.Modes.FINGERPRINT
import com.simprints.id.tools.mockUUID
import com.simprints.id.tools.time.TimeHelper
import com.simprints.testtools.common.coroutines.TestCoroutineRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EnrolmentHelperImplTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @MockK lateinit var subjectRepository: SubjectRepository
    @MockK lateinit var eventRepository: EventRepository
    @MockK lateinit var preferencesManager: PreferencesManager
    @MockK lateinit var loginInfoManager: LoginInfoManager
    @MockK lateinit var timeHelper: TimeHelper

    private lateinit var enrolmentHelper: EnrolmentHelper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)

        enrolmentHelper = EnrolmentHelperImpl(subjectRepository, eventRepository, preferencesManager, loginInfoManager, timeHelper)
        every { timeHelper.now() } returns CREATED_AT
        coEvery { preferencesManager.modalities } returns listOf(FINGER)
        every { loginInfoManager.getSignedInProjectIdOrEmpty()  } returns DEFAULT_PROJECT_ID

        mockUUID()
    }

    @Test
    fun enrol_shouldRegisterEnrolmentEvents() {
        runBlocking {

            enrolmentHelper.enrol(defaultSubject)

            val expectedEnrolmentEvent = EnrolmentEvent(CREATED_AT, defaultSubject.subjectId)
            val expectedEnrolmentRecordCreationEvent = EnrolmentRecordCreationEvent(
                CREATED_AT,
                defaultSubject.subjectId,
                defaultSubject.projectId,
                defaultSubject.moduleId,
                defaultSubject.attendantId,
                listOf(FINGERPRINT),
                EnrolmentRecordCreationEvent.buildBiometricReferences(defaultSubject.fingerprintSamples, defaultSubject.faceSamples))


            coVerify {
                eventRepository.addEventToCurrentSession(expectedEnrolmentEvent)

                eventRepository.addEvent(expectedEnrolmentRecordCreationEvent)
            }
        }
    }

    @Test
    fun enrol_shouldEnrolANewSubject() {
        runBlocking {
            enrolmentHelper.enrol(defaultSubject)

            coVerify(exactly = 1) {
                subjectRepository.performActions(listOf(SubjectAction.Creation(defaultSubject)))
            }
        }
    }

    @Test
    fun enrol_shouldPerformUpload() {
        runBlocking {
            enrolmentHelper.enrol(defaultSubject)

            coVerify(exactly = 1) {
                eventRepository.uploadEvents(LocalEventQuery(projectId = DEFAULT_PROJECT_ID))
            }
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}