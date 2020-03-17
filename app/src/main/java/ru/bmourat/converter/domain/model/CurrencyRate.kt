package ru.bmourat.converter.domain.model

import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.Rate

data class CurrencyRate(val currency: CurrencyCode, val rate: Rate)