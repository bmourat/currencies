package ru.bmourat.converter.ui.rates

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.bmourat.converter.R
import ru.bmourat.converter.domain.model.CurrencyRate
import ru.bmourat.converter.utils.setReadOnly

class RatesAdapter(
    private val baseCurrencyAmountChanged: (newAmount: String) -> Unit,
    private val baseCurrencyChanged: (newBaseCurrency: CurrencyRate) -> Unit)
    : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    private var items: MutableList<RateViewModel> = mutableListOf()
    private var updateBaseCurrencyAmount = true
    private var hasInputFormatError = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_rate_item, parent, false)
        return RateViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) = holder.bind(items[position])

    override fun onBindViewHolder(holder: RateViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for(payload in payloads) {
                when (payload) {
                    RatesDiffCallback.Payload.AMOUNT -> holder.bindAmount(items[position])
                }
            }
        }
    }

    fun updateState(ratesState: RatesViewState) {
        val diffResult = DiffUtil.calculateDiff(RatesDiffCallback(items, ratesState.viewModels))
        items.clear()
        items.addAll(ratesState.viewModels)
        updateBaseCurrencyAmount = ratesState.updateBaseCurrencyAmount
        hasInputFormatError = ratesState.hasInputFormatError
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RateViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val currencyFlag = view.findViewById<ImageView>(R.id.currency_flag)
        private val currencyCode = view.findViewById<TextView>(R.id.currency_code)
        private val currencyName = view.findViewById<TextView>(R.id.currency_name)
        private val currencyAmount = view.findViewById<EditText>(R.id.currency_amount)

        private val textWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                baseCurrencyAmountChanged(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fun bind(model: RateViewModel) {
            currencyFlag.setImageResource(model.currencyFlag)
            currencyCode.text = model.currencyShortName
            currencyName.text = model.currencyName
            bindAmount(model)
        }

        fun bindAmount(model: RateViewModel) {
            val rate = model.currencyRate.rate.toString()
            if (adapterPosition == 0) bindBaseCurrency(rate) else bindCurrency(rate)
        }

        private fun bindBaseCurrency(rate: String) {
            currencyAmount.setReadOnly(false)
            if (updateBaseCurrencyAmount) {
                currencyAmount.setText(rate)
                currencyAmount.setSelection(rate.length)
            }
            currencyAmount.addTextChangedListener(textWatcher)
            if (hasInputFormatError) {
                currencyAmount.error = itemView.resources.getString(R.string.input_format_error)
            } else {
                currencyAmount.error = null
            }
            currencyAmount.filters = arrayOf(DecimalFilter(2))
        }

        private fun bindCurrency(rate: String) {
            currencyAmount.setReadOnly(true)
            currencyAmount.removeTextChangedListener(textWatcher)
            currencyAmount.setText(rate)
            currencyAmount.setSelection(rate.length)
        }
    }
}