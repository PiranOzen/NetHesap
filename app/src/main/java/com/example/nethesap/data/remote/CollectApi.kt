package com.example.nethesap.data.remote

import com.example.nethesap.data.remote.dto.GoldResponse
import com.example.nethesap.data.remote.dto.CryptoResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface CollectApi {
    @GET("economy/goldPrice")
    suspend fun getGoldPrices(
        @Header("authorization") apiKey: String
    ): GoldResponse

    @GET("economy/crypto")
    suspend fun getCryptoPrices(
        @Header("authorization") apiKey: String
    ): CryptoResponse

    companion object {
        const val BASE_URL = "https://api.collectapi.com/"
        const val API_KEY = "apikey YOUR_API_KEY_HERE"
    }
}
