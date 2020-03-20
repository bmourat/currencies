package ru.bmourat.converter.domain.interactors

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Test
import ru.bmourat.converter.domain.CurrencyCode
import ru.bmourat.converter.domain.CurrencyRatesModel
import ru.bmourat.converter.domain.converter.CurrencyConverter
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.utils.Either
import ru.bmourat.converter.utils.TestSchedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CalculateRatesInteractorTest {

    private val initialCodeCurrency: CurrencyCode = "CODE1"
    private val initialAmount: BigDecimal = BigDecimal.ONE
    private val testSchedulers = TestSchedulers(TestScheduler())
    private val refreshRatesInteractor = mock<RefreshRatesInteractor>()
    private val currencyConverter = mock<CurrencyConverter>()
    private val sut = CalculateRatesInteractor(
        initialCodeCurrency,
        initialAmount,
        testSchedulers,
        refreshRatesInteractor,
        currencyConverter
    )


    @Test
    fun `no errors returned when refreshInteractor sends valid CurrencyRates`() {
        //Given
        whenever(refreshRatesInteractor.observeRates()).thenReturn(
            Observable.just(
                testCurrencyRatesModel()
            )
        )
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(BigDecimal.ONE)

        //When
        val testObserver = sut.observeCalculatedRates().test() as TestObserver
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertValue(
            CalculateRatesModel.Builder("1")
                .withRates(testCurrencyRatesList())
                .build()
        )
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        testObserver.dispose()
    }

    @Test
    fun `errors are returned when refreshInteractor sends error`() {
        //Given
        whenever(refreshRatesInteractor.observeRates()).thenReturn(
            Observable.just(
                testCurrencyRatesModelError()
            )
        )
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(BigDecimal.ONE)

        //When
        val testObserver = sut.observeCalculatedRates().test() as TestObserver
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertValue(
            CalculateRatesModel.Builder("1")
                .withError(Error.NetworkConnection)
                .build()
        )
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        testObserver.dispose()
    }

    @Test
    fun `rates are calculated based on previous rates in case of network error`() {
        //Given
        whenever(refreshRatesInteractor.observeRates()).thenReturn(
            Observable.just(
                testCurrencyRatesModelError()
            )
        )
        whenever(refreshRatesInteractor.currentRates).thenReturn(testCurrencyRates())
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(BigDecimal.ONE)
        val sutSpy = spy(sut)
        whenever(sutSpy.currenciesOrder).thenReturn(testCurrencyCodesList())

        //When
        val testObserver = sutSpy.observeCalculatedRates().test() as TestObserver
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertValue(
            CalculateRatesModel.Builder("1")
                .withRates(testCurrencyRatesList())
                .withError(Error.NetworkConnection)
                .build()
        )
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        testObserver.dispose()
    }

    @Test
    fun `error is returned when new amount can not be parsed`() {
        //When
        val invalidAmount = "qqqq"
        val result = sut.changeBaseCurrencyAmount(invalidAmount)

        //Then
        assertEquals(sut.baseCurrencyAmountStr, invalidAmount)
        assertEquals(sut.baseCurrencyAmount, BigDecimal.ZERO)
        assertTrue(result.errors.contains(Error.InputFormat))
    }

    @Test
    fun `no error is returned when new amount is valid`() {
        //When
        val validAmount = "100"
        val result = sut.changeBaseCurrencyAmount(validAmount)

        //Then
        assertEquals(sut.baseCurrencyAmountStr, validAmount)
        assertEquals(sut.baseCurrencyAmount, BigDecimal(100))
        assertFalse(result.errors.contains(Error.InputFormat))
    }

    @Test
    fun `base currency is changed after changeBaseCurrency`() {
        //Given
        val newBaseCurrency = CurrencyRate("CODE2", BigDecimal.ONE)
        whenever(refreshRatesInteractor.currentRates).thenReturn(testCurrencyRates())
        whenever(currencyConverter.convert(any(), any(), any())).thenReturn(BigDecimal.ONE)
        val sutSpy = spy(sut)
        whenever(sutSpy.currenciesOrder).thenReturn(testCurrencyCodesList())

        //When
        val result = sut.changeBaseCurrency(newBaseCurrency)

        //Then
        assertEquals(sut.baseCurrency, newBaseCurrency.currency)
        assertEquals(sut.baseCurrencyAmount, BigDecimal.ONE)
        assertEquals(sut.baseCurrencyAmountStr, "1")
        assertEquals(result.calculatedRates[0], newBaseCurrency)
        assertTrue(result.errors.isEmpty())
    }

    private fun testCurrencyRatesModel(): CurrencyRatesModel {
        return Either.Right(testCurrencyRates())
    }

    private fun testCurrencyRates(): CurrencyRates {
        return CurrencyRates(
            mapOf(
                "CODE1" to BigDecimal.ONE,
                "CODE2" to BigDecimal.ONE,
                "CODE3" to BigDecimal.ONE,
                "CODE4" to BigDecimal.ONE
            )
        )
    }

    private fun testCurrencyRatesModelError(): CurrencyRatesModel {
        return Either.Left(Error.NetworkConnection)
    }

    private fun testCurrencyRatesList(): List<CurrencyRate> {
        return listOf(
            CurrencyRate("CODE1", BigDecimal.ONE),
            CurrencyRate("CODE2", BigDecimal.ONE),
            CurrencyRate("CODE3", BigDecimal.ONE),
            CurrencyRate("CODE4", BigDecimal.ONE)
        )
    }

    private fun testCurrencyCodesList(): MutableList<CurrencyCode> {
        return mutableListOf(
            "CODE1",
            "CODE2",
            "CODE3",
            "CODE4"
        )
    }
}