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


fun getErrorMessage(base: String): String {
    return when(base){
        "Bin" -> "Invalid, Allowed 0 and 1"
        "Oct" -> "Invalid, Allowed 0 - 7"
        "Dec" -> "Invalid, Allowed 0 - 9"
        "Hex" -> "Invalid, Allowed 0 - 9 and A - F"
        else -> "Invalid, Allowed 0 - 9"
    }
}