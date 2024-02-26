package com.myungwoo.shoppingmall_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object {
        private val database = Firebase.database
        val user = FirebaseAuth.getInstance().currentUser
        val content = database.getReference("content")
        val bookmarkRef = database.getReference("bookmark_list")
        val boardRef = database.getReference("board")
        val commentRef = database.getReference("comment")
        val productRef = database.getReference("product")
        val couponRef = database.getReference("coupon")
        val deliveryRef = database.getReference("orders").child(user!!.uid)
    }
}