package com.myungwoo.shoppingmall_app.piggybank

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PiggyActivity : AppCompatActivity() {
    private val viewModel: PiggyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PiggyScreen(viewModel)
            }
        }
    }
}

@Composable
fun PiggyScreen(viewModel: PiggyViewModel) {
    var streamText by remember { mutableStateOf('A') }

    LaunchedEffect(Unit) {
        viewModel.zipFlow.collect { char ->
            Log.e("PiggyActivity", "char = $char")
            streamText = char
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { viewModel.onButtonClick() }) {
            Text(text = "Click Me")
        }
        Text(
            text = streamText.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 50.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}