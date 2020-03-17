package ru.bmourat.converter.ui.rates

import moxy.MvpPresenter
import ru.bmourat.converter.domain.interactors.CalculateRatesInteractor
import ru.bmourat.converter.utils.AppSchedulers
import javax.inject.Inject

class RatesPresenter @Inject constructor(private val appSchedulers: AppSchedulers,
                                         private val mapper: RatesStateMapper,
                                         private val calculateRatesInteractor: CalculateRatesInteractor
)
    : MvpPresenter<RatesView>() {

    override fun onFirstViewAttach() {
        calculateRatesInteractor.observeCalculatedRates()
            .subscribeOn(appSchedulers.io())
            .observeOn(appSchedulers.main())
            .map(mapper::mapToState)
            .subscribe {
                viewState.renderState(it)
            }
    }
}