package com.myungwoo.shoppingmall_app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
       private val database = Firebase.database
        val user = FirebaseAuth.getInstance().currentUser
        //category 컨텐츠
        val conetent =  database.getReference("content")
        //bookmarkRef 사용자가 북마크한 정보
        val bookmarkRef = database.getReference("bookmark_list")
        //bookRef 게시글 작성 정보
        val boardRef = database.getReference("board")
        val commentRef  =database.getReference("comment")
        val productRef =  database.getReference("product")
        val deliveryRef =  database.getReference("orders").child(user!!.uid)

    }

}