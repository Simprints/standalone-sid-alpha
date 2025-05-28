package com.simprints.face.infra.simfacewrapper

import com.simprints.face.infra.basebiosdk.detection.FaceDetector
import com.simprints.face.infra.basebiosdk.initialization.FaceBioSdkInitializer
import com.simprints.face.infra.basebiosdk.matching.FaceMatcher
import com.simprints.face.infra.simfacewrapper.detection.SimFaceDetector
import com.simprints.face.infra.simfacewrapper.initialization.SimFaceInitializer
import com.simprints.face.infra.simfacewrapper.matching.SimFaceMatcher
import com.simprints.simface.core.SimFace
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SimFaceProviderModule {
    @Provides
    @Singleton
    fun provideSimFace(): SimFace = SimFace()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SimFaceModule {
    @Binds
    abstract fun provideSimFaceSdkInitializer(impl: SimFaceInitializer): FaceBioSdkInitializer

    @Binds
    abstract fun provideSimFaceDetector(impl: SimFaceDetector): FaceDetector

    @Binds
    abstract fun provideSimFaceMatcher(impl: SimFaceMatcher): FaceMatcher
}
