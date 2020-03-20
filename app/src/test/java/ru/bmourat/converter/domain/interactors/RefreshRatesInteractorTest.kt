package ru.bmourat.converter.domain.interactors

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import ru.bmourat.converter.domain.CurrencyRatesModel
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.domain.repositories.CurrencyRatesRepository
import ru.bmourat.converter.utils.Either
import ru.bmourat.converter.utils.TestSchedulers
import java.util.concurrent.TimeUnit


class RefreshRatesInteractorTest {
    private val refreshIntervalMs = 1000L
    private val testSchedulers = TestSchedulers(TestScheduler())
    private val currencyRatesRepository = mock<CurrencyRatesRepository>()
    private val sut = RefreshRatesInteractor(refreshIntervalMs, testSchedulers, currencyRatesRepository)

    @Test
    fun `currencyRates are not null after successful emit`() {
        //Given
        whenever(currencyRatesRepository.rates()).thenReturn(Single.just(CurrencyRates(emptyMap())))
        assertNull(sut.currentRates)

        //When
        val observer = sut.observeRates().test()

        //Then
        assertNotNull(sut.currentRates)

        observer.dispose()
    }

    @Test
    fun `when error occurs model with error is returned`() {
        //Given
        val errorModel = Either.Left(Error.NetworkConnection) as CurrencyRatesModel
        val errorItem = Single.error<CurrencyRates>(Exception())
        whenever(currencyRatesRepository.rates()).thenReturn(errorItem)

        //When
        val testObserver = sut.observeRates().test()

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(1)
        testObserver.assertValues(errorModel)

        testObserver.dispose()
    }

    @Test
    fun `stream is continued after error occurs`() {
        //Given
        val validItem = Single.just(CurrencyRates(emptyMap()))
        val errorItem = Single.error<CurrencyRates>(Exception())
        whenever(currencyRatesRepository.rates()).thenReturn(validItem)
        
        //When
        val testObserver = sut.observeRates().test()

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(1)

        //When
        whenever(currencyRatesRepository.rates()).thenReturn(errorItem)
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(2)

        //When
        whenever(currencyRatesRepository.rates()).thenReturn(validItem)
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(3)

        testObserver.dispose()
    }
}