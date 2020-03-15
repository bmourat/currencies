package ru.bmourat.converter.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface RatesApi {

    @GET("api/android/latest")
    fun rates(@Query("base") baseCurrency: String): Single<RatesResponse>
}