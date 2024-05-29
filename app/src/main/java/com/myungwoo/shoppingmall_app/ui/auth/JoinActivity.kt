package com.myungwoo.shoppingmall_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.common.compose.component.AuthOutlinedTextField
import com.myungwoo.shoppingmall_app.ui.MainActivity

class JoinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                JoinScreen()
            }
        }
    }
}

@Composable
fun JoinScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.join_auth_btn),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.join_icon),
            contentDescription = stringResource(id = R.string.join_icon_content_desc)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        AuthOutlinedTextField(value = email, onValueChange = { email = it }, label = R.string.login_email)
        AuthOutlinedTextField(value = password, onValueChange = { password = it }, label = R.string.login_password, isPassword = true)
        AuthOutlinedTextField(value = passwordCheck, onValueChange = { passwordCheck = it }, label = R.string.join_password_check, isPassword = true)
        Spacer(modifier = Modifier.padding(16.dp))
        JoinBtn(email, password, passwordCheck)
    }
}

@Composable
fun JoinBtn(email: String, password: String, passwordCheck: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (validateJoinInputs(email, password, passwordCheck, context)) {
                createAccountWithEmailAndPassword(email, password, context)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.Black)

    ) {
        Text(stringResource(id = R.string.join_submit))
    }
}

private fun validateJoinInputs(email: String, password: String, passwordCheck: String, context: android.content.Context): Boolean {
    return when {
        email.isEmpty() -> {
            Toast.makeText(context, R.string.join_verify_email, Toast.LENGTH_SHORT).show()
            false
        }
        password.isEmpty() -> {
            Toast.makeText(context, R.string.join_verify_pw, Toast.LENGTH_SHORT).show()
            false
        }
        passwordCheck.isEmpty() -> {
            Toast.makeText(context, R.string.join_verify_pw_confirm, Toast.LENGTH_SHORT).show()
            false
        }
        password != passwordCheck -> {
            Toast.makeText(context, R.string.join_verify_pw_fail, Toast.LENGTH_SHORT).show()
            false
        }
        password.length < 6 -> {
            Toast.makeText(context, R.string.join_verify_pw_pattern, Toast.LENGTH_SHORT).show()
            false
        }
        else -> true
    }
}

private fun createAccountWithEmailAndPassword(email: String, password: String, context: android.content.Context) {
    val auth: FirebaseAuth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, R.string.join_auth_fail, Toast.LENGTH_SHORT).show()
            }
        }
}

@Preview(showBackground = true)
@Composable
fun JoinActivityComposePreview() {
    MaterialTheme {
        JoinScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun JoinBtnPreview() {
    MaterialTheme {
        JoinBtn(email = "", password = "", passwordCheck = "")
    }
}
