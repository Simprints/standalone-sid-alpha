package com.simprints.fingerprint.activities.collect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.core.livedata.LiveDataEvent
import com.simprints.core.livedata.LiveDataEventWithContent
import com.simprints.core.tools.EncodingUtils
import com.simprints.fingerprint.activities.alert.FingerprintAlert
import com.simprints.fingerprint.activities.collect.domain.FingerOrderDeterminer
import com.simprints.fingerprint.activities.collect.domain.ScanConfig
import com.simprints.fingerprint.activities.collect.state.CollectFingerprintsState
import com.simprints.fingerprint.activities.collect.state.FingerCollectionState
import com.simprints.fingerprint.activities.collect.state.ScanResult
import com.simprints.fingerprint.controllers.core.crashreport.FingerprintCrashReportManager
import com.simprints.fingerprint.controllers.core.crashreport.FingerprintCrashReportTag
import com.simprints.fingerprint.controllers.core.crashreport.FingerprintCrashReportTrigger
import com.simprints.fingerprint.controllers.core.eventData.FingerprintSessionEventsManager
import com.simprints.fingerprint.controllers.core.eventData.model.FingerprintCaptureEvent
import com.simprints.fingerprint.controllers.core.image.FingerprintImageManager
import com.simprints.fingerprint.controllers.core.preferencesManager.FingerprintPreferencesManager
import com.simprints.fingerprint.controllers.core.timehelper.FingerprintTimeHelper
import com.simprints.fingerprint.data.domain.fingerprint.FingerIdentifier
import com.simprints.fingerprint.data.domain.fingerprint.Fingerprint
import com.simprints.fingerprint.data.domain.images.FingerprintImageRef
import com.simprints.fingerprint.data.domain.images.SaveFingerprintImagesStrategy
import com.simprints.fingerprint.data.domain.images.deduceFileExtension
import com.simprints.fingerprint.exceptions.unexpected.FingerprintUnexpectedException
import com.simprints.fingerprint.scanner.ScannerManager
import com.simprints.fingerprint.scanner.domain.AcquireImageResponse
import com.simprints.fingerprint.scanner.domain.CaptureFingerprintResponse
import com.simprints.fingerprint.scanner.domain.ScannerTriggerListener
import com.simprints.fingerprint.scanner.exceptions.safe.NoFingerDetectedException
import com.simprints.fingerprint.scanner.exceptions.safe.ScannerDisconnectedException
import com.simprints.fingerprint.scanner.exceptions.safe.ScannerOperationInterruptedException
import com.simprints.fingerprint.tools.livedata.postEvent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.concurrent.schedule
import kotlin.math.min

class CollectFingerprintsViewModel(
    private val scannerManager: ScannerManager,
    private val fingerprintPreferencesManager: FingerprintPreferencesManager,
    private val imageManager: FingerprintImageManager,
    private val crashReportManager: FingerprintCrashReportManager,
    private val timeHelper: FingerprintTimeHelper,
    private val sessionEventsManager: FingerprintSessionEventsManager,
    private val fingerOrderDeterminer: FingerOrderDeterminer
) : ViewModel() {

    val state = MutableLiveData<CollectFingerprintsState>()
    fun state() = state.value
        ?: throw IllegalStateException("No state available in CollectFingerprintsViewModel")

    private fun updateState(block: CollectFingerprintsState.() -> Unit) {
        state.postValue(state.value?.apply { block() })
    }

    private fun updateFingerState(block: FingerCollectionState.() -> FingerCollectionState) {
        updateState {
            fingerStates = fingerStates.toMutableList().also {
                it[currentFingerIndex] = it[currentFingerIndex].run { block() }
            }.toList()
        }
    }

    val vibrate = MutableLiveData<LiveDataEvent>()
    val noFingersScannedToast = MutableLiveData<LiveDataEvent>()
    val launchAlert = MutableLiveData<LiveDataEventWithContent<FingerprintAlert>>()
    val launchReconnect = MutableLiveData<LiveDataEvent>()
    val finishWithFingerprints = MutableLiveData<LiveDataEventWithContent<List<Fingerprint>>>()

    private lateinit var originalFingerprintsToCapture: List<FingerIdentifier>
    private val captureEventIds: MutableMap<FingerIdentifier, String> = mutableMapOf()
    private var lastCaptureStartedAt: Long = 0
    private var scanningTask: Disposable? = null
    private var imageTransferTask: Disposable? = null

    private val scannerTriggerListener = ScannerTriggerListener {
        viewModelScope.launch(context = Dispatchers.Main) {
            if (state().isShowingConfirmDialog) {
                logScannerMessageForCrashReport("Scanner trigger clicked for confirm dialog")
                handleConfirmFingerprintsAndContinue()
            } else {
                logScannerMessageForCrashReport("Scanner trigger clicked for scanning")
                handleScanButtonPressed()
            }
        }
    }

    fun start(fingerprintsToCapture: List<FingerIdentifier>) {
        this.originalFingerprintsToCapture = fingerprintsToCapture
        setStartingState()
    }

    private fun setStartingState() {
        state.value = CollectFingerprintsState(originalFingerprintsToCapture
            .map { FingerCollectionState.NotCollected(it) }
            .let { fingerOrderDeterminer.sortedUsingCaptureOrder(it) { id } })
    }

    fun isImageTransferRequired(): Boolean =
        when (fingerprintPreferencesManager.saveFingerprintImagesStrategy) {
            SaveFingerprintImagesStrategy.NEVER -> false
            SaveFingerprintImagesStrategy.WSQ_15 -> true
        }

    fun updateSelectedFinger(index: Int) {
        scannerManager.scanner { setUiIdle() }.doInBackground()
        updateState {
            isAskingRescan = false
            isShowingSplashScreen = false
            currentFingerIndex = index
        }
    }

    private fun nudgeToNextFinger() {
        with(state()) {
            if (currentFingerIndex < fingerStates.size - 1) {
                timeHelper.newTimer().schedule(AUTO_SWIPE_DELAY) {
                    updateSelectedFinger(currentFingerIndex + 1)
                }
            }
        }
    }

    fun handleScanButtonPressed() {
        val fingerState = state().currentFingerState()
        if (fingerState is FingerCollectionState.Collected && fingerState.scanResult.isGoodScan()
            && !state().isAskingRescan) {
            updateState { isAskingRescan = true }
        } else {
            updateState { isAskingRescan = false }
            if (!isBusyForScanning()) {
                toggleScanning()
            }
        }
    }

    private fun isBusyForScanning(): Boolean = with(state()) {
        currentFingerState() is FingerCollectionState.TransferringImage ||
            isShowingConfirmDialog || isShowingSplashScreen
    }

    private fun toggleScanning() {
        when (state().currentFingerState()) {
            is FingerCollectionState.Scanning -> cancelScanning()
            is FingerCollectionState.TransferringImage -> { /* do nothing */
            }
            is FingerCollectionState.NotCollected,
            is FingerCollectionState.Skipped,
            is FingerCollectionState.NotDetected,
            is FingerCollectionState.Collected -> startScanning()
        }
    }

    private fun cancelScanning() {
        updateFingerState { toNotCollected() }
        scanningTask?.dispose()
        imageTransferTask?.dispose()
    }

    private fun startScanning() {
        updateFingerState { toScanning() }
        lastCaptureStartedAt = timeHelper.now()
        scanningTask?.dispose()
        scanningTask = scannerManager.scanner { setUiIdle() }
            .andThen(scannerManager.scanner<CaptureFingerprintResponse> {
                captureFingerprint(
                    fingerprintPreferencesManager.captureFingerprintStrategy,
                    scanningTimeoutMs.toInt(),
                    ScanConfig.qualityThreshold
                )
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::handleCaptureSuccess,
                onError = ::handleScannerCommunicationsError
            )
    }

    private fun handleCaptureSuccess(captureFingerprintResponse: CaptureFingerprintResponse) {
        val scanResult = ScanResult(captureFingerprintResponse.imageQualityScore, captureFingerprintResponse.template, null)
        vibrate.postEvent()
        if (shouldProceedToImageTransfer(scanResult.qualityScore)) {
            updateFingerState { toTransferringImage(scanResult) }
            proceedToImageTransfer()
        } else {
            updateFingerState { toCollected(scanResult) }
            handleCaptureFinished()
        }
    }

    private fun shouldProceedToImageTransfer(quality: Int) =
        isImageTransferRequired() &&
            (quality >= ScanConfig.qualityThreshold || tooManyBadScans(state().currentFingerState(), plusBadScan = true))

    private fun proceedToImageTransfer() {
        imageTransferTask?.dispose()
        imageTransferTask = scannerManager.onScanner { acquireImage(fingerprintPreferencesManager.saveFingerprintImagesStrategy) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::handleImageTransferSuccess,
                onError = ::handleScannerCommunicationsError
            )
    }

    private fun handleImageTransferSuccess(acquireImageResponse: AcquireImageResponse) {
        vibrate.postEvent()
        updateFingerState { toCollected(acquireImageResponse.imageBytes) }
        handleCaptureFinished()
    }

    private fun handleCaptureFinished() {
        with(state()) {
            logUiMessageForCrashReport("Finger scanned - ${currentFingerState().id} - ${currentFingerState()}")
            addCaptureEventInSession()
            if (fingerHasSatisfiedTerminalCondition(state().currentFingerState())) {
                resolveFingerTerminalConditionTriggered()
            }
        }
    }

    private fun addCaptureEventInSession() {
        with(state()) {
            val fingerState = currentFingerState()
            val captureEvent = FingerprintCaptureEvent(
                lastCaptureStartedAt,
                timeHelper.now(),
                fingerState.id,
                ScanConfig.qualityThreshold,
                FingerprintCaptureEvent.buildResult(fingerState),
                (fingerState as? FingerCollectionState.Collected)?.scanResult?.let {
                    FingerprintCaptureEvent.Fingerprint(fingerState.id, it.qualityScore, EncodingUtils.byteArrayToBase64(it.template))
                }
            )
            captureEventIds[fingerState.id] = captureEvent.id
            sessionEventsManager.addEventInBackground(captureEvent)
        }
    }

    private fun resolveFingerTerminalConditionTriggered() {
        with(state()) {
            if (isScanningEndStateAchieved()) {
                logUiMessageForCrashReport("Confirm fingerprints dialog shown")
                updateState { isShowingConfirmDialog = true }
            } else if (currentFingerState().let {
                    it is FingerCollectionState.Collected && it.scanResult.isGoodScan()
                }) {
                nudgeToNextFinger()
            } else {
                if (haveNotExceedMaximumNumberOfFingersToAutoAdd()) {
                    showSplashAndNudge(addNewFinger = true)
                } else if (!isOnLastFinger()) {
                    showSplashAndNudge(addNewFinger = false)
                }
            }
        }
    }

    private fun showSplashAndNudge(addNewFinger: Boolean) {
        updateState { isShowingSplashScreen = true }
        timeHelper.newTimer().schedule(TRY_DIFFERENT_FINGER_SPLASH_DELAY) {
            if (addNewFinger) handleAutoAddFinger()
            nudgeToNextFinger()
        }
    }

    private fun handleAutoAddFinger() {
        updateState {
            val nextPriorityFingerId = fingerOrderDeterminer.determineNextPriorityFinger(fingerStates.map { it.id })
            if (nextPriorityFingerId != null) {
                val newFingerState = FingerCollectionState.NotCollected(nextPriorityFingerId)
                fingerStates = fingerOrderDeterminer.sortedUsingCaptureOrder(fingerStates + listOf(newFingerState)) { id }
            }
        }
    }

    private fun handleScannerCommunicationsError(e: Throwable) {
        when (e) {
            is ScannerOperationInterruptedException -> {
                updateFingerState { toNotCollected() }
            }
            is ScannerDisconnectedException -> {
                updateFingerState { toNotCollected() }
                launchReconnect.postEvent()
            }
            is NoFingerDetectedException -> handleNoFingerDetected()
            else -> {
                updateFingerState { toNotCollected() }
                crashReportManager.logExceptionOrSafeException(e)
                Timber.e(e)
                launchAlert.postEvent(FingerprintAlert.UNEXPECTED_ERROR)
            }
        }
    }

    private fun handleNoFingerDetected() {
        vibrate.postEvent()
        updateFingerState { toNotDetected() }
        addCaptureEventInSession()
    }

    fun handleMissingFingerButtonPressed() {
        updateFingerState { toSkipped() }
        lastCaptureStartedAt = timeHelper.now()
        addCaptureEventInSession()
        resolveFingerTerminalConditionTriggered()
    }

    private fun isScanningEndStateAchieved(): Boolean = with(state()) {
        if (everyActiveFingerHasSatisfiedTerminalCondition()) {
            if (weHaveTheMinimumNumberOfAnyQualityScans() || weHaveTheMinimumNumberOfGoodScans()) {
                return true
            }
        }
        return false
    }

    private fun CollectFingerprintsState.everyActiveFingerHasSatisfiedTerminalCondition(): Boolean =
        fingerStates.all { fingerHasSatisfiedTerminalCondition(it) }

    private fun tooManyBadScans(fingerState: FingerCollectionState, plusBadScan: Boolean): Boolean =
        when (fingerState) {
            is FingerCollectionState.Scanning -> fingerState.numberOfBadScans
            is FingerCollectionState.TransferringImage -> fingerState.numberOfBadScans
            is FingerCollectionState.NotDetected -> fingerState.numberOfBadScans
            is FingerCollectionState.Collected -> fingerState.numberOfBadScans
            else -> 0
        } >= numberOfBadScansRequiredToAutoAddNewFinger - if (plusBadScan) 1 else 0

    private fun CollectFingerprintsState.haveNotExceedMaximumNumberOfFingersToAutoAdd() =
        fingerStates.size < maximumTotalNumberOfFingersForAutoAdding

    private fun CollectFingerprintsState.weHaveTheMinimumNumberOfGoodScans(): Boolean =
        fingerStates.filter {
            it is FingerCollectionState.Collected && it.scanResult.isGoodScan()
        }.size >= min(targetNumberOfGoodScans, numberOfOriginalFingers())

    private fun CollectFingerprintsState.weHaveTheMinimumNumberOfAnyQualityScans() =
        fingerStates.filter {
            fingerHasSatisfiedTerminalCondition(it)
        }.size >= maximumTotalNumberOfFingersForAutoAdding

    private fun numberOfOriginalFingers() = originalFingerprintsToCapture.toSet().size

    private fun fingerHasSatisfiedTerminalCondition(fingerState: FingerCollectionState) =
        fingerState is FingerCollectionState.Collected &&
            (tooManyBadScans(fingerState, plusBadScan = false) || fingerState.scanResult.isGoodScan())
            || fingerState is FingerCollectionState.Skipped

    fun handleConfirmFingerprintsAndContinue() {
        val collectedFingers = state().fingerStates
            .mapNotNull { it as? FingerCollectionState.Collected }

        if (collectedFingers.isEmpty()) {
            noFingersScannedToast.postEvent()
            handleRestart()
        } else {
            saveImagesAndProceedToFinish(collectedFingers)
        }
    }

    private fun saveImagesAndProceedToFinish(collectedFingers: List<FingerCollectionState.Collected>) {
        runBlocking {
            val imageRefs = collectedFingers.map { collectedFinger ->
                saveImageIfExists(collectedFinger)
            }
            val domainFingerprints = collectedFingers.zip(imageRefs) { collectedFinger, imageRef ->
                Fingerprint(collectedFinger.id, collectedFinger.scanResult.template).also { it.imageRef = imageRef }
            }
            finishWithFingerprints.postEvent(domainFingerprints)
        }
    }

    private suspend fun saveImageIfExists(collectedFinger: FingerCollectionState.Collected): FingerprintImageRef? {
        val captureEventId = captureEventIds[collectedFinger.id]

        if (collectedFinger.scanResult.image != null && captureEventId != null) {
            return imageManager.save(collectedFinger.scanResult.image, captureEventId,
                fingerprintPreferencesManager.saveFingerprintImagesStrategy.deduceFileExtension())
        } else if (collectedFinger.scanResult.image != null && captureEventId == null) {
            crashReportManager.logExceptionOrSafeException(FingerprintUnexpectedException("Could not save fingerprint image because of null capture ID"))
        }
        return null
    }

    fun handleRestart() {
        setStartingState()
    }

    fun handleOnResume() {
        scannerManager.onScanner { registerTriggerListener(scannerTriggerListener) }
    }

    fun handleOnPause() {
        scannerManager.onScanner { unregisterTriggerListener(scannerTriggerListener) }
    }

    fun handleOnBackPressed() {
        if (state().currentFingerState().isCommunicating()) {
            cancelScanning()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelScanning()
        scannerManager.scanner { disconnect() }.doInBackground()
    }

    private fun Completable.doInBackground() =
        subscribeOn(Schedulers.io()).subscribeBy(onComplete = {}, onError = {})

    fun logUiMessageForCrashReport(message: String) {
        Timber.d(message)
        crashReportManager.logMessageForCrashReport(FingerprintCrashReportTag.FINGER_CAPTURE, FingerprintCrashReportTrigger.UI, message = message)
    }

    private fun logScannerMessageForCrashReport(message: String) {
        Timber.d(message)
        crashReportManager.logMessageForCrashReport(FingerprintCrashReportTag.FINGER_CAPTURE, FingerprintCrashReportTrigger.SCANNER_BUTTON, message = message)
    }

    companion object {
        const val targetNumberOfGoodScans = 2
        const val maximumTotalNumberOfFingersForAutoAdding = 4
        const val numberOfBadScansRequiredToAutoAddNewFinger = 3

        const val scanningTimeoutMs = 3000L
        const val imageTransferTimeoutMs = 3000L

        const val AUTO_SWIPE_DELAY: Long = 500

        const val TRY_DIFFERENT_FINGER_SPLASH_DELAY: Long = 2000
    }
}