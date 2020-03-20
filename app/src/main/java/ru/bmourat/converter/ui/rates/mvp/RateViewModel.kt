package ru.bmourat.converter.ui.rates.mvp

import androidx.annotation.DrawableRes
import ru.bmourat.converter.domain.model.CurrencyRate

data class RateViewModel(
    val currencyRate: CurrencyRate,
    val currencyShortName: String,
    val currencyName: String,
    @DrawableRes val currencyFlag: Int)