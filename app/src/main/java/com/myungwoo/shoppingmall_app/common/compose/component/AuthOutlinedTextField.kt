package com.myungwoo.shoppingmall_app.common.compose.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.myungwoo.shoppingmall_app.R

@Composable
fun AuthOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.DarkGray,
            focusedLabelColor = Color.DarkGray,
            cursorColor = Color.DarkGray
        ),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = label)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun AuthOutlinedTextFieldPreview() {
    var text by remember { mutableStateOf("") }

    MaterialTheme {
        AuthOutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = R.string.app_name
        )
    }
}