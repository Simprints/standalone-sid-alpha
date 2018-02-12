package com.simprints.id.secure

import com.simprints.id.BuildConfig
import com.simprints.id.secure.models.Nonce
import com.simprints.id.secure.models.PublicKeyString
import com.simprints.id.secure.models.Token
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface ApiServiceInterface {

    companion object {
        const val baseUrl = "https://project-manager-dot-${BuildConfig.GCP_PROJECT}.appspot.com"
        const val apiKey: String = "AIzaSyAORPo9YH-TBw0F1ch8BMP9IGkNElgon6s"
    }

    @GET("/nonces")
    fun nonce(@HeaderMap headers: Map<String, String>, @Query("key") key: String = ApiServiceInterface.apiKey): Single<Nonce>

    @GET("/public-key")
    fun publicKey(@Query("key") key: String = ApiServiceInterface.apiKey): Single<PublicKeyString>

    @GET("/tokens")
    fun auth(@HeaderMap headers: Map<String, String>, @Query("key") key: String = ApiServiceInterface.apiKey): Single<Token>
}