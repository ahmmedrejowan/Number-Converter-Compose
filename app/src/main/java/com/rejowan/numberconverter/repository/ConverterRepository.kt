package com.rejowan.numberconverter.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

class ConverterRepository(private val context: Context) {

    private val _output = MutableLiveData<String?>()
    val output: MutableLiveData<String?> get() = _output

    fun convert(input: String, fromBase: Int, toBase: Int) {
        _output.value = ""
        try {
            val parsedValue = toBaseTen(input, fromBase)
            _output.value = toBase(parsedValue.first, parsedValue.second, toBase)
        } catch (e: Throwable) {
            _output.value = null
        }
    }

    private fun toBaseTen(input: String, fromBase: Int): Pair<BigInteger, BigDecimal?> {
        val parts = input.uppercase().split(".")

        val integerResult: BigInteger = parts.getOrNull(0)?.let { integerPart ->
            integerPart.foldIndexed(BigInteger.ZERO) { i, acc, c ->
                val u = c.code
                val digitValue = when (c) {
                    in '0'..'9' -> u - '0'.code
                    in 'A'..'Z' -> u - 'A'.code + 10
                    else -> throw Exception()
                }
                if (digitValue >= fromBase) throw Exception()
                acc + BigInteger.valueOf(digitValue.toLong()).multiply(
                    BigInteger.valueOf(fromBase.toLong()).pow(integerPart.length - (i + 1))
                )
            }
        } ?: BigInteger.ZERO


        val fractionResult: BigDecimal? = parts.getOrNull(1)?.let { fractionPart ->
            fractionPart.foldIndexed(BigDecimal.ZERO) { i, acc, c ->
                val u = c.code
                val digitValue = when (c) {
                    in '0'..'9' -> u - '0'.code
                    in 'A'..'Z' -> u - 'A'.code + 10
                    else -> throw Exception()
                }

                if (digitValue >= fromBase) throw Exception()
                acc + BigDecimal.valueOf(digitValue.toLong()).multiply(
                    BigDecimal.valueOf(fromBase.toDouble()).pow(-i - 1, MathContext.DECIMAL64)
                )
            }
        }

        return Pair(integerResult, fractionResult)
    }


    private fun toBase(intPart: BigInteger, fracPart: BigDecimal?, toBase: Int): String {
        val integerPart = convertIntegerToBase(intPart, toBase)
        val fractionPart = fracPart?.let { convertFractionToBase(it, toBase) }
        return fractionPart?.let { "$integerPart.$fractionPart" } ?: integerPart
    }

    private fun convertFractionToBase(fraction: BigDecimal, radix: Int): String {
        var fraction1 = fraction
        var result = ""
        val base = BigDecimal(radix)
        for (i in 0 until 10) {
            fraction1 = fraction1.multiply(base)
            val x = fraction1.toBigInteger()
            var c = ('0'.code + x.toInt()).toChar()
            if (x.compareTo(BigInteger.valueOf(9)) == 1) {
                c = ('A'.code + (x.toInt() - 10)).toChar()
            }
            result += c
            fraction1 = fraction1.subtract(BigDecimal.valueOf(x.toInt().toLong()))
            if (fraction1.compareTo(BigDecimal.ZERO) == 0) break
        }
        return result
    }

    private fun convertIntegerToBase(integer: BigInteger, radix: Int): String {
        var integer1 = integer
        var result = ""
        while (integer1.compareTo(BigInteger.ZERO) != 0) {
            val division = integer1.divideAndRemainder(BigInteger.valueOf(radix.toLong()))
            val remainder = division[1].toInt()
            var c = ('0'.code + remainder).toChar()
            if (remainder > 9) {
                val u = remainder - 10
                c = ('A'.code + u).toChar()
            }
            result = c.toString() + result
            integer1 = division[0]
        }
        if (result.isEmpty()) result = "0"
        return result
    }


    //    private fun parseToBaseTenWithDetails(
//        input: String, fromBase: Int
//    ): Triple<BigInteger, BigDecimal?, Spanned> {
//        val parts = input.uppercase().split(".")
//
//        val integerFirstStep = StringBuilder()
//        val integerSecondStep = StringBuilder()
//
//        val fractionalFirstStep = StringBuilder()
//        val fractionalSecondStep = StringBuilder()
//
//
//        val integerResult: BigInteger = parts.getOrNull(0)?.let { integerPart ->
//            integerPart.foldIndexed(BigInteger.ZERO) { i, acc, c ->
//                val u = c.code
//                val digitValue = when (c) {
//                    in '0'..'9' -> u - '0'.code
//                    in 'A'..'Z' -> u - 'A'.code + 10
//                    else -> throw Exception("Invalid character for base $fromBase: $c")
//                }
//                if (digitValue >= fromBase) throw Exception("Digit value $digitValue out of range for base $fromBase")
//
//                val stepValue = BigInteger.valueOf(digitValue.toLong()).multiply(
//                    BigInteger.valueOf(fromBase.toLong()).pow(integerPart.length - (i + 1))
//                )
//
//                integerFirstStep.append("$digitValue×$fromBase<sup>${integerPart.length - (i + 1)}</sup>")
//                integerFirstStep.append(if (i == integerPart.length - 1) "" else " + ")
//
//                integerSecondStep.append(stepValue)
//                integerSecondStep.append(if (i == integerPart.length - 1) "" else " + ")
//
//
//                acc + stepValue
//            }
//        } ?: BigInteger.ZERO
//
//
//        val fractionResult: BigDecimal? = parts.getOrNull(1)?.let { fractionPart ->
//            fractionPart.foldIndexed(BigDecimal.ZERO) { i, acc, c ->
//                val u = c.code
//                val digitValue = when (c) {
//                    in '0'..'9' -> u - '0'.code
//                    in 'A'..'Z' -> u - 'A'.code + 10
//                    else -> throw Exception("Invalid character for base $fromBase: $c")
//                }
//                if (digitValue >= fromBase) throw Exception("Digit value $digitValue out of range for base $fromBase")
//
//                val stepValue = BigDecimal.valueOf(digitValue.toLong()).multiply(
//                    BigDecimal.valueOf(fromBase.toDouble()).pow(-i - 1, MathContext.DECIMAL64)
//                )
//
//                fractionalFirstStep.append("$digitValue×$fromBase<sup>${-i - 1}</sup>")
//                fractionalFirstStep.append(if (i == fractionPart.length - 1) "" else " + ")
//
//                fractionalSecondStep.append(stepValue)
//                fractionalSecondStep.append(if (i == fractionPart.length - 1) "" else " + ")
//
//                acc + stepValue
//            }
//        }
//
//        val firstSteps =
//            integerFirstStep.toString() + (if (fractionalFirstStep.isNotEmpty()) " + $fractionalFirstStep" else "")
//        val secondSteps =
//            integerSecondStep.toString() + (if (fractionalSecondStep.isNotEmpty()) " + $fractionalSecondStep" else "")
//
//        val steps = "$firstSteps \n= $secondSteps"
//
//        return Triple(
//            integerResult, fractionResult, Html.fromHtml(steps, Html.FROM_HTML_MODE_COMPACT)
//        )
//
//    }


}