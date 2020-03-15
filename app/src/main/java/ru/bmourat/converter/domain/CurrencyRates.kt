package ru.bmourat.converter.domain

import java.math.BigDecimal

typealias Rate = BigDecimal

class CurrencyRates(private val rates: Map<String, Rate>)