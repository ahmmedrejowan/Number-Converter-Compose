package com.rejowan.numberconverter.view.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.utils.baseNameToValue

@Composable
fun NCTextField(
    onBaseSelected: (String) -> Unit = {},
    onInputValueChange: (String) -> Unit = {},
    input: String = "",
    base: String = "",
    dropHint: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {

    Log.e("NCTextField", "selectedBase1: $base")

    var mExpanded by remember { mutableStateOf(false) }
    val baseList = listOf("Bin", "Oct", "Dec", "Hex")
    var selectedBase = base
    var isValid by remember { mutableStateOf(true) }


    Log.e("NCTextField", "selectedBase2: $selectedBase")

    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown


    fun validateInput(input: String, base: String): Boolean {
        val baseValue = baseNameToValue(base)
        return input.uppercase().replace(".","").all {
            val digitValue = when (it) {
                in '0'..'9' -> it - '0'
                in 'A'..'F' -> it - 'A' + 10
                else -> -1
            }
            digitValue in 0 until baseValue
        }
    }

    Row(Modifier.fillMaxWidth()) {

        Column(
            Modifier
                .padding(5.dp)
                .weight(2f)
        ) {

            OutlinedTextField(value = selectedBase,
                readOnly = true,
                onValueChange = { },
                modifier = Modifier.clickable { mExpanded = !mExpanded },
                label = { Text(dropHint) },
                trailingIcon = {
                    Icon(icon, "", Modifier.clickable { mExpanded = !mExpanded })
                })

            DropdownMenu(expanded = mExpanded, onDismissRequest = { mExpanded = false }) {
                baseList.forEach {
                    DropdownMenuItem(text = {
                        Text(
                            text = it,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp, 0.dp)
                        )
                    },
                        onClick = {
                            selectedBase = it
                            mExpanded = false
                            onBaseSelected(it)
                        },
                        colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.onSurface)
                    )
                }

            }

        }

        val mLabel = if (readOnly) "Result in $selectedBase" else "Enter $selectedBase value"

        OutlinedTextField(value = input,
            onValueChange = {
                isValid = validateInput(it, selectedBase)
                onInputValueChange(it)
            },
            readOnly = readOnly,
            modifier = Modifier
                .padding(5.dp)
                .weight(4f),
            label = { Text(text = if (isValid) mLabel else "Invalid Input, $selectedBase Only") },
            isError = !isValid,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (selectedBase == "Hex") {
                    KeyboardType.Text
                } else {
                    KeyboardType.Number
                }
            )

        )

    }

}


@Preview(showBackground = true)
@Composable
fun NCTextFieldPreview() {
    NCTextField()
}