package ru.bmourat.converter.domain.repositories

import ru.bmourat.converter.domain.model.CurrencyCode
import ru.bmourat.converter.domain.model.CurrencyDisplayInfo

interface CurrencyDisplayInfoRepository {
    fun currencyDisplayInfo(currencyCode: CurrencyCode): CurrencyDisplayInfo
}