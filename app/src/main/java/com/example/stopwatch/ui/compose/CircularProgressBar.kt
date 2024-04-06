// 参考 https://github.com/dp9318/Circular-ProgressBar-In-Jetpack-Compose
package com.example.stopwatch.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressBar(
    percentage: Float,
    radius: Dp = 50.dp,
    color : Color = Color.Green,
    strokeWidth: Dp = 8.dp,
){
    Box(modifier = Modifier
        .size(radius*2f),
        contentAlignment = Alignment.Center
    ){
        Canvas(modifier = Modifier
            .size(radius * 2f)
        ) {
            drawArc(
                color,
                -90f,
                360 * percentage,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}