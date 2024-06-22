package com.rejowan.numberconverter.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.ui.theme.AppTheme

class Settings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ShowSettingScreen()
            }
        }
    }

    @Composable
    private fun ShowSettingScreen() {

        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    onBackPressedDispatcher.onBackPressed()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = ""
                    )
                }

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                )

            }

        }) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {

                HorizontalDivider(
                    thickness = 0.5.dp
                )


            }
        }

    }


    @Preview(showBackground = true)
    @Composable
    fun SettingPreview() {
        ShowSettingScreen()
    }
}


