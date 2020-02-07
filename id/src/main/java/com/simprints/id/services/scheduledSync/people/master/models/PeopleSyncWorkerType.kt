package com.simprints.id.services.scheduledSync.people.master.models


enum class PeopleSyncWorkerType {
    DOWN_COUNTER,
    UP_COUNTER,
    UPLOADER,
    DOWNLOADER,
    END_SYNC_REPORTER,
    START_SYNC_REPORTER;

    companion object {

        private const val TAG_PEOPLE_SYNC_WORKER_TYPE = "TAG_PEOPLE_SYNC_WORKER_TYPE_"

        fun tagForType(type: PeopleSyncWorkerType) = "$TAG_PEOPLE_SYNC_WORKER_TYPE${type}"
    }
}