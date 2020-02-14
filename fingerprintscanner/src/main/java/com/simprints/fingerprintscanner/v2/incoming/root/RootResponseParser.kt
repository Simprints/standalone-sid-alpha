package com.simprints.fingerprintscanner.v2.incoming.root

import com.simprints.fingerprintscanner.v2.domain.root.RootMessageProtocol
import com.simprints.fingerprintscanner.v2.domain.root.RootMessageType.*
import com.simprints.fingerprintscanner.v2.domain.root.RootResponse
import com.simprints.fingerprintscanner.v2.domain.root.responses.*
import com.simprints.fingerprintscanner.v2.incoming.common.MessageParser

class RootResponseParser : MessageParser<RootResponse> {

    override fun parse(messageBytes: ByteArray): RootResponse =
        try {
            RootMessageProtocol.getDataBytes(messageBytes).let { data ->
                when (RootMessageProtocol.getMessageType(messageBytes)) {
                    ENTER_MAIN_MODE -> EnterMainModeResponse.fromBytes(data)
                    ENTER_CYPRESS_OTA_MODE -> EnterCypressOtaModeResponse.fromBytes(data)
                    ENTER_STM_OTA_MODE -> EnterStmOtaModeResponse.fromBytes(data)
                    GET_VERSION -> GetVersionResponse.fromBytes(data)
                    SET_VERSION -> SetVersionResponse.fromBytes(data)
                }
            }
        } catch (e: Exception) {
            handleExceptionDuringParsing(e)
        }
}