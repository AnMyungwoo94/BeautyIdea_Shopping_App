package com.myungwoo.shoppingmall_app.mypick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myungwoo.shoppingmall_app.databinding.ActivityMyPickBinding

class MyPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPickBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}