package ru.bmourat.converter.ui.rates

class RatesViewState (
    val hasNetworkError: Boolean,
    val hasInputFormatError: Boolean,
    val viewModels: List<RateViewModel>
)