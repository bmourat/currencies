package ru.bmourat.converter.domain.converter

import ru.bmourat.converter.domain.model.Rate
import java.math.BigDecimal

interface CurrencyConverter {
    fun convert(fromAmount: BigDecimal, fromRate: Rate, toRate: Rate): BigDecimal
}