package com.example.nethesap.domain.use_case

import com.example.nethesap.domain.model.Currency
import com.example.nethesap.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Resource<List<Currency>>> = flow {
        try {
            emit(Resource.Loading())
            val currencies = repository.getCurrencies()
            emit(Resource.Success(currencies))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Bir hata oluştu"))
        }
    }
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
