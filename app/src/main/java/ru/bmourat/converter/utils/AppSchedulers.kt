package ru.bmourat.converter.utils

import io.reactivex.rxjava3.core.Scheduler

interface AppSchedulers {
    fun main(): Scheduler
    fun io(): Scheduler
    fun computation(): Scheduler
}