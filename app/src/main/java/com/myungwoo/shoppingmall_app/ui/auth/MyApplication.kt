package com.myungwoo.shoppingmall_app.ui.auth

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //KakaoSdk.init(this, BuildConfig.kakao_native_app_key)
    }
}