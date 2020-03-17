package ru.bmourat.converter.domain.interactors

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.bmourat.converter.domain.model.CurrencyRates
import ru.bmourat.converter.domain.error.Error
import ru.bmourat.converter.domain.repositories.CurrencyRatesRepository
import ru.bmourat.converter.utils.Either
import java.util.concurrent.TimeUnit

typealias CurrencyRatesModel = Either<Error, CurrencyRates>

class RefreshRatesInteractor(private val refreshIntervalMs: Long,
                             private val ratesRepository: CurrencyRatesRepository
) {

    var currentRates: CurrencyRates? = null
        private set

    fun observeRates(): Observable<CurrencyRatesModel> {
        return Single.fromCallable { ratesRepository.rates() }
            .flatMapObservable { it.toObservable() }
            .doOnNext { currentRates = it }
            .map { Either.Right(it) as CurrencyRatesModel }
            .onErrorResumeNext { Observable.just(Either.Left(Error.NetworkConnection) as CurrencyRatesModel) }
            .repeatWhen {
                it.concatMap {
                    Observable.timer(refreshIntervalMs, TimeUnit.MILLISECONDS)
                }
            }
    }
}