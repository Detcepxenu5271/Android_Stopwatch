package com.example.stopwatch

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
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
    val orientation = LocalConfiguration.current.orientation

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        StopwatchScreen(
            state = state,
            lapTimeList = lapTimeList,
            onLapResetClick = { if (state.isRunning) stopwatchViewModel.lap() else stopwatchViewModel.reset() },
            onStopStartClick = { if (state.isRunning) stopwatchViewModel.pause() else stopwatchViewModel.start() }
        )
    } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        StopwatchScreenLandscape(
            state = state,
            lapTimeList = lapTimeList,
            onLapResetClick = { if (state.isRunning) stopwatchViewModel.lap() else stopwatchViewModel.reset() },
            onStopStartClick = { if (state.isRunning) stopwatchViewModel.pause() else stopwatchViewModel.start() }
        )
    }

}

/**
 * Stopwatch 的界面 (竖屏)
 *
 * @param state [StopwatchViewModel] 中的状态, 包含 isRunning 和 elapsedTime
 * @param lapTimeList [StopwatchViewModel] 中各 lap 的时间
 * @param onLapResetClick Lap/Reset 按钮的 onClick 函数
 * @param onStopStartClick Stop/Start 按钮的 onClick 函数
 */
@Composable
fun StopwatchScreen(
    state: StopwatchState,
    lapTimeList: List<Long>,
    onLapResetClick: () -> Unit,
    onStopStartClick: () -> Unit
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
                onStopStartClick = onStopStartClick,
                modifier = modifier
            )
            Laps(
                lapTimeList = lapTimeList,
                modifier = modifier
            )
        }
    }
}

/**
 * Stopwatch 的横屏界面
 *
 * @param state
 * @param lapTimeList
 * @param onLapResetClick
 * @param onStopStartClick
 */
@Composable
fun StopwatchScreenLandscape(
    state: StopwatchState,
    lapTimeList: List<Long>,
    onLapResetClick: () -> Unit,
    onStopStartClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row (
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(
                    text = stringResource(R.string.title),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f)) // 两个 Spacer, 使 Gauge 在 center 位置
                Gauge(
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f)) // 另一个 Spacer
            }
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Laps(
                    lapTimeList = lapTimeList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                ControlButtons(
                    state = state,
                    onLapResetClick = onLapResetClick,
                    onStopStartClick = onStopStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}

/**
 * 标题
 *
 * @param text
 * @param modifier
 */
@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        // textAlign = TextAlign.Center,
        fontSize = 36.sp
    )
}

/**
 * 计时表, 包括分钟进度条和总时间
 *
 * @param state
 * @param modifier
 */
@Composable
fun Gauge(state: StopwatchState, modifier: Modifier = Modifier) {
    BoxWithConstraints (
        modifier = modifier
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        // 进度条半径为 100.dp, 如果空间不够的话就减小
        val radius = min(min(maxWidth, maxHeight) / 2, 100.dp)
        CircularProgressBar(
            percentage = TimeUtils.minuteProgress(state.elapsedTime),
            radius = radius,
            fgColor = Color.Blue,
            bgColor = Color.LightGray,
            strokeWidth = 16.dp
        )
        Text(
            text = TimeUtils.long2String(state.elapsedTime),
            fontSize = 24.sp
        )
    }
}

/**
 * 控制按钮, 包括 Lap/Reset 按钮和 Stop/Start 按钮
 *
 * @param state
 * @param onLapResetClick
 * @param onStopStartClick
 * @param modifier
 */
@Composable
fun ControlButtons(state: StopwatchState, onLapResetClick: () -> Unit, onStopStartClick: () -> Unit, modifier: Modifier = Modifier) {
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
        Button(onClick = onStopStartClick) {
            Text(
                text = if (state.isRunning) "Stop" else "Start",
                fontSize = 24.sp
            )
        }
    }
}

/**
 * Lap 列表
 *
 * @param lapTimeList
 * @param modifier
 */
@Composable
fun Laps(lapTimeList: List<Long>, modifier: Modifier = Modifier) {
    // 记录 LazyColumn 的列表状态, 用于自动滚动到最新 lap
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

/**
 * 单个 lap
 *
 * @param lapId
 * @param time
 * @param modifier
 */
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

@Preview(
    showBackground = true
)
@Composable
fun StopwatchPreview() {
    val mockState = StopwatchState(true, 47500)
    val mockLapTimeList = listOf<Long>(1, 2, 3, 4, 5, 6, 7, 8)

    StopwatchTheme {
        StopwatchScreen(
            state = mockState,
            lapTimeList = mockLapTimeList,
            onLapResetClick = {},
            onStopStartClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 500,
    widthDp = 1000
)
@Composable
fun StopwatchLandscapePreview() {
    val mockState = StopwatchState(true, 47500)
    val mockLapTimeList = listOf<Long>(1, 2, 3, 4, 5, 6, 7, 8)

    StopwatchTheme {
        StopwatchScreenLandscape(
            state = mockState,
            lapTimeList = mockLapTimeList,
            onLapResetClick = {},
            onStopStartClick = {}
        )
    }
}