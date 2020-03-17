package ru.bmourat.converter.ui.rates.di

import dagger.Subcomponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import ru.bmourat.converter.app.di.AppComponent
import ru.bmourat.converter.ui.rates.RatesFragment

@Subcomponent(modules = [RatesModule::class])
interface RatesComponent {
    fun inject(ratesFragment: RatesFragment)

    class Initializer private constructor() {
        companion object {
            fun init(): RatesComponent {
                return XInjectionManager.findComponent<AppComponent>().plus(RatesModule())
            }
        }
    }
}