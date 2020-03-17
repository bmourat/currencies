package ru.bmourat.converter.domain.interactors

import io.reactivex.rxjava3.core.Observable
import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.converter.CurrencyConverter
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.domain.model.CurrencyRates
import java.math.BigDecimal
import javax.inject.Inject

class CalculateRatesInteractor @Inject constructor(
    currency: CurrencyCode,
    initialAmount: BigDecimal,
    private val refreshRatesInteractor: RefreshRatesInteractor,
    private val currencyConverter: CurrencyConverter)  {

    var baseCurrency: CurrencyCode = currency
    var baseCurrencyAmount: BigDecimal = initialAmount

    private lateinit var currenciesOrder: MutableList<CurrencyCode>

    fun observeCalculatedRates(): Observable<CalculateRatesModel> {
        return refreshRatesInteractor.observeRates()
            .map {
                it.fold(
                    this::doOnRefreshRatesFailure,
                    this::doOnRefreshRatesSuccess
                ) as CalculateRatesModel
            }
    }

    private fun doOnRefreshRatesSuccess(ratesModel: CurrencyRates): CalculateRatesModel {
        updateCurrenciesOrder(baseCurrency, ratesModel);
        val convertedList = convertCurrencies(ratesModel)
        return CalculateRatesModel.Builder().withRates(convertedList).build()
    }

    private fun doOnRefreshRatesFailure(error: Error): CalculateRatesModel {
        val result = CalculateRatesModel.Builder().withError(error);
        refreshRatesInteractor.currentRates?.let {
            val convertedList = convertCurrencies(it)
            result.withRates(convertedList)
        }
        return result.build()
    }

    private fun convertCurrencies(ratesModel: CurrencyRates): List<CurrencyRate> {
        val baseCurrencyRate = ratesModel.rateForCurrency(baseCurrency)
        return currenciesOrder.map {
            val toRate = ratesModel.rateForCurrency(it)
            val converted = currencyConverter.convert(baseCurrencyAmount, baseCurrencyRate, toRate)
            return@map CurrencyRate(
                it,
                converted
            )
        }
    }

    private fun updateCurrenciesOrder(baseCurrency: CurrencyCode, ratesModel: CurrencyRates) {
        if (!this::currenciesOrder.isInitialized) {
            createCurrenciesOrder(baseCurrency, ratesModel)
        } else {
            val uniqueElements = currenciesOrder.union(ratesModel.currencies())
            if (uniqueElements.isNotEmpty()) {
                createCurrenciesOrder(baseCurrency, ratesModel)
            }
        }
    }

    private fun createCurrenciesOrder(baseCurrency: CurrencyCode, ratesModel: CurrencyRates) {
        currenciesOrder = ArrayList()
        currenciesOrder.add(baseCurrency)
        currenciesOrder.addAll(ratesModel.currencies().sorted().filter { it != baseCurrency })
    }
}

