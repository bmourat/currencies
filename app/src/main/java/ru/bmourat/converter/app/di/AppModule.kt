package ru.bmourat.converter.app.di

import dagger.Module
import dagger.Provides
import ru.bmourat.converter.utils.AppSchedulers
import ru.bmourat.converter.utils.RealAppSchedulers
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun appSchedulers(): AppSchedulers = RealAppSchedulers()
}