package com.myungwoo.shoppingmall_app.ui.board

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.model.BoardModel
import com.myungwoo.model.CommentModel
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityBoardInsideBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef

class BoardInsideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardInsideBinding
    private lateinit var commentAdapter: CommentLVAdapter
    private lateinit var key: String
    private val tag = BoardInsideActivity::class.java.simpleName
    private val commentDataList = mutableListOf<CommentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)

        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }

        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)

        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }
        getCommentData(key)

        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter

    }

    private fun getCommentData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentDataList.clear()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(tag, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    private fun insertComment(key: String) {
        FBRef.commentRef.child(key).push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime()
                )
            )
        Toast.makeText(this, R.string.board_inside_comment_success, Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(R.string.board_inside_dialog_tilte)

        val alertDialog = builder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            Toast.makeText(this, R.string.board_inside_modify, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
            finish()
        }
        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this, R.string.board_inside_remove_success, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")
        val imageViewFromFB = binding.getImageArea

        storageReference.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                binding.getImageArea.isVisible = false
            }
        }
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(tag, dataModel!!.title)

                    binding.titleArea.text = dataModel.title
                    binding.textArea.text = dataModel.content
                    binding.timeArea.text = dataModel.time

                    val myUid = FBAuth.getUid()
                    val writeUid = dataModel.uid

                    if (myUid.equals(writeUid)) {
                        binding.boardSettingIcon.isVisible = true
                    } else {

                    }
                } catch (e: Exception) {
                    Log.d(tag, "삭제완료")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(tag, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }
}