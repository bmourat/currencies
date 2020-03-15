package ru.bmourat.converter.app.di

import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.bmourat.converter.R
import ru.bmourat.converter.app.ConverterApp
import ru.bmourat.converter.data.RatesApi
import ru.bmourat.converter.domain.CurrencyRatesRepository
import ru.bmourat.converter.data.RatesRepositoryRemote
import ru.bmourat.converter.utils.AppSchedulers
import ru.bmourat.converter.utils.RealAppSchedulers
import javax.inject.Singleton

@Module
class AppModule(private val app: ConverterApp) {

    @Provides
    @Singleton
    fun appSchedulers(): AppSchedulers = RealAppSchedulers()

    @Provides
    @Singleton
    fun quotesApi(): RatesApi {
        val baseUrl = app.resources.getString(R.string.base_url)
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(RatesApi::class.java)
    }

    @Provides
    fun currencyRatesRepository(api: RatesApi): CurrencyRatesRepository {
        val baseCurrency = app.resources.getString(R.string.default_base_currency)
        return RatesRepositoryRemote(baseCurrency, api)
    }
}