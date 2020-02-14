package com.simprints.fingerprintscanner.v2.incoming.main.message

import com.simprints.fingerprintscanner.v2.domain.main.message.MainMessage
import com.simprints.fingerprintscanner.v2.domain.main.packet.Packet
import com.simprints.fingerprintscanner.v2.incoming.main.message.accumulators.PacketToMainMessageAccumulator
import com.simprints.fingerprintscanner.v2.tools.accumulator.accumulateAndTakeElements
import io.reactivex.Flowable

fun <R: MainMessage> Flowable<out Packet>.toMainMessageStream(accumulator: PacketToMainMessageAccumulator<R>): Flowable<R> =
    accumulateAndTakeElements(accumulator)