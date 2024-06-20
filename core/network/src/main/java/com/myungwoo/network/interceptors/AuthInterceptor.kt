package com.myungwoo.network.interceptors

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking {
            try {
                FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Failed : ${e.localizedMessage}")
                null
            }
        }
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
