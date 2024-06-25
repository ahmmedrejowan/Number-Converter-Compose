package com.rejowan.numberconverter.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.di.converterModule
import com.rejowan.numberconverter.ui.theme.AppTheme
import com.rejowan.numberconverter.view.component.SettingOtherScreen
import com.rejowan.numberconverter.view.component.SettingScreenDP
import com.rejowan.numberconverter.viewmodel.ConverterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication

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
    private fun ShowSettingScreen(viewModel: ConverterViewModel = koinViewModel()) {

        val initialDP by viewModel.decimalPlaces.observeAsState(initial = 20)


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

                Spacer(modifier = Modifier.size(5.dp))

                Text(text = "General",
                    modifier = Modifier.padding(10.dp,0.dp),
                    color = MaterialTheme.colorScheme.primary)

                SettingScreenDP(initialValue = initialDP, onValueChange = {
                    viewModel.setDecimalPlaces(it)
                })

                Text(text = "About",
                    modifier = Modifier.padding(10.dp,0.dp),
                    color = MaterialTheme.colorScheme.primary)

                SettingOtherScreen(
                    icon = {
                        Icon(Icons.Outlined.Info,
                            contentDescription ="",
                            modifier = Modifier.padding(10.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    },
                    title = "App Version",
                    description = "Version 1.0"
                )

                SettingOtherScreen(
                    icon = {
                        Icon(Icons.Outlined.Email,
                            contentDescription ="",
                            modifier = Modifier.padding(10.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    },
                    title = "Contact Us",
                    description = "Share your thoughts and comments",
                    onItemClicked = {

                    }
                )

                SettingOtherScreen(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_pref_v_c_s),
                            contentDescription ="",
                            modifier = Modifier.padding(10.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    },                    title = "Source Code",
                    description = "See source code on Github",
                    onItemClicked = {

                    }
                )

                SettingOtherScreen(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.outline_copyright_24),
                            contentDescription ="",
                            modifier = Modifier.padding(10.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    },                    title = "License",
                    description = "Apache 2.0",
                    onItemClicked = {

                    }
                )


                SettingOtherScreen(
                    icon = {
                        Icon(
                            painterResource(id = R.drawable.ic_pref_creator),
                            contentDescription ="",
                            modifier = Modifier.padding(10.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    },                    title = "Created by",
                    description = "K M Rejowan Ahmmed",
                    onItemClicked = {

                    }

                )



            }
        }

    }


    @Preview(showBackground = true)
    @Composable
    fun SettingPreview() {

        val context = LocalContext.current
        KoinApplication(application = {
            androidContext(context)
            modules(listOf(converterModule))
        }) {
            AppTheme {
                ShowSettingScreen()
            }
        }

    }

}


