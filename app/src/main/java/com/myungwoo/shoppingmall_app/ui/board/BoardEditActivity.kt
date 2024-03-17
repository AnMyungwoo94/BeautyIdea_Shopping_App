package com.myungwoo.shoppingmall_app.ui.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityBoardEditBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.shoppingmall_app.data.BoardModel

class BoardEditActivity : AppCompatActivity() {
    private lateinit var key: String
    private lateinit var binding: ActivityBoardEditBinding
    private var tag = BoardEditActivity::class.java.simpleName
    private lateinit var writerUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)

        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)
        binding.editBtn.setOnClickListener {
            editBoardData(key)
        }
    }

    private fun editBoardData(key: String) {
        FBRef.boardRef.child(key)
            .setValue(BoardModel(binding.titleArea.text.toString(), binding.contentArea.text.toString(), writerUid, FBAuth.getTime()))
        Toast.makeText(this, R.string.board_edit_set_value, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")
        val imageViewFromFB = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                Log.e(tag, "getImageData error")
            }
        }
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(tag, dataModel!!.title)
                    binding.titleArea.setText(dataModel.title)
                    binding.contentArea.setText(dataModel.content)
                    writerUid = dataModel.uid
                } catch (e: Exception) {
                    Log.d(tag, "remove success")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(tag, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }
}