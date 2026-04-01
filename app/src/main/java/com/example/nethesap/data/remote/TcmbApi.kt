package com.example.nethesap.data.remote

import com.example.nethesap.data.remote.dto.CurrencyResponse
import retrofit2.http.GET

interface TcmbApi {
    @GET("kurlar/today.xml")
    suspend fun getCurrencies(): CurrencyResponse

    companion object {
        const val BASE_URL = "https://www.tcmb.gov.tr/"
    }
}
