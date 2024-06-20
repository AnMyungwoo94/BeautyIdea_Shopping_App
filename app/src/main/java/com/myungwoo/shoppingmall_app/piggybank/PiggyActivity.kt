package com.myungwoo.shoppingmall_app.piggybank

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class PiggyActivity : AppCompatActivity() {
    private val viewModel: PiggyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val buttonText = remember { mutableStateOf("Click Me") }

                    viewModel.buttonClicked.observe(this) { clicked ->
                        if (clicked) {
                            buttonText.value = "Button Clicked"
                        }
                    }

                    MyButton(viewModel, buttonText.value)
                }
            }
        }
    }
}

@Composable
fun MyButton(viewModel: PiggyViewModel, buttonText: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { viewModel.onButtonClick() }) {
            Text(text = buttonText)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyButtonPreview() {
    val viewModel: PiggyViewModel = viewModel()
    val buttonText = remember { mutableStateOf("Click Me") }

    viewModel.buttonClicked.observeForever { clicked ->
        if (clicked) {
            buttonText.value = "Button Clicked"
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MyButton(viewModel, buttonText.value)
        }
    }
}
