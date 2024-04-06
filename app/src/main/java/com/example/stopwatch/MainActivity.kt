package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun StopwatchScreen(
    state: StopwatchState,
    lapTimeList: List<Long>,
    onLapResetClick: () -> Unit,
    onStartStopClick: () -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column (
            modifier = modifier.padding(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.height(20.dp))
            Title(
                text = stringResource(R.string.title),
                modifier = modifier
            )
            Spacer(modifier = modifier.height(20.dp))
            Gauge(
                state = state,
                modifier = modifier,
            )
            Spacer(modifier = modifier.height(20.dp))
            ControlButtons(
                state = state,
                onLapResetClick = onLapResetClick,
                onStartStopClick = onStartStopClick,
                modifier = modifier
            )
            Spacer(modifier = modifier.height(20.dp))
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
    /*Box {
        CircularProgressIndicator( //TODO
            progress = 0.5f
        )
        Text(
            text = "XX:XX:XX"
        )
    }*/
    Column (
        modifier = modifier,
        // verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator( //TODO
            progress = TimeUtils.minuteProgress(state.elapsedTime)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = TimeUtils.long2String(state.elapsedTime)
        )
    }
}

@Composable
fun ControlButtons(state: StopwatchState, onLapResetClick: () -> Unit, onStartStopClick: () -> Unit, modifier: Modifier = Modifier) {
    // val state by stopwatchViewModel.state //TODO 只检测其中的 isRunning
    Row (
        // modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onLapResetClick) {
            Text(
                text = if (state.isRunning) "Lap" else "Reset"
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Button(onClick = onStartStopClick) {
            Text(
                text = if (state.isRunning) "Stop" else "Start"
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
        modifier = modifier,
        state = listState,
        reverseLayout = true
    ) {
        itemsIndexed(lapTimeList) { index, lapTime ->
            Lap(
                lapId = index + 1,
                time = TimeUtils.long2String(lapTime),
                modifier = modifier
                    .padding(top = 10.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
fun Lap(lapId: Int, time: String, modifier: Modifier = Modifier) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Lap $lapId")
        Text(text = time)
    }
}

@Preview(showBackground = true)
@Composable
fun StopwatchPreview() {
    val mockState = StopwatchState(true, 17500) // Replace StopwatchState and ... with the actual type and value
    val mockLapTimeList = listOf<Long>(1100, 2300, 3500, 4700, 5900) // Replace List<Long> and ... with the actual type and value

    StopwatchTheme {
        StopwatchScreen(
            state = mockState,
            lapTimeList = mockLapTimeList,
            onLapResetClick = {},
            onStartStopClick = {}
        )
    }
}