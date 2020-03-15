package ru.bmourat.converter.domain

import io.reactivex.rxjava3.core.Single

interface CurrencyRatesRepository {
    fun rates(): Single<CurrencyRates>
}