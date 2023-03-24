package com.simprints.infra.events.event.remote.events.callback

import com.google.common.truth.Truth.assertThat
import com.simprints.infra.events.event.domain.models.callback.ErrorCallbackEvent
import com.simprints.infra.events.remote.models.callback.ApiErrorCallback.ApiReason.*
import com.simprints.infra.events.remote.models.callback.fromApiToDomain
import com.simprints.infra.events.remote.models.callback.fromDomainToApi
import org.junit.Test

class ApiErrorCallbackTest {

    @Test
    fun differentProjectIdSignedIn_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.DIFFERENT_PROJECT_ID_SIGNED_IN
        val api = DIFFERENT_PROJECT_ID_SIGNED_IN

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun differentUserIdSignedIn_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.DIFFERENT_USER_ID_SIGNED_IN
        val api = DIFFERENT_USER_ID_SIGNED_IN

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun guidNotFoundOnline_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.GUID_NOT_FOUND_ONLINE
        val api = GUID_NOT_FOUND_ONLINE

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun unexpectedError_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.UNEXPECTED_ERROR
        val api = UNEXPECTED_ERROR

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun bluetoothNotSupported_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.BLUETOOTH_NOT_SUPPORTED
        val api = BLUETOOTH_NOT_SUPPORTED

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun loginNotComplete_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.LOGIN_NOT_COMPLETE
        val api = LOGIN_NOT_COMPLETE

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun backendError_fromDomainToApi() {
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.BACKEND_MAINTENANCE_ERROR
        val api = BACKEND_MAINTENANCE_ERROR

        assertThat(domain.fromDomainToApi()).isEqualTo(api)
    }

    @Test
    fun backendError_fromApiToDomain() {
        val api = BACKEND_MAINTENANCE_ERROR
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.BACKEND_MAINTENANCE_ERROR

        assertThat(api.fromApiToDomain()).isEqualTo(domain)
    }

    @Test
    fun loginNotComplete_fromApiToDomain() {
        val api = LOGIN_NOT_COMPLETE
        val domain = ErrorCallbackEvent.ErrorCallbackPayload.Reason.LOGIN_NOT_COMPLETE

        assertThat(api.fromApiToDomain()).isEqualTo(domain)
    }
}