package com.rejowan.numberconverter.repositoryImpl

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift.Companion.Superscript
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
import java.math.RoundingMode
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

    override suspend fun explain(
        input: String, fromBase: Int, toBase: Int
    ): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        when (fromBase) {
            2 -> {
                when (toBase) {
                    2 -> {
                        return Triple(
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same")
                        )
                    }

                    8 -> {
                        return binToOct(input)
                    }

                    10 -> {
                        return binToDec(input).first
                    }

                    16 -> {
                        return binToHex(input)
                    }
                }

            }

            8 -> {
                when (toBase) {
                    2 -> {
                        return octToBin(input)
                    }

                    8 -> {
                        return Triple(
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same")
                        )
                    }

                    10 -> {
                        return octToDec(input).first
                    }

                    16 -> {
                        return octToHex(input)
                    }

                }
            }

            10 -> {
                when (toBase) {
                    2 -> {
                        return decToBin(input).first
                    }

                    8 -> {
                        return decToOct(input).first
                    }

                    10 -> {
                        return Triple(
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same")
                        )
                    }

                    16 -> {
                        return decToHex(input).first
                    }

                }
            }

            16 -> {
                when (toBase) {
                    2 -> {
                        return hexToBin(input)
                    }

                    8 -> {
                        return hexToOct(input)
                    }

                    10 -> {
                        return hexToDec(input).first
                    }

                    16 -> {
                        return Triple(
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same"),
                            AnnotatedString("Both bases are same")
                        )
                    }

                }
            }

        }

        return Triple(AnnotatedString(""), AnnotatedString(""), AnnotatedString(""))
    }

    private suspend fun binToOct(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val binToDecimal = binToDec(input)
        val decimalNumber =
            binToDecimal.second.first + if (binToDecimal.second.second != null) ".${binToDecimal.second.second}" else ""
        val decimalToOctal = decToOct(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Binary to Decimal\n")
        annotatedIntegral.append(binToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Octal\n")
        annotatedIntegral.append(decimalToOctal.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (binToDecimal.first.second != null) {
            annotatedFractional.appendSubTitle("Binary to Decimal\n")
            annotatedFractional.append(binToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Octal\n")
            annotatedFractional.append(decimalToOctal.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Bin)\n= ")
        result.appendSubTitle(decimalToOctal.second.first)
        if (decimalToOctal.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalToOctal.second.second.toString())
        }
        result.appendSmall(" (Oct)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private fun binToDec(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        var decimalIntegral = 0
        var decimalFractional = BigDecimal.ZERO

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
            val base = BigDecimal(2)
            val mc = MathContext.DECIMAL128

            fractionalPart.forEachIndexed { index, c ->
                val bit = c.toString().toInt()
                val position = -index - 1
                val exponent = base.pow(position, mc)
                val value = BigDecimal(bit).multiply(exponent, mc)

                decimalFractional = decimalFractional.add(value, mc)

                if (index != 0) {
                    fractionalAnnPart1.append(" + ")
                    fractionalAnnPart2.append(" + ")
                }

                fractionalAnnPart1.append("($bit x 2")
                fractionalAnnPart1.appendSuperscript("$position)")

                fractionalAnnPart2.append("${value.stripTrailingZeros()}")
            }
        }


        integralAnnotatedString.appendSubTitle(integralPart)
        integralAnnotatedString.appendSmall(" (Bin)\n= ")
        integralAnnotatedString.append(integralAnnPart1.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.append(integralAnnPart2.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.appendSubTitle("$decimalIntegral")
        integralAnnotatedString.appendSmall(" (Dec)")


        if (fractionalPart.isNotEmpty()) {
            fractionalAnnotatedString.appendSubTitle(fractionalPart)
            fractionalAnnotatedString.appendSmall(" (Bin)\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart1.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart2.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.appendSubTitle("${decimalFractional.stripTrailingZeros()}")
            fractionalAnnotatedString.appendSmall(" (Dec)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Bin)\n= ")
        result.appendSubTitle("$decimalIntegral")
        if (fractionalPart.isNotEmpty()) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalFractional.stripTrailingZeros().toString().substring(2))
        }
        result.appendSmall(" (Dec)")


        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ), Pair(
                decimalIntegral.toString(),
                if (fractionalPart.isNotEmpty()) decimalFractional.toString().substring(2) else null
            )
        )
    }

    private suspend fun binToHex(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val binToDecimal = binToDec(input)
        val decimalNumber =
            binToDecimal.second.first + if (binToDecimal.second.second != null) ".${binToDecimal.second.second}" else ""
        val decimalToHex = decToHex(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Binary to Decimal\n")
        annotatedIntegral.append(binToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Hexadecimal\n")
        annotatedIntegral.append(decimalToHex.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (binToDecimal.first.second != null) {
            annotatedFractional.appendSubTitle("Binary to Decimal\n")
            annotatedFractional.append(binToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Hexadecimal\n")
            annotatedFractional.append(decimalToHex.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Bin)\n= ")
        result.appendSubTitle(decimalToHex.second.first)
        if (decimalToHex.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalToHex.second.second.toString())
        }
        result.appendSmall(" (Hex)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private suspend fun octToBin(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val octToDecimal = octToDec(input)
        val decimalNumber =
            octToDecimal.second.first + if (octToDecimal.second.second != null) ".${octToDecimal.second.second}" else ""
        val decimalToBin = decToBin(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Octal to Decimal\n")
        annotatedIntegral.append(octToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Binary\n")
        annotatedIntegral.append(decimalToBin.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (octToDecimal.first.second != null) {
            annotatedFractional.appendSubTitle("Octal to Decimal\n")
            annotatedFractional.append(octToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Binary\n")
            annotatedFractional.append(decimalToBin.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Oct)\n= ")
        result.appendSubTitle(decimalToBin.second.first)
        if (decimalToBin.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalToBin.second.second.toString())
        }
        result.appendSmall(" (Bin)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private fun octToDec(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        var decimalIntegral = 0
        var decimalFractional = BigDecimal.ZERO

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
            val value = bit * 8.0.pow(position).toInt()

            decimalIntegral += value

            if (index != 0) {
                integralAnnPart1.append(" + ")
                integralAnnPart2.append(" + ")
            }

            integralAnnPart1.append("($bit x 8")
            integralAnnPart1.appendSuperscript("$position)")

            integralAnnPart2.append("$value")

        }


        if (fractionalPart.isNotEmpty()) {
            val base = BigDecimal(8)
            val mc = MathContext.DECIMAL128

            fractionalPart.forEachIndexed { index, c ->
                val bit = c.toString().toInt()
                val position = -index - 1
                val exponent = base.pow(position, mc)
                val value = BigDecimal(bit).multiply(exponent, mc)

                decimalFractional = decimalFractional.add(value, mc)

                if (index != 0) {
                    fractionalAnnPart1.append(" + ")
                    fractionalAnnPart2.append(" + ")
                }

                fractionalAnnPart1.append("($bit x 8")
                fractionalAnnPart1.appendSuperscript("$position)")

                fractionalAnnPart2.append("$value")
            }
        }


        integralAnnotatedString.appendSubTitle(integralPart)
        integralAnnotatedString.appendSmall(" (Oct)\n= ")
        integralAnnotatedString.append(integralAnnPart1.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.append(integralAnnPart2.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.appendSubTitle("$decimalIntegral")
        integralAnnotatedString.appendSmall(" (Dec)")


        if (fractionalPart.isNotEmpty()) {
            fractionalAnnotatedString.appendSubTitle(fractionalPart)
            fractionalAnnotatedString.appendSmall(" (Oct)\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart1.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart2.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.appendSubTitle("$decimalFractional")
            fractionalAnnotatedString.appendSmall(" (Dec)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Oct)\n= ")
        result.appendSubTitle("$decimalIntegral")
        if (fractionalPart.isNotEmpty()) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalFractional.toString().substring(2))
        }
        result.appendSmall(" (Dec)")


        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ), Pair(
                decimalIntegral.toString(),
                if (fractionalPart.isNotEmpty()) decimalFractional.toString().substring(2) else null
            )
        )
    }

    private suspend fun octToHex(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val octToDecimal = octToDec(input)
        val decimalNumber =
            octToDecimal.second.first + if (octToDecimal.second.second != null) ".${octToDecimal.second.second}" else ""
        val decimalToHex = decToHex(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Octal to Decimal\n")
        annotatedIntegral.append(octToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Hexadecimal\n")
        annotatedIntegral.append(decimalToHex.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (!octToDecimal.first.second.isNullOrEmpty()) {
            annotatedFractional.appendSubTitle("Octal to Decimal\n")
            annotatedFractional.append(octToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Hexadecimal\n")
            annotatedFractional.append(decimalToHex.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Oct)\n= ")
        result.appendSubTitle(decimalToHex.second.first)
        if (decimalToHex.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalToHex.second.second.toString())
        }
        result.appendSmall(" (Hex)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private suspend fun decToBin(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        val decimalPlaces = getDecimalPlaces()

        val parts = input.uppercase().split(".")

        val integralAnnotatedString = AnnotatedString.Builder()
        val integralString = StringBuilder()

        val fractionalAnnotatedString = AnnotatedString.Builder()
        val fractionalString = StringBuilder()

        var integralPart = BigInteger(parts[0])

        while (integralPart > BigInteger.ZERO) {
            val originalValue = integralPart
            val remainder = integralPart.rem(BigInteger.valueOf(2))
            integralString.insert(0, remainder)
            integralPart = integralPart.div(BigInteger.valueOf(2))

            integralAnnotatedString.append("• $originalValue÷2=$integralPart (Remainder = ")
            integralAnnotatedString.appendSubTitle("$remainder")
            integralAnnotatedString.append(")\n")
        }


        var steps = 0

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            var fractionalPartTemp = fractionalPart
            val base = BigDecimal(2)
            val mc = MathContext.DECIMAL128

            while (fractionalPartTemp > BigDecimal.ZERO && steps < decimalPlaces) {
                val originalValue = fractionalPartTemp
                fractionalPartTemp = fractionalPartTemp.multiply(base, mc)
                val integerPart = fractionalPartTemp.setScale(0, RoundingMode.FLOOR)
                fractionalString.append(integerPart)

                fractionalAnnotatedString.append("• $originalValue×2=$fractionalPartTemp (Subtract Int part = ")
                fractionalAnnotatedString.appendSubTitle("$integerPart")
                fractionalAnnotatedString.append(")\n")
                fractionalPartTemp = fractionalPartTemp.subtract(integerPart, mc)
                steps++

            }

        }

        integralAnnotatedString.appendSmall("\nCounting from bottom to top, the remainders are the octal equivalent of the decimal number\n So, ")
        integralAnnotatedString.appendSubTitle("${parts[0]} ")
        integralAnnotatedString.appendSmall("(Dec)")
        integralAnnotatedString.append(" = ")
        integralAnnotatedString.appendSubTitle(integralString.toString())
        integralAnnotatedString.appendSmall(" (Oct)")

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            fractionalAnnotatedString.appendSmall("\nCounting from top to bottom, the integer parts are the octal equivalent of the decimal number\n So, ")
            fractionalAnnotatedString.appendSubTitle("$fractionalPart ")
            fractionalAnnotatedString.appendSmall("(Dec)")
            fractionalAnnotatedString.append(" = ")
            fractionalAnnotatedString.appendSubTitle(fractionalString.toString())
            fractionalAnnotatedString.appendSmall(" (Oct)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Dec)\n= ")
        result.appendSubTitle(integralString.toString())
        if (parts.size == 2) {
            result.appendSubTitle(".")
            result.appendSubTitle(fractionalString.toString())
        }
        result.appendSmall(" (Oct)")

        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ), Pair(
                integralString.toString(),
                if (parts.size == 2) fractionalString.toString() else null
            )
        )


    }

    private suspend fun decToOct(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        val decimalPlaces = getDecimalPlaces()

        val parts = input.uppercase().split(".")

        val integralAnnotatedString = AnnotatedString.Builder()
        val integralString = StringBuilder()

        val fractionalAnnotatedString = AnnotatedString.Builder()
        val fractionalString = StringBuilder()

        var integralPart = BigInteger(parts[0])

        while (integralPart > BigInteger.ZERO) {
            val originalValue = integralPart
            val remainder = integralPart.rem(BigInteger.valueOf(8))
            integralString.insert(0, remainder)
            integralPart = integralPart.div(BigInteger.valueOf(8))

            integralAnnotatedString.append("• $originalValue÷8=$integralPart (Remainder = ")
            integralAnnotatedString.appendSubTitle("$remainder")
            integralAnnotatedString.append(")\n")
        }


        var steps = 0

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            var fractionalPartTemp = fractionalPart
            val base = BigDecimal(8)
            val mc = MathContext.DECIMAL128

            while (fractionalPartTemp > BigDecimal.ZERO && steps < decimalPlaces) {
                val originalValue = fractionalPartTemp
                fractionalPartTemp = fractionalPartTemp.multiply(base, mc)
                val integerPart = fractionalPartTemp.setScale(0, RoundingMode.FLOOR)
                fractionalString.append(integerPart)

                fractionalAnnotatedString.append("• $originalValue×8=$fractionalPartTemp (Subtract Int part = ")
                fractionalAnnotatedString.appendSubTitle("$integerPart")
                fractionalAnnotatedString.append(")\n")
                fractionalPartTemp = fractionalPartTemp.subtract(integerPart, mc)
                steps++

            }

        }

        integralAnnotatedString.appendSmall("\nCounting from bottom to top, the remainders are the octal equivalent of the decimal number\n So, ")
        integralAnnotatedString.appendSubTitle("${parts[0]} ")
        integralAnnotatedString.appendSmall("(Dec)")
        integralAnnotatedString.append(" = ")
        integralAnnotatedString.appendSubTitle(integralString.toString())
        integralAnnotatedString.appendSmall(" (Oct)")

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            fractionalAnnotatedString.appendSmall("\nCounting from top to bottom, the integer parts are the octal equivalent of the decimal number\n So, ")
            fractionalAnnotatedString.appendSubTitle("$fractionalPart ")
            fractionalAnnotatedString.appendSmall("(Dec)")
            fractionalAnnotatedString.append(" = ")
            fractionalAnnotatedString.appendSubTitle(fractionalString.toString())
            fractionalAnnotatedString.appendSmall(" (Oct)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Dec)\n= ")
        result.appendSubTitle(integralString.toString())
        if (parts.size == 2) {
            result.appendSubTitle(".")
            result.appendSubTitle(fractionalString.toString())
        }
        result.appendSmall(" (Oct)")

        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ),
            Pair(
                integralString.toString(),
                if (parts.size == 2) fractionalString.toString() else null
            )
        )

    }

    private suspend fun decToHex(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        val decimalPlaces = getDecimalPlaces()

        val parts = input.uppercase().split(".")

        val integralAnnotatedString = AnnotatedString.Builder()
        val integralString = StringBuilder()

        val fractionalAnnotatedString = AnnotatedString.Builder()
        val fractionalString = StringBuilder()

        var integralPart = BigInteger(parts[0])

        while (integralPart > BigInteger.ZERO) {
            val originalValue = integralPart
            val remainder = integralPart.rem(BigInteger.valueOf(16))
            integralString.insert(0, remainder.toString(16).uppercase())
            integralPart = integralPart.div(BigInteger.valueOf(16))

            integralAnnotatedString.append("• $originalValue÷16=$integralPart (Remainder = ")
            integralAnnotatedString.appendSubTitle(remainder.toString(16).uppercase())
            integralAnnotatedString.append(")\n")
        }


        var steps = 0

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            var fractionalPartTemp = fractionalPart
            val base = BigDecimal(16)
            val mc = MathContext.DECIMAL128

            while (fractionalPartTemp > BigDecimal.ZERO && steps < decimalPlaces) {
                val originalValue = fractionalPartTemp
                fractionalPartTemp = fractionalPartTemp.multiply(base, mc)
                val integerPart = fractionalPartTemp.setScale(0, RoundingMode.FLOOR)
                fractionalString.append(integerPart.toInt().toString(16).uppercase())

                fractionalAnnotatedString.append("• ${originalValue.stripTrailingZeros()}×16=${fractionalPartTemp.stripTrailingZeros()} (Subtract Int part = ")
                fractionalAnnotatedString.appendSubTitle(
                    integerPart.toInt().toString(16).uppercase()
                )
                fractionalAnnotatedString.append(")\n")
                fractionalPartTemp = fractionalPartTemp.subtract(integerPart, mc)
                steps++

            }

        }

        integralAnnotatedString.appendSmall("\nCounting from bottom to top, the remainders are the octal equivalent of the decimal number\n So, ")
        integralAnnotatedString.appendSubTitle("${parts[0]} ")
        integralAnnotatedString.appendSmall("(Dec)")
        integralAnnotatedString.append(" = ")
        integralAnnotatedString.appendSubTitle(integralString.toString())
        integralAnnotatedString.appendSmall(" (Oct)")

        if (parts.size == 2) {
            val fractionalPart = BigDecimal("0.${parts[1]}")
            fractionalAnnotatedString.appendSmall("\nCounting from top to bottom, the integer parts are the octal equivalent of the decimal number\n So, ")
            fractionalAnnotatedString.appendSubTitle("$fractionalPart ")
            fractionalAnnotatedString.appendSmall("(Dec)")
            fractionalAnnotatedString.append(" = ")
            fractionalAnnotatedString.appendSubTitle(fractionalString.toString())
            fractionalAnnotatedString.appendSmall(" (Oct)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Dec)\n= ")
        result.appendSubTitle(integralString.toString())
        if (parts.size == 2) {
            result.appendSubTitle(".")
            result.appendSubTitle(fractionalString.toString())
        }
        result.appendSmall(" (Oct)")

        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ),
            Pair(
                integralString.toString(),
                if (parts.size == 2) fractionalString.toString() else null
            )
        )


    }

    private suspend fun hexToBin(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val binToDecimal = hexToDec(input)
        val decimalNumber =
            binToDecimal.second.first + if (binToDecimal.second.second != null) ".${binToDecimal.second.second}" else ""
        val decToBin = decToBin(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Hexadecimal to Decimal\n")
        annotatedIntegral.append(binToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Bin\n")
        annotatedIntegral.append(decToBin.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (binToDecimal.first.second != null) {
            annotatedFractional.appendSubTitle("Hexadecimal to Decimal\n")
            annotatedFractional.append(binToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Bin\n")
            annotatedFractional.append(decToBin.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Hex)\n= ")
        result.appendSubTitle(decToBin.second.first)
        if (decToBin.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decToBin.second.second.toString())
        }
        result.appendSmall(" (Bin)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private suspend fun hexToOct(input: String): Triple<AnnotatedString, AnnotatedString?, AnnotatedString> {

        val binToDecimal = hexToDec(input)
        val decimalNumber =
            binToDecimal.second.first + if (binToDecimal.second.second != null) ".${binToDecimal.second.second}" else ""
        val decToOct = decToOct(decimalNumber)

        val annotatedIntegral = AnnotatedString.Builder()
        annotatedIntegral.appendSubTitle("Hexadecimal to Decimal\n")
        annotatedIntegral.append(binToDecimal.first.first)
        annotatedIntegral.appendSubTitle("\n\nDecimal to Octal\n")
        annotatedIntegral.append(decToOct.first.first)

        val annotatedFractional = AnnotatedString.Builder()
        if (binToDecimal.first.second != null) {
            annotatedFractional.appendSubTitle("Hexadecimal to Decimal\n")
            annotatedFractional.append(binToDecimal.first.second)
            annotatedFractional.appendSubTitle("\n\nDecimal to Octal\n")
            annotatedFractional.append(decToOct.first.second)
        }

        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Hex)\n= ")
        result.appendSubTitle(decToOct.second.first)
        if (decToOct.second.second != null) {
            result.appendSubTitle(".")
            result.appendSubTitle(decToOct.second.second.toString())
        }
        result.appendSmall(" (Oct)")

        return Triple(
            annotatedIntegral.toAnnotatedString(),
            annotatedFractional.toAnnotatedString(),
            result.toAnnotatedString()
        )

    }

    private fun hexToDec(input: String): Pair<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>, Pair<String, String?>> {

        var decimalIntegral = 0
        var decimalFractional = BigDecimal.ZERO

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
            val value = bit * 16.0.pow(position).toInt()

            decimalIntegral += value

            if (index != 0) {
                integralAnnPart1.append(" + ")
                integralAnnPart2.append(" + ")
            }

            integralAnnPart1.append("($bit x 16")
            integralAnnPart1.appendSuperscript("$position)")

            integralAnnPart2.append("$value")

        }


        if (fractionalPart.isNotEmpty()) {
            val base = BigDecimal(16)
            val mc = MathContext.DECIMAL128

            fractionalPart.forEachIndexed { index, c ->
                val bit = c.toString().toInt()
                val position = -index - 1
                val exponent = base.pow(position, mc)
                val value = BigDecimal(bit).multiply(exponent, mc)

                decimalFractional = decimalFractional.add(value, mc)

                if (index != 0) {
                    fractionalAnnPart1.append(" + ")
                    fractionalAnnPart2.append(" + ")
                }

                fractionalAnnPart1.append("($bit x 16")
                fractionalAnnPart1.appendSuperscript("$position)")

                fractionalAnnPart2.append("${value.stripTrailingZeros()}")
            }
        }


        integralAnnotatedString.appendSubTitle(integralPart)
        integralAnnotatedString.appendSmall(" (Hex)\n= ")
        integralAnnotatedString.append(integralAnnPart1.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.append(integralAnnPart2.toAnnotatedString())
        integralAnnotatedString.append("\n= ")
        integralAnnotatedString.appendSubTitle("$decimalIntegral")
        integralAnnotatedString.appendSmall(" (Dec)")


        if (fractionalPart.isNotEmpty()) {
            fractionalAnnotatedString.appendSubTitle(fractionalPart)
            fractionalAnnotatedString.appendSmall(" (Hex)\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart1.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.append(fractionalAnnPart2.toAnnotatedString())
            fractionalAnnotatedString.append("\n= ")
            fractionalAnnotatedString.appendSubTitle("${decimalFractional.stripTrailingZeros()}")
            fractionalAnnotatedString.appendSmall(" (Dec)")
        }


        val result = AnnotatedString.Builder()
        result.appendSubTitle(input)
        result.appendSmall(" (Hex)\n= ")
        result.appendSubTitle("$decimalIntegral")
        if (fractionalPart.isNotEmpty()) {
            result.appendSubTitle(".")
            result.appendSubTitle(decimalFractional.stripTrailingZeros().toString().substring(2))
        }
        result.appendSmall(" (Dec)")


        return Pair(
            Triple(
                integralAnnotatedString.toAnnotatedString(),
                fractionalAnnotatedString.toAnnotatedString(),
                result.toAnnotatedString()
            ), Pair(
                decimalIntegral.toString(),
                if (fractionalPart.isNotEmpty()) decimalFractional.toString().substring(2) else null
            )
        )
    }


    private fun AnnotatedString.Builder.appendSuperscript(text: String) {
        withStyle(style = SpanStyle(fontSize = 12.sp, baselineShift = Superscript)) {
            append(text)
        }
    }

    private fun AnnotatedString.Builder.appendSubTitle(text: String) {
        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)) {
            append(text)
        }
    }

    private fun AnnotatedString.Builder.appendSmall(text: String) {
        withStyle(style = SpanStyle(fontSize = 13.sp)) {
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
                    BigDecimal.valueOf(fromBase.toDouble()).pow(-i - 1, MathContext.DECIMAL128)
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