package com.myungwoo.shoppingmall_app.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token =
            runBlocking { FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token }
        val requestBuilder = originalRequest.newBuilder()

        token?.let {
            val url = originalRequest.url.newBuilder()
                .addQueryParameter("auth", it)
                .build()
            requestBuilder.url(url)
        }
        return chain.proceed(requestBuilder.build())
    }
}