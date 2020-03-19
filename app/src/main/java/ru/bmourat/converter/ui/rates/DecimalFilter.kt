package ru.bmourat.converter.ui.rates

import android.text.InputFilter
import android.text.Spanned


class DecimalFilter(private val decimalDigits: Int) : InputFilter {
    override fun filter(source: CharSequence, i: Int, i2: Int, spanned: Spanned, i3: Int, i4: Int): CharSequence? {
        var dotPos = -1
        val len = spanned.length
        for (decimalsI in 0 until len) {
            val c = spanned[decimalsI]
            if (c == '.' || c == ',') {
                dotPos = decimalsI
                break
            }
        }
        if (dotPos >= 0) {
            // protects against many dots
            if (source == "." || source == ",") {
                return ""
            }
            // if the text is entered before the dot
            if (i4 <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        }
        return null
    }

}