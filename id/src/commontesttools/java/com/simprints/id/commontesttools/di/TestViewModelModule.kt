package com.simprints.id.commontesttools.di

import com.simprints.id.activities.dashboard.DashboardViewModelFactory
import com.simprints.id.activities.dashboard.cards.daily_activity.repository.DashboardDailyActivityRepository
import com.simprints.id.activities.dashboard.cards.project.repository.DashboardProjectDetailsRepository
import com.simprints.id.activities.dashboard.cards.sync.DashboardSyncCardStateRepository
import com.simprints.id.activities.login.viewmodel.LoginViewModelFactory
import com.simprints.id.di.ViewModelModule
import com.simprints.id.secure.AuthenticationHelper
import com.simprints.testtools.common.di.DependencyRule

class TestViewModelModule(
    private val dashboardViewModelFactoryRule: DependencyRule = DependencyRule.RealRule,
    private val loginViewModelFactoryRule: DependencyRule = DependencyRule.RealRule
) : ViewModelModule() {

    override fun provideDashboardViewModelFactory(
        projectDetailsRepository: DashboardProjectDetailsRepository,
        syncCardStateRepository: DashboardSyncCardStateRepository,
        dailyActivityRepository: DashboardDailyActivityRepository
    ): DashboardViewModelFactory {
        return dashboardViewModelFactoryRule.resolveDependency {
            super.provideDashboardViewModelFactory(projectDetailsRepository, syncCardStateRepository, dailyActivityRepository)
        }
    }

    override fun provideLoginViewModelFactory(authenticationHelper: AuthenticationHelper): LoginViewModelFactory {
        return loginViewModelFactoryRule.resolveDependency {
            super.provideLoginViewModelFactory(authenticationHelper)
        }
    }
}