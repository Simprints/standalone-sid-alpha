package com.simprints.matcher.screen

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.core.livedata.LiveDataEventWithContent
import com.simprints.core.livedata.send
import com.simprints.core.tools.time.TimeHelper
import com.simprints.matcher.FaceMatchResult
import com.simprints.matcher.FingerprintMatchResult
import com.simprints.matcher.MatchParams
import com.simprints.matcher.MatchResultItem
import com.simprints.matcher.usecases.FaceMatcherUseCase
import com.simprints.matcher.usecases.FingerprintMatcherUseCase
import com.simprints.matcher.usecases.SaveMatchEventUseCase
import com.simprints.infra.logging.Simber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class MatchViewModel @Inject constructor(
    private val faceMatcher: FaceMatcherUseCase,
    private val fingerprintMatcher: FingerprintMatcherUseCase,
    private val saveMatchEvent: SaveMatchEventUseCase,
    private val timeHelper: TimeHelper,
) : ViewModel() {

    val matchState: LiveData<MatchState>
        get() = _matchState
    private val _matchState = MutableLiveData<MatchState>(MatchState.NotStarted)

    val matchResponse: LiveData<LiveDataEventWithContent<Parcelable>>
        get() = _matchResponse
    private val _matchResponse = MutableLiveData<LiveDataEventWithContent<Parcelable>>()

    fun setupMatch(params: MatchParams) = viewModelScope.launch {
        val startTime = timeHelper.now()

        val isFaceMatch = params.isFaceMatch()
        val matcherUseCase = when {
            isFaceMatch -> faceMatcher
            else -> fingerprintMatcher
        }

        val sortedResults = matcherUseCase(
            params,
            onLoadingCandidates = { tag ->
                Simber.tag(tag).i("Loading candidates")
                _matchState.postValue(MatchState.LoadingCandidates)
            },
            onMatching = { tag ->
                Simber.tag(tag).i("Matching probe against candidates")
                _matchState.postValue(MatchState.Matching)
            }
        )

        val maxFilteredResults = sortedResults.take(returnCount)
        val endTime = timeHelper.now()

        saveMatchEvent(
            startTime,
            endTime,
            params,
            sortedResults.size,
            matcherUseCase.matcherName,
            maxFilteredResults
        )

        setMatchState(sortedResults.size, maxFilteredResults)

        _matchResponse.send(when {
            isFaceMatch -> FaceMatchResult(maxFilteredResults)
            else -> FingerprintMatchResult(maxFilteredResults)
        })
    }

    private fun setMatchState(candidatesMatched: Int, results: List<MatchResultItem>) {
        val veryGoodMatches = results.count { veryGoodMatchThreshold <= it.confidence }
        val goodMatches =
            results.count { goodMatchThreshold <= it.confidence && it.confidence < veryGoodMatchThreshold }
        val fairMatches =
            results.count { fairMatchThreshold <= it.confidence && it.confidence < goodMatchThreshold }

        _matchState.postValue(MatchState.Finished(
            candidatesMatched,
            results.size,
            veryGoodMatches,
            goodMatches,
            fairMatches
        ))
    }

    sealed class MatchState {
        data object NotStarted : MatchState()
        data object LoadingCandidates : MatchState()
        data object Matching : MatchState()
        data class Finished(
            val candidatesMatched: Int,
            val returnSize: Int,
            val veryGoodMatches: Int,
            val goodMatches: Int,
            val fairMatches: Int
        ) : MatchState()
    }

    // TODO This configuration should be provided by SDK or project configuration
    //   https://simprints.atlassian.net/browse/CORE-2923
    companion object {
        const val returnCount = 10
        const val veryGoodMatchThreshold = 50.0
        const val goodMatchThreshold = 35.0
        const val fairMatchThreshold = 20.0
        const val matchingEndWaitTimeInMillis = 1000L
    }
}