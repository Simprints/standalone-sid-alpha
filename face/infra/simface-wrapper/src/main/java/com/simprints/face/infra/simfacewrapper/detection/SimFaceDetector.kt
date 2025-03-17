package com.simprints.face.infra.simfacewrapper.detection

import android.graphics.Bitmap
import com.simprints.face.infra.basebiosdk.detection.Face
import com.simprints.face.infra.basebiosdk.detection.FaceDetector
import com.simprints.simface.core.SimFaceFacade
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SimFaceDetector @Inject constructor(
    private val simFace: SimFaceFacade,
) : FaceDetector {
    companion object {
        const val SIM_FACE_TEMPLATE = "SIM_FACE"
    }

    override fun analyze(bitmap: Bitmap): Face? = runBlocking {
        // Load a bitmap image for processing
        val faces = simFace.faceDetectionProcessor.detectFaceBlocking(bitmap)
        val face = faces.getOrNull(0) ?: return@runBlocking null
        if (face.quality < 0.6) return@runBlocking null

        val template = simFace.embeddingProcessor.getEmbedding(bitmap)

        return@runBlocking Face(
            sourceWidth = bitmap.width,
            sourceHeight = bitmap.height,
            absoluteBoundingBox = face.absoluteBoundingBox,
            yaw = face.yaw,
            roll = face.roll,
            quality = face.quality,
            template = template,
            format = SIM_FACE_TEMPLATE,
        )
    }
}
