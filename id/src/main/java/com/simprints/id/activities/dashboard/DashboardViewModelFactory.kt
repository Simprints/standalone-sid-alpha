package com.simprints.id.activities.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simprints.id.activities.dashboard.cards.project.repository.DashboardProjectDetailsRepository
import com.simprints.id.activities.dashboard.cards.sync.DashboardSyncCardStateRepository

class DashboardViewModelFactory(
    private val projectDetailsRepository: DashboardProjectDetailsRepository,
    private val syncCardStateRepository: DashboardSyncCardStateRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {

            return DashboardViewModel(
                projectDetailsRepository,
                syncCardStateRepository
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
