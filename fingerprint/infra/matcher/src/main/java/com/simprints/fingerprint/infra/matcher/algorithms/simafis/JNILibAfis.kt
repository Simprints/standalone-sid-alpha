package com.simprints.fingerprint.infra.matcher.algorithms.simafis

import com.simprints.fingerprint.infra.matcher.algorithms.simafis.models.SimAfisPerson
import java.nio.ByteBuffer

object JNILibAfis : JNILibAfisInterface {
    init {
        System.loadLibrary("simmatcherwrapper")
        nativeInit()
    }

    external override fun nativeInit(): Boolean

    external override fun getNbCores(): Int

    external override fun verify(probe: ByteBuffer, candidate: ByteBuffer): Float

    external override fun identify(
        probe: SimAfisPerson,
        candidates: List<SimAfisPerson>,
        nbThreads: Int
    ): FloatArray
}