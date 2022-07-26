package com.simprints.fingerprint.controllers.core.network

import com.google.common.truth.Truth
import com.simprints.core.login.LoginInfoManager
import com.simprints.fingerprint.scanner.data.FirmwareTestData
import com.simprints.id.data.file.FileUrl
import com.simprints.id.data.file.FileUrlRemoteInterface
import com.simprints.testtools.common.coroutines.TestCoroutineRule
import com.simprints.testtools.common.coroutines.TestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FingerprintFileDownloaderTest {

    lateinit var fingerprintFileDownloader: FingerprintFileDownloader

    @MockK
    lateinit var fingerprintApiClientFactory: FingerprintApiClientFactory

    @MockK
    lateinit var loginInfoManager: LoginInfoManager

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val dispatcherProvider = TestDispatcherProvider(testCoroutineRule)


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        fingerprintFileDownloader =
            FingerprintFileDownloader(
                fingerprintApiClientFactory,
                loginInfoManager,
                dispatcherProvider
            )
    }
    @Test
    fun getFileUrl() = runBlocking {
        // Given
        val apiClient: FingerprintApiClient<FileUrlRemoteInterface> = mockk()
        val api: FileUrlRemoteInterface = mockk()
        coEvery { fingerprintApiClientFactory.buildClient<FileUrlRemoteInterface>(any()) } returns apiClient
        every { apiClient.api } returns api
        coEvery { api.getFileUrl(any(), any()) } returns FileUrl(FirmwareTestData.SOME_URL)
        every { loginInfoManager.getSignedInProjectIdOrEmpty()} returns "projectId"
        // When
        val result = fingerprintFileDownloader.getFileUrl("Any fileId")
        // Then
        Truth.assertThat(result).isEqualTo(FirmwareTestData.SOME_URL)
    }
}