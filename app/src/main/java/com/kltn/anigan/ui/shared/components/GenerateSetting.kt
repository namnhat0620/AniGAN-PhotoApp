package com.kltn.anigan.ui.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kltn.anigan.R

const val MIN_VALUE = 1
const val MAX_VALUE = 20

@Composable
private fun NumOfGeneration(
    numOfGenerations: Int,
    onNumOfGenerationsChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Number of Generated", color = Color.White)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_remove_empty),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (numOfGenerations >= MIN_VALUE) {
                            onNumOfGenerationsChanged(numOfGenerations - 1)
                        }
                    }
                    .size(16.dp)
            )
            Text(
                text = numOfGenerations.toString(),
                color = Color.White,
                modifier = Modifier.padding(10.dp, 0.dp))
            Image(
                painter = painterResource(id = R.drawable.icon_add_circled_outline),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (numOfGenerations < MAX_VALUE) {
                            onNumOfGenerationsChanged(numOfGenerations + 1)
                        }
                    }
                    .size(16.dp)
            )
        }
    }
}

@Composable
@Preview
fun GenerateSetting() {
    var numOfGenerations by remember { mutableIntStateOf(2)}

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumOfGeneration(
            numOfGenerations,
            onNumOfGenerationsChanged = { newValue ->
                numOfGenerations = newValue
            }
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp))
        GradientButton(
            gradientColors = listOf(Color(0xFF00FFF0), Color(0xFF00FF66)),
            cornerRadius = 16.dp,
            nameButton = "Generate Now!",
            roundedCornerShape = RoundedCornerShape(size = 30.dp)
        )
        Text(
            text = "Every creation consumes $numOfGenerations credits.",
            color = Color.Gray
        )
    }

}