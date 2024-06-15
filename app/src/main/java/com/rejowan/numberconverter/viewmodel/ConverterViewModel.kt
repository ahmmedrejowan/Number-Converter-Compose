package com.rejowan.numberconverter.viewmodel

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


    val decimalPlaces: LiveData<Int> = liveData {
        emit(repository.getDecimalPlaces())
    }

    fun convert(input: String, fromBase: Int, toBase: Int) {
        viewModelScope.launch {
            _output.value = repository.convert(input, fromBase, toBase)
        }
    }

    fun setDecimalPlaces(decimalPlaces: Int) {
        viewModelScope.launch {
            repository.setDecimalPlaces(decimalPlaces)
        }
    }


}