package com.rejowan.numberconverter.repositoryImpl

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.BaselineShift.Companion.Superscript
import androidx.compose.ui.text.substring
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rejowan.numberconverter.repository.ConverterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.math.pow


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "decimal_pref")

class ConverterRepositoryImpl(context: Context) : ConverterRepository {

    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        val DECIMAL_PLACES_KEY = intPreferencesKey("decimal_places")
    }


    override suspend fun convert(input: String, fromBase: Int, toBase: Int): String? {
        return try {
            val parsedValue = toBaseTen(input, fromBase)
            val decimalPlaces = getDecimalPlaces()
            toBase(parsedValue.first, parsedValue.second, toBase, decimalPlaces)

        } catch (e: Throwable) {
            null
        }
    }

    override suspend fun explain(input: String, fromBase: Int, toBase: Int): AnnotatedString {

        when (fromBase) {
            2 -> {
                when (toBase) {
                    2 -> {
                        return AnnotatedString("The number is already in base 2")
                    }

                    8 -> {
                        return AnnotatedString("First convert the number to base 10, then convert it to base 8")
                    }

                    10 -> {
                        return binToDecimal(input)
                    }

                    16 -> {
                        return AnnotatedString("First convert the number to base 10, then convert it to base 16")
                    }
                }

            }


        }




        return AnnotatedString("")
    }

    private fun binToDecimal(input: String): AnnotatedString {

        var decimalIntegral = 0
        var decimalFractional = 0.0

        val parts = input.uppercase().split(".")
        val integralPart = parts[0]
        val fractionalPart = parts.getOrNull(1) ?: ""

        val integralAnnotatedString = AnnotatedString.Builder()
        val fractionalAnnotatedString = AnnotatedString.Builder()

        val integralAnnPart1 = AnnotatedString.Builder()
        val integralAnnPart2 = AnnotatedString.Builder()

        val fractionalAnnPart1 = AnnotatedString.Builder()
        val fractionalAnnPart2 = AnnotatedString.Builder()


        integralPart.forEachIndexed { index, c ->
            val bit = c.toString().toInt()
            val position = integralPart.length - index - 1
            val value = bit * 2.0.pow(position).toInt()

            decimalIntegral += value

            if (index != 0) {
                integralAnnPart1.append(" + ")
                integralAnnPart2.append(" + ")
            }

            integralAnnPart1.append("($bit x 2")
            integralAnnPart1.appendSuperscript("$position)")

            integralAnnPart2.append("$value")

        }

        if (fractionalPart.isNotEmpty()) {
            fractionalPart.forEachIndexed { index, c ->
                val bit = c.toString().toInt()
                val position = -index - 1
                val value = bit * 2.0.pow(position)

                decimalFractional += value

                if (index != 0) {
                    fractionalAnnPart1.append(" + ")
                    fractionalAnnPart2.append(" + ")
                }

                fractionalAnnPart1.append("($bit x 2")
                fractionalAnnPart1.appendSuperscript("$position)")

                fractionalAnnPart2.append("$value")
            }
        }


        integralAnnotatedString.appendBold("Integral Part\n\n")
        integralAnnotatedString.appendSemiBold(integralPart)
        integralAnnotatedString.appendSmall(" (Bin)\n= ")
        integralAnnotatedString.append(integralAnnPart1.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.append(integralAnnPart2.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.appendSemiBold("$decimalIntegral")
        integralAnnotatedString.appendSmall(" (Dec)")


        if (fractionalPart.isNotEmpty()) {
            fractionalAnnotatedString.appendBold("\n\n\nFractional Part\n\n0.")
            fractionalAnnotatedString.appendSemiBold(fractionalPart)
            fractionalAnnotatedString.appendSmall(" (Bin)\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart1.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart2.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.appendSemiBold("$decimalFractional")
            fractionalAnnotatedString.appendSmall(" (Dec)")
        }


        val result = AnnotatedString.Builder()
        result.append("\n\n\n")
        result.appendBold("Result\n\n")
        result.append(input)
        result.appendSmall(" (Bin)\n= ")
        result.append("$decimalIntegral")
        if (fractionalPart.isNotEmpty()) {
            result.append(".")
            result.append(decimalFractional.toString().substring(2))
        }
        result.appendSmall(" (Dec)")


        val finalAnnotatedString = AnnotatedString.Builder()
        finalAnnotatedString.append(integralAnnotatedString.toAnnotatedString())
        finalAnnotatedString.append(fractionalAnnotatedString.toAnnotatedString())
        finalAnnotatedString.append(result.toAnnotatedString())

        return finalAnnotatedString.toAnnotatedString()
    }


    fun AnnotatedString.Builder.appendSuperscript(text: String) {
        withStyle(style = SpanStyle(fontSize = 12.sp, baselineShift = Superscript)) {
            append(text)
        }
    }

    fun AnnotatedString.Builder.appendSubscript(text: String) {
        withStyle(style = SpanStyle(fontSize = 12.sp, baselineShift = BaselineShift.Subscript)) {
            append(text)
        }
    }

    fun AnnotatedString.Builder.appendBold(text: String) {
        withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)) {
            append(text)
        }
    }

    fun AnnotatedString.Builder.appendSmall(text: String) {
        withStyle(style = SpanStyle(fontSize = 12.sp)) {
            append(text)
        }
    }

    fun AnnotatedString.Builder.appendSemiBold(text: String) {
        withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
            append(text)
        }
    }


    override suspend fun setDecimalPlaces(decimalPlaces: Int) {
        Log.e("decimalPlaces", decimalPlaces.toString())
        dataStore.edit { settings ->
            settings[DECIMAL_PLACES_KEY] = decimalPlaces
        }
    }

    override suspend fun getDecimalPlaces(): Int {
        val decimalPlacesFlow: Flow<Int> = dataStore.data.map { preferences ->
            preferences[DECIMAL_PLACES_KEY] ?: 15
        }

        Log.e("decimalPlacesFlow", decimalPlacesFlow.first().toString())
        return decimalPlacesFlow.first()
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

    private fun toBase(
        intPart: BigInteger, decimalPart: BigDecimal?, toBase: Int, decimalPlaces: Int
    ): String {
        val integerPart = convertIntegerToBase(intPart, toBase)
        val fractionPart = decimalPart?.let { convertFractionToBase(it, toBase, decimalPlaces) }
        return fractionPart?.let { "$integerPart.$fractionPart" } ?: integerPart
    }

    private fun convertFractionToBase(
        fraction: BigDecimal, radix: Int, decimalPlaces: Int
    ): String {
        var fraction1 = fraction
        var result = ""
        val base = BigDecimal(radix)
        for (i in 0 until decimalPlaces) {
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


}