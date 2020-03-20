package ru.bmourat.converter.ui.rates.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.bmourat.converter.R
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.ui.rates.mvp.RateViewModel
import ru.bmourat.converter.ui.rates.mvp.RatesViewState
import ru.bmourat.converter.utils.DecimalFilter
import ru.bmourat.converter.utils.showKeyboard

class RatesAdapter(
    private val baseCurrencyAmountChanged: (newAmount: String) -> Unit,
    private val baseCurrencyChanged: (newBaseCurrency: CurrencyRate) -> Unit
) : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    private var forceBaseCurrencyFocus = false
    private var ratesState = RatesViewState(
        hasNetworkError = false,
        hasInputFormatError = false,
        forceBaseCurrencyFocus = false,
        baseCurrencyAmount = "",
        viewModels = emptyList()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_rate_item, parent, false)
        return RateViewHolder(view)
    }

    override fun getItemCount(): Int = ratesState.viewModels.size

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(ratesState.viewModels[position], position)
    }

    override fun onBindViewHolder(
        holder: RateViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for (payload in payloads) {
                when (payload) {
                    RatesDiffCallback.Payload.AMOUNT -> holder.bindAmount(
                        ratesState.viewModels[position],
                        position
                    )
                }
            }
        }
    }

    fun updateState(newRatesState: RatesViewState) {
        val diffResult = DiffUtil.calculateDiff(
            RatesDiffCallback(
                forceBaseCurrencyFocus to ratesState.viewModels,
                newRatesState.forceBaseCurrencyFocus to newRatesState.viewModels
            )
        )
        ratesState = newRatesState
        forceBaseCurrencyFocus = newRatesState.forceBaseCurrencyFocus
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RateViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val currencyFlag = view.findViewById<ImageView>(R.id.currency_flag)
        private val currencyCode = view.findViewById<TextView>(R.id.currency_code)
        private val currencyName = view.findViewById<TextView>(R.id.currency_name)
        private val currencyAmount = view.findViewById<EditText>(R.id.currency_amount)
        private val clickableArea = view.findViewById<FrameLayout>(R.id.clickable_area)

        private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                baseCurrencyAmountChanged(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fun bind(model: RateViewModel, position: Int) {
            currencyFlag.setImageResource(model.currencyFlag)
            currencyCode.text = model.currencyShortName
            currencyName.text = model.currencyName
            bindAmount(model, position)
        }

        fun bindAmount(model: RateViewModel, position: Int) {
            if (position == 0) bindBaseCurrency(model) else bindCurrency(model)
        }

        private fun bindCurrency(model: RateViewModel) {
            val rate = model.currencyRate.rate.toString()
            with(currencyAmount) {
                error = null
                isEnabled = false
                removeTextChangedListener(textWatcher)
                setText(rate)
                setOnClickListener {
                    baseCurrencyChanged(model.currencyRate)
                }
            }
            with(clickableArea) {
                visibility = View.VISIBLE
                setOnClickListener { baseCurrencyChanged(model.currencyRate) }
            }
        }

        private fun bindBaseCurrency(model: RateViewModel) {
            with(currencyAmount) {
                isEnabled = true
                removeTextChangedListener(textWatcher)
                if (!isFocused) {
                    setText(ratesState.baseCurrencyAmount)
                    setSelection(ratesState.baseCurrencyAmount.length)
                }
                if (forceBaseCurrencyFocus) {
                    showKeyboard()
                    forceBaseCurrencyFocus = false
                }
                error = if (ratesState.hasInputFormatError) {
                    itemView.resources.getString(R.string.input_format_error)
                } else {
                    null
                }
                addTextChangedListener(textWatcher)
                filters = arrayOf(DecimalFilter(2))
                setOnClickListener(null)
            }
            with(clickableArea) {
                visibility = View.GONE
                setOnClickListener(null)
            }
        }
    }
}