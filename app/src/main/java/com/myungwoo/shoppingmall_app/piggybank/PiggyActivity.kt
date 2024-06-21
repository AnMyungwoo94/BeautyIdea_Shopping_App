package com.myungwoo.shoppingmall_app.piggybank

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var streamText by remember { mutableStateOf("입력값") }
    var inputNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.emitIntFlow.collect {
            Log.d("PiggyActivity", "방출된 값 : $it")
            streamText = it.toString()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = inputNumber,
            placeholder = {
                Text(text = "숫자를 입력해 주세요", color = Color.Gray, fontSize = 14.sp)
            },
            onValueChange = {
                inputNumber = it
            },
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
        )
        Button(onClick = {
            viewModel.onButtonClick(inputNumber)
            inputNumber = ""
        }) {
            Text(text = "전송하기")
        }
        Text(
            text = streamText,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 50.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}