package com.myungwoo.shoppingmall_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class FBAuth {

    companion object{
        private lateinit var auth: FirebaseAuth

        fun getUid() : String {

            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
        }

        fun getTime() : String {
            //1. Calendar에 있는 시간을 받음
            val currentDateTime = Calendar.getInstance().time
            //2. 어떤 형식으로 받을지 설정
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss",
                Locale.KOREA).format(currentDateTime)

            return dateFormat
        }
    }
}