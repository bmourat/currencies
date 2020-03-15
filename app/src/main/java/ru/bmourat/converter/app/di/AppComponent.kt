package ru.bmourat.converter.app.di

import dagger.Component
import ru.bmourat.converter.app.ConverterApp
import ru.bmourat.converter.quotes.di.QuotesComponent
import ru.bmourat.converter.quotes.di.QuotesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    operator fun plus(quotesModule: QuotesModule): QuotesComponent

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