plugins {
    id("simprints.feature")
    id("kotlin-parcelize")
}

android {
    namespace = "com.simprints.feature.consent"
}

dependencies {
    implementation(project(":infraconfig"))
    implementation(project(":infralogin"))
    implementation(project(":infraevents"))
    implementation(project(":featureexitform"))
}