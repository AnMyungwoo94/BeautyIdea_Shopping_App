package com.myungwoo.shoppingmall_app.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myungwoo.shoppingmall_app.databinding.ActivityProductSuccessBinding
import com.myungwoo.shoppingmall_app.ui.setting.SettingActivity

class ProductSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMypage.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}