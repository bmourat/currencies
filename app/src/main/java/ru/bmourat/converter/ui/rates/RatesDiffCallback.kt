package ru.bmourat.converter.ui.rates

import androidx.recyclerview.widget.DiffUtil

class RatesDiffCallback(
    private val oldItems: Pair<Boolean, List<RateViewModel>>,
    private val newItems: Pair<Boolean, List<RateViewModel>>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems.second[oldItemPosition].currencyShortName == newItems.second[newItemPosition].currencyShortName
    }

    override fun getOldListSize(): Int = oldItems.second.size

    override fun getNewListSize(): Int = newItems.second.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldItems.first == newItems.first) {
            oldItems.second[oldItemPosition].currencyRate.rate == newItems.second[newItemPosition].currencyRate.rate
        } else {
            false
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return Payload.AMOUNT
    }

    enum class Payload {
        AMOUNT
    }
}