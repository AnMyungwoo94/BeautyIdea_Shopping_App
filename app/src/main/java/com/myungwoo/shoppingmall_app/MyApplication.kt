package com.myungwoo.shoppingmall_app

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.myungwoo.shoppingmall_app.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.kakao_native_app_key)
    }
}