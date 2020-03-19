package ru.bmourat.converter.ui.rates

import androidx.recyclerview.widget.DiffUtil

class RatesDiffCallback(
    private val oldItems: List<RateViewModel>,
    private val newItems: List<RateViewModel>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].currencyShortName == newItems[newItemPosition].currencyShortName
    }

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].currencyRate.rate == newItems[newItemPosition].currencyRate.rate
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return Payload.AMOUNT
    }

    enum class Payload {
        AMOUNT
    }
}