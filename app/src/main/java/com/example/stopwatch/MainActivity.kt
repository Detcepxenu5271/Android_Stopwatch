package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.ui.compose.CircularProgressBar
import com.example.stopwatch.ui.theme.StopwatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopwatchTheme {
                StopwatchApp()
            }
        }
    }
}

/**
 * Stopwatch 应用的入口结构
 *
 * @param stopwatchViewModel 处理 Stopwatch 的相关数据
 */
@Composable
fun StopwatchApp(stopwatchViewModel: StopwatchViewModel = viewModel(factory = StopwatchViewModel.Factory)) {
    val state by stopwatchViewModel.state
    val lapTimeList = stopwatchViewModel.lapTimeList

    StopwatchScreen(
        state = state,
        lapTimeList = lapTimeList,
        onLapResetClick = { if (state.isRunning) stopwatchViewModel.lap() else stopwatchViewModel.reset() },
        onStartStopClick = { if (state.isRunning) stopwatchViewModel.pause() else stopwatchViewModel.start() }
    )
}

/**
 * Stopwatch 的界面
 *
 * @param state [StopwatchViewModel] 中的状态, 包含 isRunning 和 elapsedTime
 * @param lapTimeList [StopwatchViewModel] 中各 lap 的时间
 * @param onLapResetClick Lap/Reset 按钮的 onClick 函数
 * @param onStartStopClick Start/Stop 按钮的 onClick 函数
 */
@Composable
fun StopwatchScreen(
    state: StopwatchState,
    lapTimeList: List<Long>,
    onLapResetClick: () -> Unit,
    onStartStopClick: () -> Unit
) {
    // 默认宽度为最大
    val modifier = Modifier
        .fillMaxWidth()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column (
            modifier = modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title(
                text = stringResource(R.string.title),
                modifier = modifier
            )
            Gauge(
                state = state,
                modifier = modifier,
            )
            ControlButtons(
                state = state,
                onLapResetClick = onLapResetClick,
                onStartStopClick = onStartStopClick,
                modifier = modifier
            )
            Laps(
                lapTimeList = lapTimeList,
                modifier = modifier
            )
        }
    }
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        // textAlign = TextAlign.Center,
        fontSize = 36.sp,
    )
}

@Composable
fun Gauge(state: StopwatchState, modifier: Modifier = Modifier) {
    // val state by stopwatchViewModel.state //TODO 只检测其中的 elapsedTime
    Box (
        modifier = modifier
            .padding(top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressBar(
            percentage = TimeUtils.minuteProgress(state.elapsedTime),
            radius = 100.dp,
            color = Color.Blue,
            strokeWidth = 16.dp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = TimeUtils.long2String(state.elapsedTime),
            fontSize = 24.sp
        )
    }
}

@Composable
fun ControlButtons(state: StopwatchState, onLapResetClick: () -> Unit, onStartStopClick: () -> Unit, modifier: Modifier = Modifier) {
    // val state by stopwatchViewModel.state //TODO 只检测其中的 isRunning
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onLapResetClick) {
            Text(
                text = if (state.isRunning) "Lap" else "Reset",
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Button(onClick = onStartStopClick) {
            Text(
                text = if (state.isRunning) "Stop" else "Start",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun Laps(lapTimeList: List<Long>, modifier: Modifier = Modifier) {
    // val lapTimeList = stopwatchViewModel.lapTimeList //TODO 不能用 by, 但是这么写也被 observe 了
    val listState = rememberLazyListState()
    // 添加新 lap 时, 滚动到最新的 lap
    LaunchedEffect(lapTimeList.size) {
        listState.animateScrollToItem(index = if (lapTimeList.size == 0) 0 else lapTimeList.size - 1)
    }
    LazyColumn (
        modifier = modifier
            /*.border(5.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .padding(10.dp)*/,
        state = listState,
        reverseLayout = true
    ) {
        itemsIndexed(lapTimeList) { index, lapTime ->
            Lap(
                lapId = index + 1,
                time = TimeUtils.long2String(lapTime),
                modifier = modifier
                    .padding(10.dp)
                    .background(Color(248, 248, 248), RoundedCornerShape(5.dp))
            )
        }
    }
}

@Composable
fun Lap(lapId: Int, time: String, modifier: Modifier = Modifier) {
    Row (
        modifier = modifier
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "Lap $lapId", fontSize = 16.sp)
        Text(text = time, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun StopwatchPreview() {
    val mockState = StopwatchState(true, 47500) // Replace StopwatchState and ... with the actual type and value
    val mockLapTimeList = listOf<Long>(1100, 2300, 3500, 4700, 5900, 30000) // Replace List<Long> and ... with the actual type and value

    StopwatchTheme {
        StopwatchScreen(
            state = mockState,
            lapTimeList = mockLapTimeList,
            onLapResetClick = {},
            onStartStopClick = {}
        )
    }
}