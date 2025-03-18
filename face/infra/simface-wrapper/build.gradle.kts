plugins {
    id("simprints.infra")
}

android {
    namespace = "com.simprints.face.infra.simfacewrapper"
}

dependencies {
    implementation(project(":face:infra:base-bio-sdk"))
    implementation(libs.simface)
    implementation(libs.polyprotect)

    // TensorFlow Lite dependencies
    implementation(libs.face.detection)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
}
