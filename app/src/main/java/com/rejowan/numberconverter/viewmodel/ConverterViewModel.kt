package com.rejowan.numberconverter.viewmodel

import androidx.lifecycle.ViewModel
import com.rejowan.numberconverter.repository.ConverterRepository

class ConverterViewModel(private val repository: ConverterRepository) : ViewModel() {

    val output = repository.output

    fun convert(input: String, fromBase: Int, toBase: Int) {
        repository.convert(input, fromBase, toBase)
    }


}