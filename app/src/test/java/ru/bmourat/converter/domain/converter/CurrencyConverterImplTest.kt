package ru.bmourat.converter.domain.converter

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyConverterImplTest {

    private val resultScale = 2
    private val sut = CurrencyConverterImpl(resultScale)

    @Test
    fun `zero is returned when fromAmount is less or equal to zero`() {
        //When
        var result = sut.convert(BigDecimal(-1), BigDecimal.ONE, BigDecimal.ONE)

        //Then
        assertEquals(result, BigDecimal.ZERO)

        //When
        result = sut.convert(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE)

        //Then
        assertEquals(result, BigDecimal.ZERO)
    }

    @Test
    fun `zero is returned when fromRate is less or equal to zero`() {
        //When
        var result = sut.convert(BigDecimal.ONE, BigDecimal(-1), BigDecimal.ONE)

        //Then
        assertEquals(result, BigDecimal.ZERO)

        //When
        result = sut.convert(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE)

        //Then
        assertEquals(result, BigDecimal.ZERO)
    }

    @Test
    fun `zero is returned when toRate is less or equal to zero`() {
        //When
        var result = sut.convert(BigDecimal.ONE, BigDecimal.ONE, BigDecimal(-1))

        //Then
        assertEquals(result, BigDecimal.ZERO)

        //When
        result = sut.convert(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO)

        //Then
        assertEquals(result, BigDecimal.ZERO)
    }

    @Test
    fun `right value is returned when all input parameters are valid`() {
        //Given
        var fromAmount = bigDecimal("100")
        var fromRate = BigDecimal.ONE
        var toRate = bigDecimal("1.16")
        //When
        var result = sut.convert(fromAmount, fromRate, toRate)

        //Then
        assertEquals(result, bigDecimal("116.00"))

        //Given
        fromAmount = bigDecimal("100")
        fromRate = bigDecimal("0.87")
        toRate = bigDecimal("1.16")

        //When
        result = sut.convert(fromAmount, fromRate, toRate)

        //Then
        assertEquals(result, bigDecimal("133.33"))
    }

    private fun bigDecimal(valueStr: String): BigDecimal {
        return BigDecimal(valueStr).setScale(resultScale)
    }
}