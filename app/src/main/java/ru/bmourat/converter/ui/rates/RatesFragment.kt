package ru.bmourat.converter.ui.rates

import android.os.Bundle
import me.vponomarenko.injectionmanager.IHasComponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.bmourat.converter.R
import ru.bmourat.converter.ui.rates.di.RatesComponent
import javax.inject.Inject
import javax.inject.Provider


class RatesFragment : MvpAppCompatFragment(R.layout.fragment_converter), RatesView, IHasComponent<RatesComponent> {

    @Inject
    lateinit var presenterProvider: Provider<RatesPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() };

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        super.onCreate(savedInstanceState)
    }

    override fun renderState(viewState: RatesViewState) {
    }

    override fun getComponent(): RatesComponent = RatesComponent.Initializer.init()

    private fun initDependencies() {
        XInjectionManager.bindComponent(this).inject(this)
    }
}
