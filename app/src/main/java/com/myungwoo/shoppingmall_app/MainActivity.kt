package com.myungwoo.shoppingmall_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.NavHostFragment
import com.myungwoo.shoppingmall_app.databinding.ActivityMainBinding
import com.myungwoo.shoppingmall_app.product.ProductCart_Activity
import com.myungwoo.shoppingmall_app.setting.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

       binding.settingBtn.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

       binding.cartBtn.setOnClickListener {
            val intent = Intent(this, ProductCart_Activity::class.java)
            startActivity(intent)
        }

//        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
//           auth.signOut()
//
//            val intent = Intent(this, IntroActivity::class.java)
//            //회원가입 후 뒤로가기 눌렀을때 앱이 종료하도록 설정
//           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//
//        }
    }
}