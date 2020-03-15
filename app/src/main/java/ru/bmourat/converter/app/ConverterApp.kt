package ru.bmourat.converter.app

import android.app.Application
import me.vponomarenko.injectionmanager.IHasComponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import ru.bmourat.converter.app.di.AppComponent

class ConverterApp: Application(), IHasComponent<AppComponent>{

    override fun onCreate() {
        super.onCreate()
        XInjectionManager.init(this)
        XInjectionManager.bindComponent(this)
    }

    override fun getComponent(): AppComponent = AppComponent.Initializer.init(this)
}