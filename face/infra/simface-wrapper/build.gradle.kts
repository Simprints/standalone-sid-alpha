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
}
