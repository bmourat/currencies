package ru.bmourat.converter.utils

import io.reactivex.rxjava3.schedulers.TestScheduler

class TestSchedulers(private val testScheduler: TestScheduler): AppSchedulers {
    override fun main() = testScheduler
    override fun io() = testScheduler
    override fun computation() = testScheduler
}