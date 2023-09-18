package com.myungwoo.shoppingmall_app.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityProductSuccessBinding
import com.myungwoo.shoppingmall_app.setting.SettingActivity

class ProductSuccess_Activity : AppCompatActivity() {
    private lateinit var binding : ActivityProductSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMypage.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

}