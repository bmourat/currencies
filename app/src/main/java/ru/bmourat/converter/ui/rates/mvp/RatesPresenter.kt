package ru.bmourat.converter.ui.rates.mvp

import android.util.Log
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import ru.bmourat.converter.domain.interactors.CalculateRatesInteractor
import ru.bmourat.converter.domain.model.CalculateRatesModel
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.utils.AppSchedulers
import javax.inject.Inject

class RatesPresenter @Inject constructor(private val appSchedulers: AppSchedulers,
                                         private val mapper: RatesStateMapper,
                                         private val calculateRatesInteractor: CalculateRatesInteractor
)
    : MvpPresenter<RatesView>() {

    private val logTag = RatesPresenter::class.java.simpleName
    private val disposables = CompositeDisposable()

    override fun attachView(view: RatesView?) {
        super.attachView(view)
        disposables.add(calculateRatesInteractor.observeCalculatedRates()
            .subscribeOn(appSchedulers.io())
            .observeOn(appSchedulers.main())
            .map { mapModel(it) }
            .subscribe(
                this::renderViewState,
                this::handleError
            )
        )
    }

    override fun detachView(view: RatesView?) {
        super.detachView(view)
        disposables.clear()
    }

    fun onBaseCurrencyAmountChanged(newAmount: String) {
        val model = calculateRatesInteractor.changeBaseCurrencyAmount(newAmount)
        renderViewState(mapModel(model))
    }

    fun onBaseCurrencyChanged(newBaseCurrency: CurrencyRate) {
        val model = calculateRatesInteractor.changeBaseCurrency(newBaseCurrency)
        renderViewState(mapModel(model, true))
    }

    private fun mapModel(model: CalculateRatesModel, forceBaseCurrencyFocus: Boolean = false): RatesViewState {
        return mapper.mapToState(model, forceBaseCurrencyFocus)
    }

    private fun renderViewState(ratesViewState: RatesViewState) {
        viewState.renderState(ratesViewState)
    }

    /**
     * This should not be called as we manage all errors before
     * presenter. But in case we missed something :)
     */
    private fun handleError(throwable: Throwable) {
        Log.e(logTag, "Received an error $throwable")
    }
}