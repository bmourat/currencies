package ru.bmourat.converter.data

import android.content.res.Resources
import ru.bmourat.converter.R
import ru.bmourat.converter.domain.model.CurrencyCode
import ru.bmourat.converter.domain.model.CurrencyDisplayInfo
import ru.bmourat.converter.domain.repositories.CurrencyDisplayInfoRepository

class CurrencyDisplayInfoRepositoryImpl(resources: Resources)
    : CurrencyDisplayInfoRepository {
    private val displayInfoMap = mutableMapOf<String, CurrencyDisplayInfo>()

    init {
        initMap(resources)
    }

    override fun currencyDisplayInfo(currencyCode: CurrencyCode): CurrencyDisplayInfo {
        val result = displayInfoMap[currencyCode]
        return result ?: CurrencyDisplayInfo(currencyCode, "", R.drawable.ic_currency_unknown)
    }

    private fun initMap(resources: Resources) {
        val codes = resources.obtainTypedArray(R.array.currency_codes)
        val names = resources.obtainTypedArray(R.array.currency_names)
        val flags = resources.obtainTypedArray(R.array.currency_flags)
        for(i in 0 until codes.length()) {
            val code = codes.getString(i)
            val name = names.getString(i)
            val flag = flags.getResourceId(i, 0)
            if (code != null && name != null && flag != 0) {
                displayInfoMap[code] = CurrencyDisplayInfo(code, name, flag)
            }
        }
        codes.recycle()
        names.recycle()
        flags.recycle()
    }
}