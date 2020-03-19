package ru.bmourat.converter.app.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.bmourat.converter.R
import ru.bmourat.converter.app.ConverterApp
import ru.bmourat.converter.data.CurrencyDisplayInfoRepositoryImpl
import ru.bmourat.converter.data.RatesApi
import ru.bmourat.converter.domain.repositories.CurrencyRatesRepository
import ru.bmourat.converter.data.RatesRepositoryRemote
import ru.bmourat.converter.domain.interactors.CalculateRatesInteractor
import ru.bmourat.converter.domain.interactors.RefreshRatesInteractor
import ru.bmourat.converter.domain.converter.CurrencyConverter
import ru.bmourat.converter.domain.converter.CurrencyConverterImpl
import ru.bmourat.converter.domain.repositories.CurrencyDisplayInfoRepository
import ru.bmourat.converter.utils.AppSchedulers
import ru.bmourat.converter.utils.RealAppSchedulers
import java.math.BigDecimal
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
    @Singleton
    fun resources(): Resources = app.resources

    @Provides
    fun currencyRatesRepository(api: RatesApi): CurrencyRatesRepository {
        val baseCurrency = app.resources.getString(R.string.default_base_currency)
        return RatesRepositoryRemote(baseCurrency, api)
    }

    @Provides
    fun currencyDisplayInfoRepository(resources: Resources): CurrencyDisplayInfoRepository {
        return CurrencyDisplayInfoRepositoryImpl(resources)
    }

    @Provides
    fun calculateRatesInteractor(appSchedulers: AppSchedulers,
                                 refreshRatesInteractor: RefreshRatesInteractor,
                                 currencyConverter: CurrencyConverter): CalculateRatesInteractor {
        val baseCurrency = app.resources.getString(R.string.default_base_currency)
        val initialAmount = app.resources.getInteger(R.integer.initial_convert_amount)
        val resultScale = app.resources.getInteger(R.integer.conversion_result_scale)
        return CalculateRatesInteractor(
            baseCurrency, BigDecimal(initialAmount).setScale(resultScale),
            appSchedulers, refreshRatesInteractor, currencyConverter
        )
    }

    @Provides
    fun refreshRatesInteractor(currencyRatesRepository: CurrencyRatesRepository,
                               appSchedulers: AppSchedulers): RefreshRatesInteractor {
        val refreshInterval = app.resources.getInteger(R.integer.server_refresh_interval_ms)
        return RefreshRatesInteractor(
            refreshInterval.toLong(),
            appSchedulers,
            currencyRatesRepository
        )
    }

    @Provides
    fun currencyConverter(): CurrencyConverter {
        val resultScale = app.resources.getInteger(R.integer.conversion_result_scale)
        return CurrencyConverterImpl(resultScale)
    }
}