package com.rejowan.numberconverter.viewmodel

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.repository.ConverterRepository
import kotlinx.coroutines.launch

class ConverterViewModel(private val repository: ConverterRepository) : ViewModel() {

    private val _output = MutableLiveData<String?>()
    val output: LiveData<String?> get() = _output

    private val _decimalPlaces = MutableLiveData<Int>()
    val decimalPlaces: LiveData<Int> get() = _decimalPlaces

    private val _explanation = MutableLiveData<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>>()
    val explanation: LiveData<Triple<AnnotatedString, AnnotatedString?, AnnotatedString>> get() = _explanation

    init {
        viewModelScope.launch {
            _decimalPlaces.value = repository.getDecimalPlaces()
        }
    }


    fun convert(input: String, fromBase: Int, toBase: Int) {
        viewModelScope.launch {
            _output.value = repository.convert(input, fromBase, toBase)
        }
    }

    fun explain(input: String, fromBase: Int, toBase: Int) {
        viewModelScope.launch {
            if (input.isNotEmpty()) {
                _explanation.value = repository.explain(input, fromBase, toBase)
            }
        }
    }

    fun setDecimalPlaces(decimalPlaces: Int) {
        viewModelScope.launch {
            repository.setDecimalPlaces(decimalPlaces)
            _decimalPlaces.value = decimalPlaces
        }
    }


}