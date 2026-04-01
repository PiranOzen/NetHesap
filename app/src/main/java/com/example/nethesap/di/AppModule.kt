package com.example.nethesap.di

import com.example.nethesap.data.remote.TcmbApi
import com.example.nethesap.data.repository.CurrencyRepositoryImpl
import com.example.nethesap.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTcmbApi(): TcmbApi {
        return Retrofit.Builder()
            .baseUrl(TcmbApi.BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(TcmbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(api: TcmbApi): CurrencyRepository {
        return CurrencyRepositoryImpl(api)
    }
}
