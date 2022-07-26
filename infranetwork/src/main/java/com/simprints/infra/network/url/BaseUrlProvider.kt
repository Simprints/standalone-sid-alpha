package com.simprints.infra.network.url

interface BaseUrlProvider {
    fun getApiBaseUrl(): String
    fun setApiBaseUrl(apiBaseUrl: String?)
    fun resetApiBaseUrl()
}