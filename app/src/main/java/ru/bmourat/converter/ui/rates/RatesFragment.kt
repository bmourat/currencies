package ru.bmourat.converter.ui.rates

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var ratesAdapter: RatesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        super.onCreate(savedInstanceState)
        ratesAdapter = RatesAdapter(
            presenter::onBaseCurrencyAmountChanged, presenter::onBaseCurrencyChanged)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ratesRecycler = view.findViewById<RecyclerView>(R.id.rv_rates)
        with(ratesRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ratesAdapter
        }
    }

    override fun renderState(viewState: RatesViewState) {
        ratesAdapter.updateState(viewState)
    }

    override fun getComponent(): RatesComponent = RatesComponent.Initializer.init()

    private fun initDependencies() {
        XInjectionManager.bindComponent(this).inject(this)
    }
}
