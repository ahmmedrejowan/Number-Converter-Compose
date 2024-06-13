package com.rejowan.numberconverter.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.di.converterModule
import com.rejowan.numberconverter.ui.theme.AppTheme
import com.rejowan.numberconverter.utils.baseNameToValue
import com.rejowan.numberconverter.view.component.NCTextField
import com.rejowan.numberconverter.viewmodel.ConverterViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinApplication


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {

                ShowHomeContent()


            }
        }
    }


    @Composable
    fun ShowHomeContent(viewModel: ConverterViewModel = koinViewModel()) {

        val context = LocalContext.current
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        var inputValue by remember {
            mutableStateOf("")
        }

        var inputBase by remember {
            mutableStateOf("Bin")
        }

        var outputBase by remember {
            mutableStateOf("Dec")
        }

        val output by viewModel.output.observeAsState()

        LaunchedEffect(inputValue, inputBase, outputBase) {
            viewModel.convert(
                inputValue, baseNameToValue(inputBase), baseNameToValue(outputBase)
            )

        }


        Scaffold(modifier = Modifier.fillMaxSize(),

            topBar = {

               Column (Modifier.fillMaxWidth()) {
                   Spacer(modifier = Modifier.height(15.dp))

                   Text(text = "Number Converter",
                       style = MaterialTheme.typography.titleMedium,
                       fontSize = 20.sp,
                       modifier = Modifier.fillMaxWidth(),
                       textAlign = TextAlign.Center)

                   Text(text = "Convert between bases with ease!",
                       style = MaterialTheme.typography.bodyMedium,
                       fontSize = 14.sp,
                       modifier = Modifier.padding(2.dp).fillMaxWidth(),
                       textAlign = TextAlign.Center)

                   Spacer(modifier = Modifier.height(10.dp))

               }

            }

        ) { innerPadding ->

            Column(modifier = Modifier.padding(innerPadding)) {

                Spacer(modifier = Modifier.height(10.dp))

                NCTextField(onBaseSelected = { inputBase = it },
                    onInputValueChange = { inputValue = it },
                    input = inputValue,
                    base = inputBase,
                    dropHint = "Input",
                    trailingIcon = {
                        if (inputValue.isNotEmpty()) {
                            Icon(Icons.Filled.Clear, "", Modifier.clickable {
                                inputValue = ""
                            })
                        }
                    })

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Box(modifier = Modifier
                        .padding(5.dp)
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape
                        )
                        .clickable {
                            val temp = inputBase
                            inputBase = outputBase
                            outputBase = temp

                            inputValue = if (inputValue.isNotEmpty()) output ?: "" else ""

                        }, contentAlignment = Alignment.Center) {
                        Icon(
                            painterResource(id = R.drawable.ic_swap), contentDescription = ""
                        )
                    }


                }

                Spacer(modifier = Modifier.height(5.dp))


                NCTextField(onBaseSelected = { outputBase = it },
                    input = if (inputValue.isEmpty()) "" else output ?: "",
                    base = outputBase,
                    readOnly = true,
                    dropHint = "Output",
                    trailingIcon = {
                        if (output.toString().isNotEmpty() && inputValue.isNotEmpty()) {
                            Icon(painterResource(id = R.drawable.ic_copy),
                                contentDescription = "",
                                Modifier.clickable {
                                    val clip = ClipData.newPlainText("Copied Text", output)
                                    clipboardManager.setPrimaryClip(clip)
                                    Toast.makeText(
                                        context, "Copied to Clipboard", Toast.LENGTH_SHORT
                                    ).show()

                                })
                        }
                    }

                )


            }

        }

    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        KoinApplication(application = {
            modules(listOf(converterModule))
        }) {
            AppTheme {
                ShowHomeContent()
            }
        }

    }

}
