package ru.bmourat.converter.domain.repositories

import io.reactivex.rxjava3.core.Single
import ru.bmourat.converter.domain.model.CurrencyRates

interface CurrencyRatesRepository {
    fun rates(): Single<CurrencyRates>
}