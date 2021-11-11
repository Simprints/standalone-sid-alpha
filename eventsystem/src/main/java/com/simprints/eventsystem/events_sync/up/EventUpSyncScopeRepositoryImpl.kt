package com.simprints.eventsystem.events_sync.up

import com.simprints.core.login.LoginInfoManager
import com.simprints.core.tools.coroutines.DispatcherProvider
import com.simprints.eventsystem.events_sync.up.domain.EventUpSyncOperation
import com.simprints.eventsystem.events_sync.up.domain.EventUpSyncScope.ProjectScope
import com.simprints.eventsystem.events_sync.up.domain.getUniqueKey
import com.simprints.eventsystem.events_sync.up.local.DbEventUpSyncOperationStateDao
import com.simprints.eventsystem.events_sync.up.local.DbEventsUpSyncOperationState.Companion.buildFromEventsUpSyncOperationState
import kotlinx.coroutines.withContext

class EventUpSyncScopeRepositoryImpl(
    val loginInfoManager: LoginInfoManager,
    private val dbEventUpSyncOperationStateDao: DbEventUpSyncOperationStateDao,
    private val dispatcher: DispatcherProvider
) : EventUpSyncScopeRepository {

    override suspend fun getUpSyncScope(): ProjectScope {
        val projectId = loginInfoManager.getSignedInProjectIdOrEmpty()
        val syncScope = ProjectScope(projectId)

        syncScope.operation = refreshState(syncScope.operation)

        return syncScope
    }

    override suspend fun insertOrUpdate(syncScopeOperation: EventUpSyncOperation) {
        withContext(dispatcher.io()) {
            dbEventUpSyncOperationStateDao.insertOrUpdate(buildFromEventsUpSyncOperationState(syncScopeOperation))
        }
    }

    private suspend fun refreshState(upOperation: EventUpSyncOperation): EventUpSyncOperation {
        val op = upOperation.copy()
        val state =
            dbEventUpSyncOperationStateDao.load().toList().firstOrNull {
                it.id == op.getUniqueKey()
            }

        return upOperation.copy(lastSyncTime = state?.lastUpdatedTime, lastState = state?.lastState)
    }

    override suspend fun deleteAll() {
        withContext(dispatcher.io()) {
            dbEventUpSyncOperationStateDao.deleteAll()
        }
    }
}