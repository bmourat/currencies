package ru.bmourat.converter.ui.rates.mvp

import ru.bmourat.converter.ui.rates.mvp.RateViewModel

class RatesViewState (
    val hasNetworkError: Boolean,
    val hasInputFormatError: Boolean,
    val forceBaseCurrencyFocus: Boolean,
    val baseCurrencyAmount: String,
    val viewModels: List<RateViewModel>
)