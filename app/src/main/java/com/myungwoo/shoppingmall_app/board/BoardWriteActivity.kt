package com.myungwoo.shoppingmall_app.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityBoardWriteBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardWriteBinding
    private val TAG = BoardWriteActivity::class.java.simpleName
    private var isImageUpload = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        binding.writeBtn.setOnClickListener {
            //writeBtn을 클릭하면 입력된 값을 받아온다.
            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            Log.d(TAG, title.toString())
            Log.d(TAG, content.toString())

            // 파이어베이스 store에 이미지를 저장하고 싶다.
            // 만약에 내가 게시글을 클릭했을 때, 게시글에 대한 정보를 받아와야 하는데
            // 이미지 이름에 대한 정보를 모르기 때문에
            // 이미지 이름을 문서의 key값으로 해줘서 이미지에 대한 정보를 찾기 쉽게 해놓음.
            //이미지를 파일명을 key값으로 하지 않는다면 주석처리된 push()로 사용
            val key = FBRef.boardRef.push().key.toString()
            FBRef.boardRef.child(key).setValue(BoardModel(title, content, uid, time))

            //push()를 하면 랜덤한 key값이 생성됨
//            FBRef.boardRef.push().setValue(BoardModel(title, content, uid, time))

            Toast.makeText(this, "게시글 입력완료", Toast.LENGTH_SHORT).show()
           if( isImageUpload == true ){
            imageUpload(key)
           }
            finish()

        }
        binding.imageArea.setOnClickListener {
            //갤러리에서 이미지 가져오기
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }
    }

    //Firebase에 이미지 저장하기
    private fun imageUpload(key : String){
        // Get the data from an ImageView as bytes
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")

        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
        }
    }

    //onActivityResult 갤러리에서 이미지 가져와서 imageArea에 넣어주기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode==100){
            binding.imageArea.setImageURI(data?.data)
        }
    }
}