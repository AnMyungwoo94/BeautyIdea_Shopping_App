package com.myungwoo.shoppingmall_app.piggybank

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val currentChar = viewModel.textStream.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { viewModel.onButtonClick() }) {
            Text(text = "Click Me")
        }
        Text(
            text = currentChar.value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 50.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}