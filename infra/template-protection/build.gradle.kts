plugins {
    id("simprints.infra")
    id("simprints.library.room")
    id("kotlin-parcelize")
}

android {
    namespace = "com.simprints.infra.templateprotection"
}

dependencies {
    api(libs.polyprotect)
}
