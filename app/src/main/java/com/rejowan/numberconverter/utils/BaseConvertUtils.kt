package com.rejowan.numberconverter.utils

fun baseNameToValue(baseName: String): Int {
    return when (baseName) {
        "Bin" -> 2
        "Oct" -> 8
        "Dec" -> 10
        "Hex" -> 16
        else -> 10
    }
}
