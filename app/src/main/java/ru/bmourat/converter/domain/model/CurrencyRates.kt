package ru.bmourat.converter.domain.model

import java.math.BigDecimal

typealias CurrencyCode = String
typealias Rate = BigDecimal

class CurrencyRates(private val rates: Map<CurrencyCode, Rate>) {

    fun currencies(): Set<CurrencyCode> {
        return rates.keys;
    }

    fun rateForCurrency(currencyCode: CurrencyCode): Rate {
        val result = rates[currencyCode]
        return result ?: throw NoSuchElementException()
    }
}