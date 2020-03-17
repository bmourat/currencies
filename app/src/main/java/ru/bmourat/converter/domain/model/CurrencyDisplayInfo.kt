package ru.bmourat.converter.domain.model

import androidx.annotation.DrawableRes

data class CurrencyDisplayInfo(
    val currencyShortName: String,
    val currencyName: String,
    @DrawableRes val currencyFlag: Int
)