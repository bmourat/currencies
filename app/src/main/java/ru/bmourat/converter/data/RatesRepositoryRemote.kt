package ru.bmourat.converter.data

import io.reactivex.rxjava3.core.Single
import ru.bmourat.converter.domain.CurrencyRates
import ru.bmourat.converter.domain.CurrencyRatesRepository
import java.math.BigDecimal

class RatesRepositoryRemote(private val baseCurrency: String,
                            private val api: RatesApi):
    CurrencyRatesRepository {
    override fun rates(): Single<CurrencyRates> {
        return api.rates(baseCurrency)
            .map { HashMap(it.rates) }
            .map {
                it[baseCurrency] = BigDecimal.ONE
                it
            }
            .map { CurrencyRates(it) }
    }
}