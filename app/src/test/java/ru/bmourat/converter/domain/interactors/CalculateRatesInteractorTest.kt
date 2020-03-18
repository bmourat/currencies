package ru.bmourat.converter.domain.interactors

import org.junit.Test
import org.mockito.Mockito.mock
import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.converter.CurrencyConverter
import java.math.BigDecimal

class CalculateRatesInteractorTest {

    private val initialCodeCurrency:CurrencyCode = "EUR"
    private val initialAmount:BigDecimal = BigDecimal.TEN
    private val refreshRatesInteractor = mock(RefreshRatesInteractor::class.java)
    private val currencyConverter = mock(CurrencyConverter::class.java)
    private val sut = CalculateRatesInteractor(
        initialCodeCurrency,
        initialAmount,
        refreshRatesInteractor,
        currencyConverter)


    @Test
    fun `test`() {
    }
}