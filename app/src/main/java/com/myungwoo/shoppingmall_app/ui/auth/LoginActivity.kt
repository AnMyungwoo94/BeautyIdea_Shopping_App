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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.MainActivity
import com.myungwoo.shoppingmall_app.ui.auth.component.AuthOutlinedTextField

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LOGIN",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 50.dp, bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.login_icon),
            contentDescription = "로그인 이미지"
        )
        Spacer(modifier = Modifier.padding(24.dp))
        Text(
            text = "아이디와 비밀번호를 입력해주세요",
            fontSize = 15.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))
        AuthOutlinedTextField(value = email, onValueChange = { email = it }, label = "Email")
        AuthOutlinedTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
        Spacer(modifier = Modifier.padding(16.dp))
        LoginBtn(email, password)
    }
}

@Composable
fun LoginBtn(email: String, password: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(context, R.string.login_empty, Toast.LENGTH_SHORT).show()
            } else {
                val auth: FirebaseAuth = Firebase.auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(context, MainActivity::class.java)
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, R.string.login_fail, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        },
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text("로그인", fontSize = 15.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    MaterialTheme {
        LoginBtn("", "")
    }
}