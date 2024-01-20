package com.myungwoo.shoppingmall_app.contentList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.myungwoo.shoppingmall_app.R

class ContentShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_show)

        val getUri = intent.getStringExtra("uri")

        val webView: WebView = findViewById(R.id.webView)
        webView.loadUrl(getUri.toString())
    }
}