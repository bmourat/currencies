package ru.bmourat.converter.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.bmourat.converter.utils.Either
import java.util.concurrent.TimeUnit
import javax.inject.Inject


typealias CurrencyRatesModel = Either<Failure, CurrencyRates>

class RefreshRatesInteractor @Inject constructor(private val ratesRepository: CurrencyRatesRepository) {

    //FIXME: pass as a parameter
    private val refreshIntervalMs = 1000L

    fun execute(): Observable<CurrencyRatesModel> {
        return Single.fromCallable { ratesRepository.rates() }
            .flatMapObservable { it.toObservable() }
            .map { Either.Right(it) as CurrencyRatesModel }
            .onErrorResumeNext { Observable.just(Either.Left(Failure.NetworkConnection) as CurrencyRatesModel) }
            .repeatWhen {
                it.concatMap {
                    Observable.timer(refreshIntervalMs, TimeUnit.MILLISECONDS)
                }
            }
    }
}