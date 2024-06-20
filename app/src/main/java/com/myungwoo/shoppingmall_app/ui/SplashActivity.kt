package com.myungwoo.shoppingmall_app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.myungwoo.data.repository.UserRepository
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.auth.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            SplashScreen(userRepository)
        }
    }
}

@Composable
fun SplashScreen(userRepository: UserRepository) {
    val context = LocalContext.current
    val email by userRepository.email.collectAsState(initial = null)
    val password by userRepository.password.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        delay(1000)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            context.startActivity(Intent(context, MainActivity::class.java))
        } else {
            context.startActivity(Intent(context, IntroActivity::class.java))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Splash Image",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        //SplashScreen()
    }
}


