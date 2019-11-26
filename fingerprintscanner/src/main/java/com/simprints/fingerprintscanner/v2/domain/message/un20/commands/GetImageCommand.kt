package com.simprints.fingerprintscanner.v2.domain.message.un20.commands

import com.simprints.fingerprintscanner.v2.domain.message.un20.Un20Command
import com.simprints.fingerprintscanner.v2.domain.message.un20.models.ImageFormat
import com.simprints.fingerprintscanner.v2.domain.message.un20.models.Un20MessageType

class GetImageCommand(val imageFormat: ImageFormat) : Un20Command(Un20MessageType.GetImage(imageFormat.byte)) {

    companion object {
        fun fromBytes(minorResponseByte: Byte, @Suppress("unused_parameter") data: ByteArray) =
            GetImageCommand(ImageFormat.fromBytes(byteArrayOf(minorResponseByte)))
    }
}