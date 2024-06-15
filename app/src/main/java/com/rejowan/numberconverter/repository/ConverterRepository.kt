package com.rejowan.numberconverter.repository

import androidx.compose.ui.text.AnnotatedString

interface ConverterRepository {
    suspend fun convert(input: String, fromBase: Int, toBase: Int): String?
    suspend fun explain(input: String, fromBase: Int, toBase: Int): AnnotatedString?
    suspend fun setDecimalPlaces(decimalPlaces: Int)
    suspend fun getDecimalPlaces(): Int
}