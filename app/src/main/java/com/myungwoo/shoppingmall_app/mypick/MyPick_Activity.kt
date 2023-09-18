package com.myungwoo.shoppingmall_app.mypick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityMyPickBinding

class MyPick_Activity : AppCompatActivity() {

    private lateinit var binding :  ActivityMyPickBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}