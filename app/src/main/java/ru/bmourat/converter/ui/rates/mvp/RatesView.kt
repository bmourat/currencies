package ru.bmourat.converter.ui.rates.mvp

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface RatesView: MvpView {
    fun renderState(viewState: RatesViewState)
}