package ru.bmourat.converter.quotes.di

import dagger.Subcomponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import ru.bmourat.converter.app.di.AppComponent
import ru.bmourat.converter.quotes.QuotesFragment

@Subcomponent(modules = [QuotesModule::class])
interface QuotesComponent {
    fun inject(quotesFragment: QuotesFragment)

    class Initializer private constructor() {
        companion object {
            fun init(): QuotesComponent {
                return XInjectionManager.findComponent<AppComponent>().plus(QuotesModule())
            }
        }
    }
}