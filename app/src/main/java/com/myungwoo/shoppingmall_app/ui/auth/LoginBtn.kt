package com.myungwoo.shoppingmall_app.ui.auth

import androidx.annotation.DrawableRes
import com.myungwoo.shoppingmall_app.R

enum class LoginBtn(@DrawableRes val resId: Int, val contentDescription: String) {
    EMAil(resId = R.drawable.login_email, contentDescription = "Email Login Icon"),
    KAKAO(resId = R.drawable.login_kakao, contentDescription = "Kakao Login Icon"),
    GOOGLE(resId = R.drawable.login_google, contentDescription = "GOOGle Login Icon"),
    JOIN(resId = R.drawable.login_sign, contentDescription = "Join Login Icon"),
}