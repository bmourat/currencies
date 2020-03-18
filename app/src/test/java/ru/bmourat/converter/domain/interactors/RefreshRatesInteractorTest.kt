package ru.bmourat.converter.domain.interactors

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import ru.bmourat.converter.domain.CurrencyRatesModel
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.domain.repositories.CurrencyRatesRepository
import ru.bmourat.converter.utils.Either
import ru.bmourat.converter.utils.TestSchedulers
import java.util.concurrent.TimeUnit


class RefreshRatesInteractorTest {

    private val testSchedulers = TestSchedulers(TestScheduler())
    private val currencyRatesRepository = mock(CurrencyRatesRepository::class.java)

    @Test
    fun `currencyRates are not null after successful emit`() {
        //Given
        `when`(currencyRatesRepository.rates()).thenReturn(Single.just(CurrencyRates(emptyMap())))
        val refreshRatesInteractor = RefreshRatesInteractor(1000L, testSchedulers, currencyRatesRepository)
        assertNull(refreshRatesInteractor.currentRates)

        //When
        val observer = refreshRatesInteractor.observeRates().test()

        //Then
        assertNotNull(refreshRatesInteractor.currentRates)
        observer.dispose()
    }

    @Test
    fun `when error occurs model with error is returned`() {
        //Given
        val errorModel = Either.Left(Error.NetworkConnection) as CurrencyRatesModel
        val errorItem = Single.error<CurrencyRates>(Exception())
        `when`(currencyRatesRepository.rates()).thenReturn(errorItem)
        val refreshRatesInteractor = RefreshRatesInteractor(1000L, testSchedulers, currencyRatesRepository)

        //When
        val testObserver = refreshRatesInteractor.observeRates().test()

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(1)
        testObserver.assertValues(errorModel)
    }

    @Test
    fun `stream is continued after error occurs`() {
        //Given
        val validItem = Single.just(CurrencyRates(emptyMap()))
        val errorItem = Single.error<CurrencyRates>(Exception())
        `when`(currencyRatesRepository.rates()).thenReturn(validItem)
        val refreshRatesInteractor = RefreshRatesInteractor(1000L, testSchedulers, currencyRatesRepository)
        
        //When
        val testObserver = refreshRatesInteractor.observeRates().test()

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(1)

        //When
        `when`(currencyRatesRepository.rates()).thenReturn(errorItem)
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(2)

        //When
        `when`(currencyRatesRepository.rates()).thenReturn(validItem)
        testSchedulers.computation().advanceTimeBy(1, TimeUnit.SECONDS)

        //Then
        testObserver.assertNotComplete()
        testObserver.assertValueCount(3)

        testObserver.dispose()
    }
}