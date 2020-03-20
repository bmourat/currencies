package ru.bmourat.converter.ui.rates.mvp

import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.bmourat.converter.domain.interactors.CalculateRatesInteractor
import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.utils.TestSchedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class RatesPresenterTest {

    private val testSchedulers = TestSchedulers(TestScheduler())
    private val view = mock<RatesView>()
    private val mapper = mock<RatesStateMapper>()
    private val interactor = mock<CalculateRatesInteractor>()
    private val sut = RatesPresenter(testSchedulers, mapper, interactor)

    @Test
    fun `renderState is called after every time model is received`() {
        //Given
        val ratesViewState = emptyRatesViewState()
        whenever(interactor.observeCalculatedRates())
            .thenReturn(Observable.fromIterable(
                listOf(emptyCalculatedRatesModel(), emptyCalculatedRatesModel(), emptyCalculatedRatesModel())
            ))
        whenever(mapper.mapToState(any(), any())).thenReturn(ratesViewState)

        //When
        sut.attachView(view)
        testSchedulers.main().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        verify(view, times(3)).renderState(ratesViewState)
    }

    @Test
    fun `base currency focus is forced after base currency change`() {
        //Given
        val newBaseCurrency = CurrencyRate("CODE1", BigDecimal.ONE)
        whenever(mapper.mapToState(any(), any())).thenReturn(emptyRatesViewState())
        whenever(interactor.observeCalculatedRates()).thenReturn(Observable.empty())
        whenever(interactor.changeBaseCurrency(newBaseCurrency)).thenReturn(emptyCalculatedRatesModel())

        //When
        sut.attachView(view)
        sut.onBaseCurrencyChanged(newBaseCurrency)

        //Then
        verify(mapper, times(1)).mapToState(emptyCalculatedRatesModel(), true)
    }

    @Test
    fun `renderState is called after base currency change`() {
        //Given
        val newBaseCurrency = CurrencyRate("CODE1", BigDecimal.ONE)
        whenever(mapper.mapToState(any(), any())).thenReturn(emptyRatesViewState())
        whenever(interactor.observeCalculatedRates()).thenReturn(Observable.empty())
        whenever(interactor.changeBaseCurrency(newBaseCurrency)).thenReturn(emptyCalculatedRatesModel())

        //When
        sut.attachView(view)
        sut.onBaseCurrencyChanged(newBaseCurrency)

        //Then
        verify(view, times(1)).renderState(any())
    }

    @Test
    fun `renderState is called after base currency amount change`() {
        //Given
        val newBaseCurrencyAmount = "amount"
        whenever(mapper.mapToState(any(), any())).thenReturn(emptyRatesViewState())
        whenever(interactor.observeCalculatedRates()).thenReturn(Observable.empty())
        whenever(interactor.changeBaseCurrencyAmount(newBaseCurrencyAmount)).thenReturn(emptyCalculatedRatesModel())

        //When
        sut.attachView(view)
        sut.onBaseCurrencyAmountChanged(newBaseCurrencyAmount)

        //Then
        verify(view, times(1)).renderState(any())
    }

    private fun emptyCalculatedRatesModel(): CalculateRatesModel {
        return CalculateRatesModel.Builder("").build()
    }

    private fun emptyRatesViewState(): RatesViewState {
        return RatesViewState(
            hasNetworkError = false,
            hasInputFormatError = false,
            forceBaseCurrencyFocus = false,
            baseCurrencyAmount = "",
            viewModels = emptyList()
        )
    }
}