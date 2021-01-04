package com.simprints.id.data.db.event.remote.models

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.simprints.id.data.db.event.domain.models.ScannerConnectionEvent.ScannerConnectionPayload
import com.simprints.id.data.db.event.domain.models.ScannerConnectionEvent.ScannerConnectionPayload.ScannerGeneration
import com.simprints.id.data.db.event.domain.models.ScannerConnectionEvent.ScannerConnectionPayload.ScannerGeneration.VERO_1
import com.simprints.id.data.db.event.domain.models.ScannerConnectionEvent.ScannerConnectionPayload.ScannerGeneration.VERO_2
import com.simprints.id.data.db.event.domain.models.ScannerConnectionEvent.ScannerConnectionPayload.ScannerInfo
import com.simprints.id.data.db.event.remote.models.ApiScannerConnectionPayload.ApiScannerGeneration

@Keep
@JsonInclude(Include.NON_NULL)
data class ApiScannerConnectionPayload(override val startTime: Long,
                                       override val version: Int,
                                       val scannerInfo: ApiScannerInfo) : ApiEventPayload(ApiEventPayloadType.ScannerConnection, version, startTime) {

    @Keep
    @JsonInclude(Include.NON_NULL)
    class ApiScannerInfo(val scannerId: String,
                         val macAddress: String,
                         val generation: ApiScannerGeneration?,
                         var hardwareVersion: String?) {

        constructor(scannerInfo: ScannerInfo) :
            this(scannerInfo.scannerId, scannerInfo.macAddress,
                scannerInfo.generation.toApiScannerGeneration(), scannerInfo.hardwareVersion)
    }

    constructor(domainPayload: ScannerConnectionPayload) : this(
        domainPayload.createdAt,
        domainPayload.eventVersion,
        ApiScannerInfo(domainPayload.scannerInfo))

    enum class ApiScannerGeneration {
        VERO_1,
        VERO_2
    }
}


fun ScannerGeneration.toApiScannerGeneration() =
    when (this) {
        VERO_1 -> ApiScannerGeneration.VERO_1
        VERO_2 -> ApiScannerGeneration.VERO_2
    }