package com.myungwoo.shoppingmall_app.contentList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.product.ProductModel
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ContentListActivity : AppCompatActivity() {
    lateinit var myRef: DatabaseReference
    //북마크 key값을 담을 리스트
    val bookmarkIdList = mutableListOf<String>()
    lateinit var rvAdapter: ContentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_list)

        val items = ArrayList<ContentModel>()
        val itemKeyList = ArrayList<String>()
        rvAdapter = ContentRVAdapter(baseContext, items, itemKeyList, bookmarkIdList)

        // Write a message to the database
        val database = Firebase.database

        val category = intent.getStringExtra("category")
        if (category == "categoryALl") {
            myRef = FBRef.conetent.child("categoryALl")
        } else if (category == "categoryLip") {
            myRef = FBRef.conetent.child("categoryLip")
        } else if (category == "categoryBlusher") {
            myRef = FBRef.conetent.child("categoryBlusher")
        } else if (category == "categoryMascara") {
            myRef = FBRef.conetent.child("categoryMascara")
        } else if (category == "categoryNail") {
            myRef = FBRef.conetent.child("categoryNail")
        } else if (category == "categoryShadow") {
            myRef = FBRef.conetent.child("categoryShadow")
        } else if (category == "categorySkin") {
            myRef = FBRef.conetent.child("categorySkin")
        } else if (category == "categorySun") {
            myRef = FBRef.conetent.child("categorySun")
        }


        //파이어베이스 데이터 읽기
        val postListener = object : ValueEventListener {
            //Content 데이터읽기
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    //contents id값 가져오기
                    Log.d("ContentListActivity", dataModel.key.toString())
                    //ContentModel의 형태로 데이터를 받아옴
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    //contentKey 를 데이터베이스에 넣어줌
                    itemKeyList.add(dataModel.key.toString())
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)

        //RecyclerView와 Adapter 연결하기
        val rv: RecyclerView = findViewById(R.id.rv)
        rv.adapter = rvAdapter
        //RecyclerView 3줄로 나오게 하기
        rv.layoutManager = GridLayoutManager(this, 2)
        getBookmarkData()


    }
    private fun getBookmarkData(){
        //파이어베이스 데이터 읽기(북마크)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //데이터가 변경될 때 기존에 데이터는 clear 해주고, 다시 값을 받아줌
                bookmarkIdList.clear()

                for (dataModel in dataSnapshot.children) {
                    bookmarkIdList.add(dataModel.key.toString())
                }
                rvAdapter.notifyDataSetChanged()
                Log.d("북마크데이터", bookmarkIdList.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }
}