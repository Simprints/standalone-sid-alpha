package com.simprints.fingerprint.activities.refusal

import android.content.Intent
import com.simprints.feature.exitform.ExitFormResult
import com.simprints.feature.exitform.exitFormConfiguration
import com.simprints.feature.exitform.scannerOptions
import com.simprints.feature.exitform.toArgs
import com.simprints.fingerprint.activities.refusal.result.RefusalTaskResult
import com.simprints.fingerprint.data.domain.refusal.RefusalFormReason
import com.simprints.infra.resources.R

/**
 * This is a temporary bridge between new navigation patterns and old activity result-heavy implementations
 */
object RefusalAlertHelper {

    fun refusalArgs() = exitFormConfiguration {
        titleRes = R.string.why_did_you_skip_fingerprinting
        backButtonRes = com.simprints.fingerprint.R.string.button_scan_prints
        visibleOptions = scannerOptions()
    }.toArgs()

    fun handleRefusal(result: ExitFormResult, onBack: () -> Unit = {}, onSubmit: (Intent) -> Unit = {}) {
        val option = result.submittedOption()
        if (option != null) {
            onSubmit(getIntentForResultData(RefusalTaskResult(
                RefusalTaskResult.Action.SUBMIT,
                RefusalTaskResult.Answer(RefusalFormReason.fromExitFormOption(option), result.reason.orEmpty())
            )))
        } else {
            onBack()
        }
    }

    private fun getIntentForResultData(refusalResult: RefusalTaskResult) =
        Intent().putExtra(RefusalTaskResult.BUNDLE_KEY, refusalResult)

}