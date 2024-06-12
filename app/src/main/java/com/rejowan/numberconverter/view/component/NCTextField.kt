package com.rejowan.numberconverter.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NCTextField(
    onBaseSelected: (String) -> Unit = {},
    onInputValueChange: (String) -> Unit = {},
    input: String = ""
) {


    var mExpanded by remember { mutableStateOf(false) }
    val baseList = listOf("Bin", "Oct", "Dec", "Hex")
    var selectedBase by remember { mutableStateOf(baseList[0]) }
    var inputValue by remember { mutableStateOf(input) }

    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown

    Row(Modifier.fillMaxWidth()) {

        Column(
            Modifier
                .padding(5.dp)
                .weight(1.6f)
        ) {

            OutlinedTextField(value = selectedBase,
                readOnly = true,
                onValueChange = {
                    selectedBase = it
                    onBaseSelected(it)
                },
                modifier = Modifier.clickable { mExpanded = !mExpanded },
                label = { Text("Base") },
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

        OutlinedTextField(value = inputValue,
            onValueChange = {
                inputValue = it
                onInputValueChange(it)
            },
            modifier = Modifier
                .padding(5.dp)
                .weight(4f),
            label = { Text("Enter $selectedBase value") }

        )

    }

}


@Preview(showBackground = true)
@Composable
fun NCTextFieldPreview() {
    NCTextField()
}