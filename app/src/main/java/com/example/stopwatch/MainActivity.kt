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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun StopwatchApp(stopwatchViewModel: StopwatchViewModel = viewModel()) {
    val state by stopwatchViewModel.state

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
                stopwatchViewModel = stopwatchViewModel,
                modifier = modifier
            )
            Spacer(modifier = modifier.height(20.dp))
            ControlButtons(
                stopwatchViewModel = stopwatchViewModel,
                modifier = modifier
            )
            Spacer(modifier = modifier.height(20.dp))
            Laps(
                stopwatchViewModel = stopwatchViewModel,
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
fun Gauge(stopwatchViewModel: StopwatchViewModel, modifier: Modifier = Modifier) {
    /*Box {
        CircularProgressIndicator( // TODO
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
        CircularProgressIndicator( // TODO
            progress = TimeUtils.minuteProgress(stopwatchViewModel.state.value.elapsedTime)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = TimeUtils.long2String(stopwatchViewModel.state.value.elapsedTime)
        )
    }
}

@Composable
fun ControlButtons(stopwatchViewModel: StopwatchViewModel, modifier: Modifier = Modifier) {
    Row (
        // modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            if (stopwatchViewModel.state.value.isRunning)
                // stopwatchViewModel.lap()
            else
                stopwatchViewModel.reset()
        }) {
            Text(
                text = if (stopwatchViewModel.state.value.isRunning) "Lap" else "Reset"
            )
        }
        Spacer(modifier = Modifier.width(30.dp))
        Button(onClick = {
            if (stopwatchViewModel.state.value.isRunning)
                stopwatchViewModel.pause()
            else
                stopwatchViewModel.start()
        }) {
            Text(
                text = if (stopwatchViewModel.state.value.isRunning) "Stop" else "Start"
            )
        }
    }
}

@Composable
fun Laps(stopwatchViewModel: StopwatchViewModel, modifier: Modifier = Modifier) {
    Column (

    ) {
        Lap(3, "xx:xx:xxx", modifier)
        Lap(2, "xx:xx:xxx", modifier)
        Lap(1, "xx:xx:xxx", modifier)
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
fun GreetingPreview() {
    StopwatchTheme {
        StopwatchApp()
    }
}