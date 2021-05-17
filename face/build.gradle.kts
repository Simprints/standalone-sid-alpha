plugins {
    id("com.android.dynamic-feature")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("org.sonarqube")
}

apply {
    from("${rootDir}${File.separator}buildSrc${File.separator}build_config.gradle")
}

sonarqube {
    properties {
        property("sonar.sources", "src/main/java")
    }
}


android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments(mapOf(Pair("clearPackageData", "true")))
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }

    buildFeatures.viewBinding = true
}

repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.google.com")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven(url = "https://s3.amazonaws.com/repo.commonsware.com")
}

dependencies {
    // https://issuetracker.google.com/issues/132906456
    // When Unit tests are launched in CL, the classes.jar for the base module is not included in the final testing classes.jar file.
    // So the tests that have references to the base module fail with java.lang.NoClassDefFoundError exceptions.
    // The following line includes the base module classes.jar into the final one.
    // To run unit tests from CL: ./gradlew fingerprint:test
    testRuntimeOnly(
        fileTree(
            mapOf(
                "include" to listOf("**/*.jar"),
                "dir" to "../id/build/intermediates/app_classes/"
            )
        )
    )

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":id"))
    implementation(project(":uicomponents"))

    implementation(Dependencies.cameraView)
    implementation(Dependencies.circleImageView)
    implementation(Dependencies.Timber.core)

    // Retrofit
    implementation(Dependencies.Retrofit.core)
    implementation(Dependencies.Retrofit.logging)
    implementation(Dependencies.Retrofit.okhttp)
    implementation(Dependencies.Retrofit.converterScalars)

    // Android X
    implementation(Dependencies.AndroidX.UI.constraintlayout)
    implementation(Dependencies.AndroidX.CameraX.core)
    implementation(Dependencies.AndroidX.multidex)

    // Android X
    androidTestImplementation(Dependencies.Testing.AndroidX.core_testing)
    androidTestImplementation(Dependencies.Testing.AndroidX.monitor)
    androidTestImplementation(Dependencies.Testing.AndroidX.core)
    androidTestImplementation(Dependencies.Testing.AndroidX.ext_junit)
    androidTestImplementation(Dependencies.Testing.AndroidX.runner)
    androidTestImplementation(Dependencies.Testing.AndroidX.rules)
    androidTestUtil(Dependencies.Testing.AndroidX.orchestrator)

    androidTestImplementation(Dependencies.Testing.Mockk.android)
    androidTestImplementation(Dependencies.Testing.truth)

    // Espresso
    androidTestImplementation(Dependencies.Testing.Espresso.core)
    androidTestImplementation(Dependencies.Testing.Espresso.intents)
    androidTestImplementation(Dependencies.Testing.Espresso.barista) {
        exclude("com.android.support")
        exclude("com.google.code.findbugs")
        exclude("org.jetbrains.kotlin")
        exclude("com.google.guava")
    }

    // Koin
    androidTestImplementation(Dependencies.Koin.core_ext)
    androidTestImplementation(Dependencies.Testing.koin)

    // ######################################################
    //                      Unit test
    // ######################################################

    // Simprints
    testImplementation(project(":testtools"))
    testImplementation(Dependencies.Testing.junit) {
        exclude("com.android.support")
    }

    // Android X
    testImplementation(Dependencies.Testing.AndroidX.ext_junit)
    testImplementation(Dependencies.Testing.AndroidX.core)
    testImplementation(Dependencies.Testing.AndroidX.core_testing)
    testImplementation(Dependencies.Testing.AndroidX.runner)

    // Kotlin
    testImplementation(Dependencies.Testing.coroutines_test)

    // Mockk
    testImplementation(Dependencies.Testing.Mockk.core)
    testImplementation(Dependencies.Testing.truth)
    testImplementation(Dependencies.Testing.Robolectric.core)
}