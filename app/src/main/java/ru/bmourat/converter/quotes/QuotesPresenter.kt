package ru.bmourat.converter.quotes

import android.util.Log
import moxy.MvpPresenter
import ru.bmourat.converter.utils.AppSchedulers
import javax.inject.Inject

class QuotesPresenter @Inject constructor(val appSchedulers: AppSchedulers): MvpPresenter<QuotesView>() {
}