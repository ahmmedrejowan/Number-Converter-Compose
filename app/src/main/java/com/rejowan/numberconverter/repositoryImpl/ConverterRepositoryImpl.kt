package com.rejowan.numberconverter.repositoryImpl

import android.content.Context
import android.util.Log
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