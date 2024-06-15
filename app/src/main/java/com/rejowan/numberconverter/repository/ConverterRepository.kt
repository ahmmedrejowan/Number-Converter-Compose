package com.rejowan.numberconverter.repository

interface ConverterRepository {
    suspend fun convert(input: String, fromBase: Int, toBase: Int): String?
    suspend fun setDecimalPlaces(decimalPlaces: Int)
    suspend fun getDecimalPlaces(): Int
}