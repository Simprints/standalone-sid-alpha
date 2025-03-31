package com.simprints.face.infra.simfacewrapper.detection

import android.content.Context
import android.graphics.Bitmap
import com.simprints.face.infra.basebiosdk.detection.Face
import com.simprints.face.infra.basebiosdk.detection.FaceDetector
import com.simprints.simface.core.SimFaceFacade
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import androidx.core.graphics.scale

class SimFaceDetector @Inject constructor(
    private val simFace: SimFaceFacade,
    @ApplicationContext private val context: Context,
) : FaceDetector {
    companion object {
        const val SIM_FACE_TEMPLATE = "SIM_FACE"
    }

    override fun analyze(bitmap: Bitmap): Face? = runBlocking {

        // TODO PoC - create directory in cache to store temporary files
        //    full path /data/data/com.simprints.id/cache/dumpExtraction/<timestamp>
        val folderName = System.currentTimeMillis().toString()
        val folder = File(context.cacheDir, "/dumpExtraction/$folderName")
        folder.mkdirs()

        // TODO PoC - save scaled bitmap to cache
        FileOutputStream(File(folder, "source.png")).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Load a bitmap image for processing
        val faces = simFace.faceDetectionProcessor.detectFaceBlocking(bitmap)

        // TODO PoC - save detected face objects
        File(folder, "faces.txt").printWriter().use { out ->
            faces.joinToString("\n").let { out.println(it.toString()) }
        }

        val face = faces.getOrNull(0) ?: return@runBlocking null
        if (face.quality < 0.6) return@runBlocking null

        val alignedBitmap = simFace.faceDetectionProcessor.alignFace(bitmap, face.absoluteBoundingBox)
        // TODO PoC - save cropped bitmap to cache
        FileOutputStream(File(folder, "aligned.png")).use { out ->
            alignedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // TODO PoC - save scaled bitmap to cache(as it scales in SimFace)
        FileOutputStream(File(folder, "scaled.png")).use { out ->
            alignedBitmap.scale(112, 112, false).compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // TODO PoC - save template to cache
        val template = simFace.embeddingProcessor.getEmbedding(alignedBitmap)
        File(folder, "template.txt").printWriter().use { out ->
            template.toList().joinToString(", ").let { out.println(it) }
        }


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
