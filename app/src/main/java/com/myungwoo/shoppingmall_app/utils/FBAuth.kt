package com.myungwoo.shoppingmall_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class FBAuth {
    companion object {
        private lateinit var auth: FirebaseAuth

        fun getUid(): String {

            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
        }

        fun getTime(): String {
            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat(
                "yyyy.MM.dd HH:mm:ss",
                Locale.KOREA
            ).format(currentDateTime)
            return dateFormat
        }
    }
}