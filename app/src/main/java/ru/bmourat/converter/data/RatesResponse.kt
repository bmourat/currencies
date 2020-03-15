package ru.bmourat.converter.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RatesResponse (
    @SerializedName("baseCurrency") val baseCurrency: String,
    @SerializedName("rates") val rates: HashMap<String, BigDecimal>
)