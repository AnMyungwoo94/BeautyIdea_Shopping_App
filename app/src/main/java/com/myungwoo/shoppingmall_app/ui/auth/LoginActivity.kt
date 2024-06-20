package com.myungwoo.shoppingmall_app.ui.auth

import android.content.Context
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
import com.myungwoo.data.repository.UserRepository
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.common.compose.component.AuthOutlinedTextField
import com.myungwoo.shoppingmall_app.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(userRepository)
            }
        }
    }
}

@Composable
fun LoginScreen(userRepository: UserRepository? = null) {
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
            text = stringResource(id = R.string.login_login),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 50.dp, bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.login_icon),
            contentDescription = stringResource(id = R.string.login_icon_content_image)
        )
        Spacer(modifier = Modifier.padding(24.dp))
        Text(
            text = stringResource(id = R.string.login_prompt),
            fontSize = 15.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))
        AuthOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = R.string.login_email
        )
        AuthOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = R.string.login_password,
            isPassword = true
        )
        Spacer(modifier = Modifier.padding(16.dp))
        if (userRepository != null) {
            LoginBtn(email, password, userRepository)
        }
    }
}

@Composable
fun LoginBtn(email: String, password: String, userRepository: UserRepository? = null) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (validateLoginInputs(email, password, context)) {
                if (userRepository != null) {
                    signInWithEmailAndPassword(email, password, context, userRepository)
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
        Text(stringResource(id = R.string.login_button), fontSize = 15.sp)
    }
}

private fun validateLoginInputs(email: String, password: String, context: Context): Boolean {
    return when {
        email.isEmpty() && password.isEmpty() -> {
            Toast.makeText(context, R.string.login_empty, Toast.LENGTH_SHORT).show()
            false
        }

        email.isEmpty() -> {
            Toast.makeText(context, R.string.login_empty_email, Toast.LENGTH_SHORT).show()
            false
        }

        password.isEmpty() -> {
            Toast.makeText(context, R.string.login_empty_password, Toast.LENGTH_SHORT).show()
            false
        }

        else -> true
    }
}

private fun signInWithEmailAndPassword(
    email: String,
    password: String,
    context: Context,
    userRepository: UserRepository
) {
    val auth: FirebaseAuth = Firebase.auth

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                runBlocking {
                    userRepository.saveUserLogin(email, password)
                }
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            } else {
                Toast.makeText(context, R.string.login_fail, Toast.LENGTH_SHORT).show()
            }
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
        LoginBtn(
            email = "test@example.com",
            password = "password"
        )
    }
}
