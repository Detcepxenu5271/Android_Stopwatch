// 参考 https://github.com/dp9318/Circular-ProgressBar-In-Jetpack-Compose
package com.example.stopwatch.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 环形进度条
 *
 * @param percentage 进度, 范围应为 [0,1)
 * @param radius 进度条半径
 * @param fgColor 进度条填充颜色
 * @param bgColor 进度条背景颜色
 * @param strokeWidth 进度条粗细
 */
@Composable
fun CircularProgressBar(
    percentage: Float,
    radius: Dp = 50.dp,
    fgColor : Color = Color.Green,
    bgColor: Color = Color.LightGray,
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
                bgColor,
                -90f,
                360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                fgColor,
                -90f,
                360 * percentage,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}