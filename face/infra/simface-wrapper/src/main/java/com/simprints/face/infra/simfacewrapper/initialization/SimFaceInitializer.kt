package com.simprints.face.infra.simfacewrapper.initialization

import android.app.Activity
import com.simprints.face.infra.basebiosdk.initialization.FaceBioSdkInitializer
import com.simprints.simface.core.SimFaceConfig
import com.simprints.simface.core.SimFaceFacade
import javax.inject.Inject

class SimFaceInitializer @Inject constructor() : FaceBioSdkInitializer {
    override fun tryInitWithLicense(
        activity: Activity,
        license: String,
    ): Boolean {
        SimFaceFacade.initialize(SimFaceConfig(activity.baseContext))
        return true
    }
}
