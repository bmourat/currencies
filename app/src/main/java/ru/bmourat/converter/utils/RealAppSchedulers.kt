package ru.bmourat.converter.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class RealAppSchedulers: AppSchedulers {
    override fun main(): Scheduler = AndroidSchedulers.mainThread()
    override fun io(): Scheduler = Schedulers.io()
    override fun computation(): Scheduler = Schedulers.computation()
}