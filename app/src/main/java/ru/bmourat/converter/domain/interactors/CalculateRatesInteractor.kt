package ru.bmourat.converter.domain.interactors

import androidx.annotation.VisibleForTesting
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.converter.CurrencyConverter
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.utils.AppSchedulers
import java.math.BigDecimal
import javax.inject.Inject

class CalculateRatesInteractor @Inject constructor(
    currency: CurrencyCode,
    initialAmount: BigDecimal,
    private val appSchedulers: AppSchedulers,
    private val refreshRatesInteractor: RefreshRatesInteractor,
    private val currencyConverter: CurrencyConverter
) {

    var baseCurrency: CurrencyCode = currency
        private set
    var baseCurrencyAmount: BigDecimal = initialAmount
        private set
    var baseCurrencyAmountStr: String = initialAmount.toString()
        private set

    @VisibleForTesting
    open var currenciesOrder: MutableList<CurrencyCode> = mutableListOf()
        private set


    fun observeCalculatedRates(): Observable<CalculateRatesModel> {
        return refreshRatesInteractor.observeRates()
            .observeOn(appSchedulers.main())
            .map {
                it.fold(
                    this::doOnRefreshRatesFailure,
                    this::doOnRefreshRatesSuccess
                ) as CalculateRatesModel
            }
    }

    fun changeBaseCurrencyAmount(newAmount: String): CalculateRatesModel {
        baseCurrencyAmountStr = newAmount
        val result = modelBuilder()
        baseCurrencyAmount = try {
            BigDecimal(newAmount)
        }catch (error: Exception) {
            result.withError(Error.InputFormat)
            BigDecimal.ZERO
        }
        val converted = convertCurrenciesWithLastRates()
        converted?.let{
            result.withRates(it)
        }
        return result.build()
    }

    fun changeBaseCurrency(currencyRate: CurrencyRate): CalculateRatesModel {
        baseCurrency = currencyRate.currency
        baseCurrencyAmount = currencyRate.rate
        baseCurrencyAmountStr = baseCurrencyAmount.toString()
        currenciesOrder.remove(currencyRate.currency)
        currenciesOrder.add(0,currencyRate.currency)
        val result = modelBuilder()
        val converted = convertCurrenciesWithLastRates()
        converted?.let{
            result.withRates(it)
        }
        return result.build()
    }

    private fun doOnRefreshRatesSuccess(ratesModel: CurrencyRates): CalculateRatesModel {
        updateCurrenciesOrder(baseCurrency, ratesModel)
        val convertedList = convertCurrencies(ratesModel)
        return modelBuilder().withRates(convertedList).build()
    }

    private fun doOnRefreshRatesFailure(error: Error): CalculateRatesModel {
        val result = modelBuilder().withError(error)
        val converted = convertCurrenciesWithLastRates()
        converted?.let{
            result.withRates(it)
        }
        return result.build()
    }

    private fun convertCurrenciesWithLastRates(): List<CurrencyRate>? {
        return refreshRatesInteractor.currentRates?.let {
            convertCurrencies(it)
        }
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
        if (currenciesOrder.isEmpty()) {
            createCurrenciesOrder(baseCurrency, ratesModel)
        }
    }

    private fun createCurrenciesOrder(baseCurrency: CurrencyCode, ratesModel: CurrencyRates) {
        currenciesOrder = ArrayList()
        currenciesOrder.add(baseCurrency)
        currenciesOrder.addAll(ratesModel.currencies().sorted().filter { it != baseCurrency })
    }

    private fun modelBuilder(): CalculateRatesModel.Builder {
        return CalculateRatesModel.Builder(baseCurrencyAmountStr)
    }
}

