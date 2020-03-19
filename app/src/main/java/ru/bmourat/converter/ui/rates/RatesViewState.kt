package ru.bmourat.converter.ui.rates

class RatesViewState (
    val hasNetworkError: Boolean,
    val hasInputFormatError: Boolean,
    val forceBaseCurrencyFocus: Boolean,
    val baseCurrencyAmount: String,
    val viewModels: List<RateViewModel>
)