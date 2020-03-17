package ru.bmourat.converter.domain

import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.utils.Either
import java.math.BigDecimal

typealias CurrencyRatesModel = Either<Error, CurrencyRates>
typealias CurrencyCode = String
typealias Rate = BigDecimal