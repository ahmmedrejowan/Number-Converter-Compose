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
        "Bin" -> "Invalid, Only 0 and 1"
        "Oct" -> "Invalid, Only 0 to 7"
        "Dec" -> "Invalid, Only 0 to 9"
        "Hex" -> "Invalid, Only 0 to 9 and A to F"
        else -> "Invalid, Only 0 to 9"
    }
}