package com.simprints.face.capture.screens

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.core.DeviceID
import com.simprints.core.livedata.LiveDataEvent
import com.simprints.core.livedata.LiveDataEventWithContent
import com.simprints.core.livedata.send
import com.simprints.core.tools.time.Timestamp
import com.simprints.face.capture.FaceCaptureResult
import com.simprints.face.capture.models.FaceDetection
import com.simprints.face.capture.usecases.BitmapToByteArrayUseCase
import com.simprints.face.capture.usecases.IsUsingAutoCaptureUseCase
import com.simprints.face.capture.usecases.SaveFaceImageUseCase
import com.simprints.face.capture.usecases.SimpleCaptureEventReporter
import com.simprints.face.infra.biosdkresolver.ResolveFaceBioSdkUseCase
import com.simprints.infra.authstore.AuthStore
import com.simprints.infra.config.sync.ConfigManager
import com.simprints.infra.license.LicenseRepository
import com.simprints.infra.license.SaveLicenseCheckEventUseCase
import com.simprints.infra.logging.LoggingConstants.CrashReportTag.FACE_CAPTURE
import com.simprints.infra.logging.Simber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
internal class FaceCaptureViewModel @Inject constructor(
    private val authStore: AuthStore,
    private val configManager: ConfigManager,
    private val saveFaceImage: SaveFaceImageUseCase,
    private val eventReporter: SimpleCaptureEventReporter,
    private val bitmapToByteArray: BitmapToByteArrayUseCase,
    private val licenseRepository: LicenseRepository,
    private val resolveFaceBioSdk: ResolveFaceBioSdkUseCase,
    private val saveLicenseCheckEvent: SaveLicenseCheckEventUseCase,
    private val isUsingAutoCapture: IsUsingAutoCaptureUseCase,
    @DeviceID private val deviceID: String,
) : ViewModel() {
    // Updated in live feedback screen
    var attemptNumber: Int = 0
    var samplesToCapture = 1
    var initialised = false

    var shouldCheckCameraPermissions = AtomicBoolean(true)

    private var faceDetections = listOf<FaceDetection>()

    val recaptureEvent: LiveData<LiveDataEvent>
        get() = _recaptureEvent
    private val _recaptureEvent = MutableLiveData<LiveDataEvent>()

    val exitFormEvent: LiveData<LiveDataEvent>
        get() = _exitFormEvent
    private val _exitFormEvent = MutableLiveData<LiveDataEvent>()

    val unexpectedErrorEvent: LiveData<LiveDataEvent>
        get() = _unexpectedErrorEvent
    private val _unexpectedErrorEvent = MutableLiveData<LiveDataEvent>()

    val finishFlowEvent: LiveData<LiveDataEventWithContent<FaceCaptureResult>>
        get() = _finishFlowEvent
    private val _finishFlowEvent = MutableLiveData<LiveDataEventWithContent<FaceCaptureResult>>()

    val invalidLicense: LiveData<LiveDataEvent>
        get() = _invalidLicense
    private val _invalidLicense = MutableLiveData<LiveDataEvent>()

    val isAutoCaptureEnabled: LiveData<Boolean>
        get() = _isAutoCaptureEnabled
    private val _isAutoCaptureEnabled = MutableLiveData<Boolean>()

    fun setupCapture(samplesToCapture: Int) {
        this.samplesToCapture = samplesToCapture
    }

    fun initFaceBioSdk(activity: Activity) = viewModelScope.launch {
        val initializer = resolveFaceBioSdk().initializer
        !initializer.tryInitWithLicense(activity, "")
    }

    fun setupAutoCapture() = viewModelScope.launch {
        _isAutoCaptureEnabled.postValue(isUsingAutoCapture())
    }

    fun getSampleDetection() = faceDetections.firstOrNull()

    fun flowFinished() {
        Simber.i("Finishing capture flow", tag = FACE_CAPTURE)
        viewModelScope.launch {
            val projectConfiguration = configManager.getProjectConfiguration()
            if (projectConfiguration.face?.imageSavingStrategy?.shouldSaveImage() == true) {
                saveFaceDetections()
            }

            val items = faceDetections.mapIndexed { index, detection ->
                FaceCaptureResult.Item(
                    captureEventId = detection.id,
                    index = index,
                    sample = FaceCaptureResult.Sample(
                        faceId = detection.id,
                        template = detection.face?.template ?: ByteArray(0),
                        imageRef = detection.securedImageRef,
                        format = detection.face?.format ?: "",
                    ),
                )
            }
            val referenceId = UUID.randomUUID().toString()
            eventReporter.addBiometricReferenceCreationEvents(referenceId, items.mapNotNull { it.captureEventId })

            _finishFlowEvent.send(FaceCaptureResult(referenceId, items))
        }
    }

    fun captureFinished(newFaceDetections: List<FaceDetection>) {
        faceDetections = newFaceDetections
    }

    fun handleBackButton() {
        _exitFormEvent.send()
    }

    fun recapture() {
        Simber.i("Starting face recapture flow", tag = FACE_CAPTURE)
        faceDetections = listOf()
        _recaptureEvent.send()
    }

    private fun saveFaceDetections() {
        Simber.i("Saving captures to disk", tag = FACE_CAPTURE)
        faceDetections.forEach { saveImage(it, it.id) }
    }

    private fun saveImage(
        faceDetection: FaceDetection,
        captureEventId: String,
    ) {
        runBlocking {
            faceDetection.securedImageRef = saveFaceImage(bitmapToByteArray(faceDetection.bitmap), captureEventId)
        }
    }

    fun submitError(throwable: Throwable) {
        Simber.e("Face capture failed", throwable, FACE_CAPTURE)
        _unexpectedErrorEvent.send()
    }

    fun addOnboardingComplete(startTime: Timestamp) {
        Simber.i("Face capture onboarding complete", tag = FACE_CAPTURE)
        eventReporter.addOnboardingCompleteEvent(startTime)
    }

    fun addCaptureConfirmationAction(
        startTime: Timestamp,
        isContinue: Boolean,
    ) {
        eventReporter.addCaptureConfirmationEvent(startTime, isContinue)
    }
}
