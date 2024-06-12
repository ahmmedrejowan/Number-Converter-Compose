package com.rejowan.numberconverter.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.rejowan.numberconverter.ui.theme.AppTheme
import com.rejowan.numberconverter.utils.baseMap
import com.rejowan.numberconverter.view.component.NCTextField
import com.rejowan.numberconverter.viewmodel.ConverterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val converterViewModel: ConverterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {

                ShowHomeContent()


            }
        }
    }


    @Composable
    fun ShowHomeContent() {

        var firstValue by remember { mutableStateOf("") }
        var secondValue by remember { mutableStateOf("") }

        var firstBase by remember { mutableStateOf("Bin") }
        var secondBase by remember { mutableStateOf("Dec") }



        if (!LocalInspectionMode.current) {
            converterViewModel.output.observe(this) {
                it?.let {
                    if (it.isNotEmpty()) {
                        secondValue = it
                        Log.d("MainActivity", "Second Value: $secondValue")
                    }
                } ?: run {
                    secondValue = ""
                }
            }
        }



        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Column(modifier = Modifier.padding(innerPadding)) {

                NCTextField(onBaseSelected = {
                       firstBase = it

                }, onInputValueChange = {
                    firstValue = it
                    converterViewModel.convert(
                        input = firstValue,
                        fromBase = baseMap[firstBase]!!,
                        toBase = baseMap[secondBase]!!
                    )

                }, input = firstValue, base = firstBase
                )


                NCTextField(onBaseSelected = {
                    secondBase = it

                }, onInputValueChange = {
                    secondBase = it

                }, input = if (firstValue.isNotEmpty()) secondValue else "",
                    base = secondBase,
                    readOnly = true
                )

            }

        }

    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        AppTheme {
            ShowHomeContent()
        }
    }

}
