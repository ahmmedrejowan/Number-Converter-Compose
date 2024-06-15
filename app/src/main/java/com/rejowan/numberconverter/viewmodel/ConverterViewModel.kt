package com.rejowan.numberconverter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.repository.ConverterRepository
import kotlinx.coroutines.launch

class ConverterViewModel(private val repository: ConverterRepository) : ViewModel() {

    private val _output = MutableLiveData<String?>()
    val output: LiveData<String?> get() = _output

    private val _decimalPlaces = MutableLiveData<Int>()
    val decimalPlaces: LiveData<Int> get() = _decimalPlaces

    init {
        viewModelScope.launch {
            Log.e("ConverterViewModel", "init")
            _decimalPlaces.value = repository.getDecimalPlaces()
            Log.e("decimalPlaces", _decimalPlaces.value.toString())
        }
    }


    fun convert(input: String, fromBase: Int, toBase: Int) {
        viewModelScope.launch {
            _output.value = repository.convert(input, fromBase, toBase)
        }
    }

    fun setDecimalPlaces(decimalPlaces: Int) {
        viewModelScope.launch {
            repository.setDecimalPlaces(decimalPlaces)
            _decimalPlaces.value = decimalPlaces
        }
    }


}