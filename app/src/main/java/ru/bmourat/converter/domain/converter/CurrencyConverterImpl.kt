package ru.bmourat.converter.domain.converter

import ru.bmourat.converter.domain.Rate
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyConverterImpl(private val resultScale: Int): CurrencyConverter {
    override fun convert(fromAmount: BigDecimal, fromRate: Rate, toRate: Rate): BigDecimal {
        if (fromAmount <= BigDecimal.ZERO ||
            fromRate <= BigDecimal.ZERO   ||
            toRate <= BigDecimal.ZERO) {
            return BigDecimal.ZERO;
        }
        val valueInBaseCurrency = fromAmount.divide(fromRate, RoundingMode.HALF_EVEN)
        return valueInBaseCurrency.multiply(toRate).setScale(resultScale, RoundingMode.HALF_EVEN)
    }
}