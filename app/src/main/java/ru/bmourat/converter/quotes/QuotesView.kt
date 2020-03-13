package ru.bmourat.converter.quotes

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndStrategy::class)
interface QuotesView: MvpView {
}