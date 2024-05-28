package com.myungwoo.shoppingmall_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.ui.MainActivity

class IntroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MaterialTheme {
                IntroScreen(
                    onLoginClick = { navigateToLogin() },
                    onJoinClick = { navigateToJoin() },
                    onGoogleSignInClick = { signInWithGoogle() },
                    onKakaoSignInClick = { signInWithKakao() }
                )
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun navigateToJoin() {
        startActivity(Intent(this, JoinActivity::class.java))
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signInWithKakao() {
        val context = this.applicationContext
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("IntroActivity", "카카오 로그인 실패", error)
            } else if (token != null) {
                UserApiClient.instance.me { user, meError ->
                    if (meError != null) {
                        Log.e("IntroActivity", "카카오 사용자 정보 가져오기 실패", meError)
                    } else if (user != null) {
                        //val email = user.kakaoAccount?.email
                        val nickname = user.kakaoAccount?.profile?.nickname
                        if (nickname != null) {
                            // 카카오 사용자 정보를 Firebase에 저장하거나 다음 화면으로 이동
                            Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signInAnonymously()
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Log.i("IntroActivity", "Firebase 인증 성공")
                                        toMainActivity()
                                    } else {
                                        Log.e("IntroActivity", "Firebase 인증 실패", task.exception)
                                    }
                                }
                        }
                    }
                }
            }
        }

        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.w("IntroActivity", "Google sign in failed", e)
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                toMainActivity()
            } else {
                Log.w("IntroActivity", "firebaseAuthWithGoogle 실패", task.exception)
            }
        }
    }

    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun IntroScreen(
    onLoginClick: () -> Unit,
    onJoinClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onKakaoSignInClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.main_icon),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.padding(32.dp))
            LoginClickBtn(onLoginClick, LoginBtn.EMAil)
            LoginClickBtn(onGoogleSignInClick, LoginBtn.GOOGLE)
            LoginClickBtn(onKakaoSignInClick, LoginBtn.KAKAO)
            LoginClickBtn(onJoinClick, LoginBtn.JOIN)
        }
    }
}

@Composable
fun LoginClickBtn(
    onClick: () -> Unit,
    loginBtnEnum: LoginBtn,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        elevation = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)

    ) {
        Image(
            painter = painterResource(loginBtnEnum.resId),
            contentDescription = loginBtnEnum.contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    MaterialTheme {
        IntroScreen(
            onLoginClick = {},
            onJoinClick = {},
            onGoogleSignInClick = {},
            onKakaoSignInClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginClickBtnPreview() {
    MaterialTheme {
        LoginClickBtn(onClick = {}, loginBtnEnum = LoginBtn.EMAil)
    }
}
