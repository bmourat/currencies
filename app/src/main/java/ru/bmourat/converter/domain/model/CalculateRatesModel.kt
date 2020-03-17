package ru.bmourat.converter.domain.model

import ru.bmourat.converter.domain.error.Error

class CalculateRatesModel private constructor(
    val calculatedRates: List<CurrencyRate>,
    val errors: List<Error>) {

    data class Builder(
        private var calculatedRates: List<CurrencyRate> = emptyList(),
        private var errors: MutableList<Error> = mutableListOf()) {
        fun withRates(rates: List<CurrencyRate>) = apply { this.calculatedRates = rates }
        fun withError(error: Error) = apply { this.errors.add(error) }
        fun build() = CalculateRatesModel(
            calculatedRates,
            errors
        )
    }
}