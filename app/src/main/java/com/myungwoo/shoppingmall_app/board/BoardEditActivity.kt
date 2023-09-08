package com.myungwoo.shoppingmall_app.board

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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardEditActivity : AppCompatActivity() {
    private lateinit var key: String
    private lateinit var binding : ActivityBoardEditBinding
    private var TAG = BoardEditActivity::class.java.simpleName
    private lateinit var writerUid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit )

        key = intent.getStringExtra("key").toString()
        //게시물 수정시 데이터랑, 이미지 불러옴
        getBoardData(key)
        getImageData(key)
        binding.editBtn.setOnClickListener {
        //수정하기
            editBoardData(key)
        }

    }
    private fun editBoardData(key:String){
        FBRef.boardRef.child(key).setValue(BoardModel(binding.titleArea.text.toString(), binding.contentArea.text.toString(), writerUid, FBAuth.getTime()))
        Toast.makeText(this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private  fun getImageData(key: String){
        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // ImageView in your Activity
        val imageViewFromFB = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener(
            OnCompleteListener { task ->
                if(task.isSuccessful){
                    Glide.with(this)
                        .load(task.result)
                        .into(imageViewFromFB)
                }else{
                }
            }
        )
    }
    private fun getBoardData(key: String){
        val postListener = object : ValueEventListener {
            //Content 데이터읽기
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    //수정시 값 불러오기(Time은 자동으로 불러오기 때문에 안함)
                    binding.titleArea.setText(dataModel.title)
                    binding.contentArea.setText(dataModel.content)
                    writerUid = dataModel.uid
                }catch(e:Exception){
                    Log.d(TAG, "삭제완료")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)

    }
}