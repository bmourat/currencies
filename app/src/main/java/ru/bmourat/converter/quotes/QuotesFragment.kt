package ru.bmourat.converter.quotes

import android.os.Bundle
import me.vponomarenko.injectionmanager.IHasComponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.bmourat.converter.R
import ru.bmourat.converter.quotes.di.QuotesComponent
import javax.inject.Inject
import javax.inject.Provider


class QuotesFragment : MvpAppCompatFragment(R.layout.fragment_converter), QuotesView, IHasComponent<QuotesComponent> {

    @Inject
    lateinit var presenterProvider: Provider<QuotesPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() };

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        super.onCreate(savedInstanceState)
    }

    override fun getComponent(): QuotesComponent = QuotesComponent.Initializer.init()

    private fun initDependencies() {
        XInjectionManager.bindComponent(this).inject(this)
    }
}
