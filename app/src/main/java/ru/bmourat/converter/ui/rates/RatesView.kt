package ru.bmourat.converter.ui.rates

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndStrategy::class)
interface RatesView: MvpView {
    fun renderState(viewState: RatesViewState)
}