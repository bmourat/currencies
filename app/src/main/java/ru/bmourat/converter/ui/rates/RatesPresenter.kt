package ru.bmourat.converter.ui.rates

import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.bmourat.converter.domain.interactors.CalculateRatesInteractor
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.utils.AppSchedulers
import javax.inject.Inject

class RatesPresenter @Inject constructor(private val appSchedulers: AppSchedulers,
                                         private val mapper: RatesStateMapper,
                                         private val calculateRatesInteractor: CalculateRatesInteractor
)
    : MvpPresenter<RatesView>() {

    private val disposables = CompositeDisposable()
    private var updateBaseCurrencyAmount = true

    override fun attachView(view: RatesView?) {
        super.attachView(view)
        disposables.add(calculateRatesInteractor.observeCalculatedRates()
            .subscribeOn(appSchedulers.io())
            .observeOn(appSchedulers.main())
            .map{ mapper.mapToState(it, updateBaseCurrencyAmount)}
            .subscribe {
                viewState.renderState(it)
                if (updateBaseCurrencyAmount) {
                    updateBaseCurrencyAmount = false
                }
            }
        )
    }

    override fun detachView(view: RatesView?) {
        super.detachView(view)
        disposables.clear()
    }

    fun onBaseCurrencyAmountChanged(newAmount: String) {
        calculateRatesInteractor.changeBaseCurrencyAmount(newAmount)
    }

    fun onBaseCurrencyChanged(newBaseCurrency: CurrencyRate) {

    }
}