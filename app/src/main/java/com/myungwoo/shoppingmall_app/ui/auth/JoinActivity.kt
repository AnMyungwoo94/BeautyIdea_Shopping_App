package com.myungwoo.shoppingmall_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.MainActivity

private lateinit var auth: FirebaseAuth

class JoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                auth = Firebase.auth
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    JoinActivityCompose()
                }
            }
        }
    }
}

@Composable
fun JoinActivityCompose() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }
    var isGoToJoin by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(id = R.string.join_auth_btn),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.join_icon),
            contentDescription = "사과"
        )
        Spacer(modifier = Modifier.padding(16.dp))

        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                cursorColor = Color.DarkGray
            ),
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                cursorColor = Color.DarkGray
            ),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                cursorColor = Color.DarkGray
            ),
            value = passwordCheck,
            onValueChange = { passwordCheck = it },
            label = { Text("Password Check") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {
                isGoToJoin = true

                if (email.isEmpty()) {
                    Toast.makeText(context, R.string.join_verify_email, Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                if (password.isEmpty()) {
                    Toast.makeText(context, R.string.join_verify_pw, Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                if (passwordCheck.isEmpty()) {
                    Toast.makeText(context, R.string.join_verify_pw_confirm, Toast.LENGTH_SHORT)
                        .show()
                    isGoToJoin = false
                }

                if (password != passwordCheck) {
                    Toast.makeText(context, R.string.join_verify_pw_fail, Toast.LENGTH_SHORT).show()
                    isGoToJoin = false
                }

                if (password.length < 6) {
                    Toast.makeText(context, R.string.join_verify_pw_pattern, Toast.LENGTH_SHORT)
                        .show()
                    isGoToJoin = false
                }

                if (isGoToJoin) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, R.string.join_auth_fail, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black)

        ) {
            Text("작성완료")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinActivityComposePreview() {
    MaterialTheme {
        JoinActivityCompose()
    }
}