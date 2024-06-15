package com.rejowan.numberconverter.repository

import androidx.compose.ui.text.AnnotatedString

interface ConverterRepository {
    suspend fun convert(input: String, fromBase: Int, toBase: Int): String?
    suspend fun explain(input: String, fromBase: Int, toBase: Int): Triple<AnnotatedString, AnnotatedString?, AnnotatedString>
    suspend fun setDecimalPlaces(decimalPlaces: Int)
    suspend fun getDecimalPlaces(): Int
}