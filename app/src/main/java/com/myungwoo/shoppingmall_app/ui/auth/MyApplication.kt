package com.myungwoo.shoppingmall_app.ui.auth

import android.app.Application
import com.myungwoo.shoppingmall_app.BuildConfig
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.kakao_native_app_key)
    }
}