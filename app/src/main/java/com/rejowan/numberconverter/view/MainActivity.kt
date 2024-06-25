package com.rejowan.numberconverter.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.di.converterModule
import com.rejowan.numberconverter.ui.theme.AppTheme
import com.rejowan.numberconverter.utils.baseNameToValue
import com.rejowan.numberconverter.view.component.DialogDecimalPlaces
import com.rejowan.numberconverter.view.component.NCTextField
import com.rejowan.numberconverter.viewmodel.ConverterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
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

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

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

        val initialDP by viewModel.decimalPlaces.observeAsState(initial = 20)

        val lifecycleOwner = LocalLifecycleOwner.current
        val state by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

        LaunchedEffect(state) {

            if (state == Lifecycle.State.RESUMED) {
                viewModel.getDecimalPlaces()
            }
            
        }


        val explanation by viewModel.explanation.observeAsState()

        fun validateInput(input: String, base: String): Boolean {
            if (input.split(".").size > 2) {
                return false
            }

            val baseValue = baseNameToValue(base)

            val validChars =
                ('0' until '0' + baseValue).joinToString("") + if (baseValue > 10) ('A' until 'A' + baseValue - 10).joinToString(
                    ""
                ) else ""

            return input.uppercase().replace(".", "")
                .all { it in validChars } && input.all { it.isDigit() || it in 'A'..'F' || it == '.' }
        }

        var expanded by remember {
            mutableStateOf(false)
        }

        val icon =
            if (expanded) R.drawable.ic_collapse else R.drawable.ic_expand

        LaunchedEffect(inputValue, inputBase, outputBase, initialDP) {

            Log.e("DP2", initialDP.toString())


            if (validateInput(inputValue, inputBase)) {
                viewModel.convert(
                    inputValue, baseNameToValue(inputBase), baseNameToValue(outputBase)
                )

                viewModel.explain(
                    inputValue,
                    baseNameToValue(inputBase),
                    baseNameToValue(outputBase)
                )
            }

        }

        Scaffold(modifier = Modifier.fillMaxSize(),

            topBar = {

                if (!expanded) {

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        IconButton(onClick = { }) {}

                        Column(Modifier.weight(1f)) {
                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = "Number Converter",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Convert between bases with ease!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                        }

                        IconButton(onClick = {
                            startActivity(Intent(context, Settings::class.java))
                        }) {

                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.secondary
                            )

                        }
                    }


                }

            }

        ) { innerPadding ->

            Column(modifier = Modifier
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }) {


                Column {

                    Spacer(modifier = Modifier.height(10.dp))

                    NCTextField(onBaseSelected = { inputBase = it },
                        onInputValueChange = { inputValue = it },
                        input = inputValue,
                        base = inputBase,
                        dropHint = "Input",
                        focusRequester = focusRequester,
                        trailingIcon = {
                            if (inputValue.isNotEmpty()) {
                                Icon(Icons.Filled.Clear, "", Modifier.clickable {
                                    inputValue = ""
                                })
                            }
                        })

                    Spacer(modifier = Modifier.height(5.dp))

                    if (!expanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {


                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(40.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        val temp = inputBase
                                        inputBase = outputBase
                                        outputBase = temp

                                        inputValue =
                                            if (inputValue.isNotEmpty()) output ?: "" else ""

                                        focusManager.clearFocus()

                                    }, contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.ic_swap),
                                    contentDescription = ""
                                )
                            }

                            Spacer(modifier = Modifier.size(5.dp))

                            DialogDecimalPlaces(initialValue = initialDP, onValueChange = {
                                viewModel.setDecimalPlaces(it)
                            })


                        }

                        Spacer(modifier = Modifier.height(5.dp))

                    }


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


                if (inputValue.isNotEmpty()) {

                    Column {

                        Spacer(modifier = Modifier.size(10.dp))

                        Row {
                            Column(Modifier.weight(1f)) {

                            }

                            Column(Modifier.weight(5f)) {
                                Text(
                                    text = "Explanation",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(Modifier.weight(1f)) {

                                IconButton(onClick = {
                                    expanded = !expanded
                                }) {
                                    Icon(painterResource(id = icon), contentDescription = "")
                                }

                            }
                        }

                        HorizontalDivider()


                        Spacer(modifier = Modifier.size(10.dp))

                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {


                            Text(text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Integral Part")
                                }
                            }, modifier = Modifier.padding(5.dp))

                            Text(
                                text = buildAnnotatedString {
                                    explanation?.let {
                                        append(it.first)
                                    }
                                },
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(15.dp, 0.dp)
                            )

                            Spacer(modifier = Modifier.size(5.dp))

                            explanation?.let {
                                if (it.second.toString().isNotEmpty()) {
                                    Text(text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 16.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("Fractional Part")
                                        }
                                    }, modifier = Modifier.padding(5.dp))

                                    Text(
                                        text = buildAnnotatedString {
                                            append(it.second)
                                        },
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(15.dp, 0.dp)
                                    )

                                    Spacer(modifier = Modifier.size(5.dp))

                                }
                            }


                            Text(text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Total")
                                }
                            }, modifier = Modifier.padding(5.dp))

                            Text(
                                text = buildAnnotatedString {
                                    explanation?.let {
                                        append(it.third)
                                    }
                                },
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(15.dp, 0.dp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))


                        }


                    }

                } else {

                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Image(
                            painterResource(id = R.drawable.info_empty_magic),
                            contentDescription = ""
                        )


                        Text(
                            text = "Start Typing...\nSomething Magical will happen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )

                    }


                }


            }


        }

    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        val context = LocalContext.current
        KoinApplication(application = {
            androidContext(context)
            modules(listOf(converterModule))
        }) {
            AppTheme {
                ShowHomeContent()
            }
        }

    }

}
