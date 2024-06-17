package com.rejowan.numberconverter.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun AnnotatedString.Builder.appendSuperscript(text: String) {
    withStyle(style = SpanStyle(fontSize = 12.sp, baselineShift = BaselineShift.Superscript)) {
        append(text)
    }
}

fun AnnotatedString.Builder.appendSubTitle(text: String) {
    withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)) {
        append(text)
    }
}

fun AnnotatedString.Builder.appendSmall(text: String) {
    withStyle(style = SpanStyle(fontSize = 13.sp)) {
        append(text)
    }
}
