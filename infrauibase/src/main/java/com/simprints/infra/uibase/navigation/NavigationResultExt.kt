package com.simprints.infra.uibase.navigation

import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

/**
 * Add lifecycle aware fragment result listener for a provided destination ID for the navigation host controller.
 * This listener acts like bridge between hosting activity and navigation graph.
 *
 * Use this method to handle result from the root fragment of the navigation graph within an activity.
 *
 * Handler will be invoked when the result is set in the calling fragment.
 */
fun <T : Parcelable> FragmentContainerView.handleResult(
    lifecycleOwner: LifecycleOwner,
    @IdRes targetDestinationId: Int,
    handler: (T) -> Unit
) {
    val expectedResultKey = resultName(targetDestinationId)
    getFragment<Fragment>().childFragmentManager
        .setFragmentResultListener(expectedResultKey, lifecycleOwner) { key, resultBundle ->
            resultBundle.getParcelable<T>(key)?.let(handler)
        }
}

/**
 * Add fragment result listener directly to the calling fragment.
 * This function should be used only in fragment tests to verify correct results are being returned.
 */
fun <T : Parcelable> Fragment.handleResultDirectly(@IdRes targetDestinationId: Int, handler: (T) -> Unit) {
    val expectedResultKey = resultName(targetDestinationId)
    setFragmentResultListener(expectedResultKey) { key, resultBundle ->
        resultBundle.getParcelable<T>(key)?.let(handler)
    }
}

/**
 * Add a listener for fragment result only within navigation graph (including sub-graphs).
 *
 * When navigating to a nested graph for result do not use "popUpTo=graph" as it prevents target
 * destination from being added to the backstack and makes result delivery impossible.
 *
 * Handler will be invoked when parent fragment is restored.
 */
@Suppress("UsePropertyAccessSyntax") // compiler is confused by `lifecycle` getter
fun <T : Parcelable> NavController.handleResult(
    lifecycleOwner: LifecycleOwner,
    @IdRes currentDestinationId: Int,
    @IdRes targetDestinationId: Int,
    handler: (T) -> Unit
) {
    // `getCurrentBackStackEntry` doesn't work in case of recovery from the process death when dialog is opened.
    val currentEntry = getBackStackEntry(currentDestinationId)
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            handleResultFromChild(targetDestinationId, currentEntry, handler)
        }
    }
    currentEntry.getLifecycle().addObserver(observer)
    lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            currentEntry.getLifecycle().removeObserver(observer)
        }
    })
}

private fun <T : Parcelable> handleResultFromChild(
    @IdRes childDestinationId: Int,
    currentEntry: NavBackStackEntry,
    handler: (T) -> Unit
) {
    val expectedResultKey = resultName(childDestinationId)

    with(currentEntry.savedStateHandle) {
        if (contains(expectedResultKey)) {
            get<T>(expectedResultKey)?.let(handler)
            remove<T>(expectedResultKey)
        }
    }
}

/**
 * Sets the provided parcelable as a fragment result to be used both
 * within and outside of the navigation graph.
 */
fun <T : Parcelable> NavController.setResult(fragment: Fragment, result: T) {
    val currentDestinationId = currentDestination?.id
    if (currentDestinationId != null) {
        val resultName = resultName(currentDestinationId)

        // Set results into correct navigation stack entry
        previousBackStackEntry?.savedStateHandle?.set(resultName, result)
        // Send result to fragment result listeners
        fragment.setFragmentResult(resultName, bundleOf(resultName to result))
    }
}

/**
 * Same as `setResult()` but also pops current fragment from backstack
 *
 * @return true if the stack was popped at least once
 */
fun <T : Parcelable> NavController.finishWithResult(fragment: Fragment, result: T): Boolean {
    setResult(fragment, result)
    return popBackStack()
}

private fun resultName(resultSourceId: Int) = "result-$resultSourceId"