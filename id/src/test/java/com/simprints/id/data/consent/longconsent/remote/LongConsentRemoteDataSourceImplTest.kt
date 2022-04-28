package com.simprints.id.data.consent.longconsent.remote

import com.google.common.truth.Truth.assertThat
import com.simprints.core.login.LoginInfoManager
import com.simprints.core.network.SimApiClient
import com.simprints.core.network.SimApiClientFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.net.URL

class LongConsentRemoteDataSourceImplTest {

    @MockK
    lateinit var loginInfoManagerMock: LoginInfoManager
    @MockK
    lateinit var remoteApi: LongConsentRemoteInterface
    @MockK
    lateinit var simApiFactory: SimApiClientFactory
    @MockK
    lateinit var consentDownloader: (FileUrl) -> ByteArray
    @MockK
    lateinit var remoteApiClient: SimApiClient<LongConsentRemoteInterface>


    private lateinit var remoteDataSource: LongConsentRemoteDataSourceImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        every { remoteApiClient.api } returns remoteApi
        coEvery { simApiFactory.buildClient(LongConsentRemoteInterface::class) } returns remoteApiClient
        every { loginInfoManagerMock.getSignedInProjectIdOrEmpty() } returns PROJECT_ID_TEST

        remoteDataSource = LongConsentRemoteDataSourceImpl(
            loginInfoManagerMock,
            simApiFactory,
            consentDownloader
        )
    }

    @Test
    fun `should return file containing contents of long-consent string`() = runBlockingTest {
        val longConsentByteStream = "some really long consent privacy notice.".toByteArray()
        val fileUrl = FileUrl("https://simprints/mock-longconsent-file-url")

        every { consentDownloader.invoke(any()) } returns longConsentByteStream
        coEvery { remoteApi.getLongConsentDownloadUrl(any(), any()) } returns fileUrl

        val longConsent = remoteDataSource.downloadLongConsent(DEFAULT_LANGUAGE)

        assertThat(longConsent.bytes).isEqualTo(longConsentByteStream)
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "EN"
        private const val PROJECT_ID_TEST = "project_id_test"
        const val ABSOLUTE_PATH = "app_root_folder"
    }

}