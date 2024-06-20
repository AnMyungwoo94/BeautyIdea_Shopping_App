package com.myungwoo.shoppingmall_app.ui.auth.kakao

object KakaoUserInfo {
    private var kakaoEmail: String? = null
    private var kakaoNickName: String? = null

    fun setKakaoEmail(email: String) {
        kakaoEmail = email
    }

    fun setKakaoNickName(nickname: String) {
        kakaoNickName = nickname
    }

    fun getKakaoEmail(): String? {
        return kakaoEmail
    }

    fun getKakaoNickName(): String? {
        return kakaoNickName
    }
}