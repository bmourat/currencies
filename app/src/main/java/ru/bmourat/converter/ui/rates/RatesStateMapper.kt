package ru.bmourat.converter.ui.rates

import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.repositories.CurrencyDisplayInfoRepository
import ru.bmourat.converter.domain.error.Error
import javax.inject.Inject

class RatesStateMapper @Inject constructor(private val currencyInfoRepository: CurrencyDisplayInfoRepository) {

    fun mapToState(calculateRatesModel: CalculateRatesModel, forceBaseCurrencyFocus: Boolean): RatesViewState {
        var hasConnectionError = false
        var hasInputFormatError = false
        for (error in calculateRatesModel.errors) {
            when (error) {
                Error.NetworkConnection -> hasConnectionError = true
                Error.InputFormat -> hasInputFormatError = true
            }
        }
        val viewModels = calculateRatesModel.calculatedRates.map {
            val displayInfo = currencyInfoRepository.currencyDisplayInfo(it.currency)
            return@map RateViewModel(
                it,
                displayInfo.currencyShortName,
                displayInfo.currencyName,
                displayInfo.currencyFlag
            )
        }
        return RatesViewState(
            hasConnectionError,
            hasInputFormatError,
            forceBaseCurrencyFocus,
            calculateRatesModel.baseCurrencyAmount,
            viewModels)
    }

}