package ru.bmourat.converter.ui.rates

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import me.vponomarenko.injectionmanager.IHasComponent
import me.vponomarenko.injectionmanager.x.XInjectionManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.bmourat.converter.R
import ru.bmourat.converter.ui.rates.adapter.RatesAdapter
import ru.bmourat.converter.ui.rates.di.RatesComponent
import ru.bmourat.converter.ui.rates.mvp.RatesPresenter
import ru.bmourat.converter.ui.rates.mvp.RatesView
import ru.bmourat.converter.ui.rates.mvp.RatesViewState
import javax.inject.Inject
import javax.inject.Provider


class RatesFragment :
    MvpAppCompatFragment(R.layout.fragment_converter),
    RatesView,
    IHasComponent<RatesComponent> {

    @Inject
    lateinit var presenterProvider: Provider<RatesPresenter>
    private val presenter by moxyPresenter { presenterProvider.get() }

    private lateinit var ratesRecycler: RecyclerView
    private lateinit var ratesAdapter: RatesAdapter
    private lateinit var progress: ProgressBar

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initDependencies()
        super.onCreate(savedInstanceState)
        ratesAdapter = RatesAdapter(
            presenter::onBaseCurrencyAmountChanged, presenter::onBaseCurrencyChanged
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratesRecycler = view.findViewById(R.id.rv_rates)
        progress = view.findViewById(R.id.pb_progress)
        with(ratesRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ratesAdapter
        }
    }

    override fun renderState(viewState: RatesViewState) {
        ratesAdapter.updateState(viewState)
        renderErrorsIfAny(viewState)
        renderProgress(viewState)
        if (viewState.forceBaseCurrencyFocus) {
            ratesRecycler.scrollToPosition(0)
        }
    }

    private fun renderErrorsIfAny(viewState: RatesViewState) {
        if (viewState.hasNetworkError) {
            val message =
                if (viewState.viewModels.isEmpty()) R.string.network_connection_error
                else R.string.network_connection_error_with_result
            showError(message)
        } else {
            hideError()
        }
    }

    private fun renderProgress(viewState: RatesViewState) {
        progress.visibility = if (viewState.viewModels.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showError(@StringRes errorMessage: Int) {
        if (snackBar == null) {
            snackBar =
                Snackbar.make(requireView(), resources.getString(errorMessage), LENGTH_INDEFINITE)
            snackBar?.show()
        }
    }

    private fun hideError() {
        snackBar?.dismiss()
        snackBar = null
    }

    override fun getComponent(): RatesComponent = RatesComponent.Initializer.init()

    private fun initDependencies() {
        XInjectionManager.bindComponent(this).inject(this)
    }
}
