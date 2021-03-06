package ru.bmourat.converter.domain.model

import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.Rate

class CurrencyRates(private val rates: Map<CurrencyCode, Rate>) {

    fun currencies(): Set<CurrencyCode> {
        return rates.keys;
    }

    fun rateForCurrency(currencyCode: CurrencyCode): Rate {
        val result = rates[currencyCode]
        return result ?: throw NoSuchElementException()
    }
}