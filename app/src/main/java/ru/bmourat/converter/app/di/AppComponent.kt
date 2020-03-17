package ru.bmourat.converter.app.di

import dagger.Component
import ru.bmourat.converter.app.ConverterApp
import ru.bmourat.converter.ui.rates.di.RatesComponent
import ru.bmourat.converter.ui.rates.di.RatesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    operator fun plus(ratesModule: RatesModule): RatesComponent

    class Initializer private constructor() {
        companion object {
            fun init(app: ConverterApp): AppComponent {
                return DaggerAppComponent.builder()
                    .appModule(AppModule(app))
                    .build()
            }
        }
    }
}