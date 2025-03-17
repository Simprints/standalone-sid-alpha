package com.simprints.face.infra.simfacewrapper

import android.content.Context
import com.simprints.face.infra.basebiosdk.detection.FaceDetector
import com.simprints.face.infra.basebiosdk.initialization.FaceBioSdkInitializer
import com.simprints.face.infra.basebiosdk.matching.FaceMatcher
import com.simprints.face.infra.simfacewrapper.detection.SimFaceDetector
import com.simprints.face.infra.simfacewrapper.initialization.SimFaceInitializer
import com.simprints.face.infra.simfacewrapper.matching.SimFaceMatcher
import com.simprints.simface.core.SimFaceConfig
import com.simprints.simface.core.SimFaceFacade
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SimFaceProviderModule {
    @Provides
    fun provideSimFaceFacade(
        @ApplicationContext context: Context,
    ): SimFaceFacade {
        SimFaceFacade.initialize(SimFaceConfig(context))
        return SimFaceFacade.getInstance()
    }
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
