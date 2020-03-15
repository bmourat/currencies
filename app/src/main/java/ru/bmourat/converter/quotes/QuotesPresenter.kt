package ru.bmourat.converter.quotes

import android.util.Log
import moxy.MvpPresenter
import ru.bmourat.converter.domain.RefreshRatesInteractor
import ru.bmourat.converter.utils.AppSchedulers
import java.util.*
import javax.inject.Inject

class QuotesPresenter @Inject constructor(private val appSchedulers: AppSchedulers,
                                          private val refreshRatesInteractor: RefreshRatesInteractor)
    : MvpPresenter<QuotesView>() {

    private val tag = this::class.java.simpleName

    override fun onFirstViewAttach() {
        refreshRatesInteractor.execute()
            .subscribeOn(appSchedulers.io())
            .observeOn(appSchedulers.main())
            .subscribe {
                it.fold(
                    { error -> Log.d(tag, "Received an error: $error at ${Date()}")},
                    { rates -> Log.d(tag, "Received a value: $rates at ${Date()}")}
                )
            }
    }
}