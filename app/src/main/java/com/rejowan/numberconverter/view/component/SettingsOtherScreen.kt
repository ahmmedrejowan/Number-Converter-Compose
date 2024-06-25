package com.rejowan.numberconverter.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.R

@Composable
fun SettingOtherScreen(
    icon: Painter = painterResource(id = R.drawable.ic_swap),
    title: String = "Info Title",
    description: String = "Info Description",
    onItemClicked: () -> Unit = {}
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onItemClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            icon, contentDescription = "DP", modifier = Modifier.padding(10.dp)
        )

        Spacer(modifier = Modifier.size(5.dp))

        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
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


    }

}


@Preview(showBackground = true)
@Composable
fun PreviewSettingOtherScreen() {
    SettingOtherScreen()
}