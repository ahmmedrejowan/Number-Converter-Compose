package com.rejowan.numberconverter.view.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rejowan.numberconverter.R

@Composable
fun SettingScreenDP(
    initialValue: Int = 20,
    onValueChange: (Int) -> Unit = {},
    icon: Painter = painterResource(id = R.drawable.ic_swap),
    title: String = "Decimal Places",
    description: String = "Result decimal place limit"
) {

    var value by remember { mutableFloatStateOf(initialValue.toFloat()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = initialValue) {
        value = initialValue.toFloat()
    }

    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            icon, contentDescription = "DP", modifier = Modifier.padding(10.dp)
        )

        Spacer(modifier = Modifier.size(5.dp))

        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(5.dp))

        }

        OutlinedTextField(
            modifier = Modifier
                .width(80.dp)
                .padding(5.dp),
            value = value.toInt().toString(),
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            ),
            readOnly = true,
            onValueChange = {

            },
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            expanded = !expanded
                        }
                    }
                }
            },
        )


        if (expanded) {
            Dialog(onDismissRequest = { }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface
                ) {
                    Column {


                        Spacer(modifier = Modifier.size(20.dp))

                        Text(
                            text = "Decimal Places",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.size(5.dp))

                        Text(
                            text = "The result from the conversion will be rounded to the number of decimal places you specify below.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 5.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )


                        Spacer(modifier = Modifier.size(15.dp))

                        Text(
                            text = "${value.toInt()}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 35.sp
                        )

                        Spacer(modifier = Modifier.size(10.dp))


                        Slider(value = value,
                            onValueChange = {
                                value = it
                            },
                            valueRange = 1f..34f,
                            steps = 0,
                            modifier = Modifier.padding(10.dp),
                            onValueChangeFinished = {
                                onValueChange(value.toInt())
                            })


                        Spacer(modifier = Modifier.size(5.dp))

                        Button(
                            onClick = { expanded = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = "Dismiss")
                        }

                        Spacer(modifier = Modifier.size(10.dp))


                    }
                }

            }

        }

    }

}


@Preview(showBackground = true)
@Composable
fun PreviewSettingScreenDP() {
    SettingScreenDP()
}