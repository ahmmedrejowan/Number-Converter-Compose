package com.rejowan.numberconverter.repository

interface ConverterRepository {
   fun convert(input: String, fromBase: Int, toBase: Int) : String?
}