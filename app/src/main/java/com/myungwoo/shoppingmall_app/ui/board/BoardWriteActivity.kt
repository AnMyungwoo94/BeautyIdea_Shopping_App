package com.myungwoo.shoppingmall_app.ui.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.model.BoardModel
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityBoardWriteBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardWriteBinding
    private var isImageUpload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.writeBtn.setOnClickListener {
            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            val key = FBRef.boardRef.push().key.toString()
            FBRef.boardRef.child(key).setValue(
                BoardModel(
                    title,
                    content,
                    uid,
                    time
                )
            )

            Toast.makeText(this, R.string.board_write_write_success, Toast.LENGTH_SHORT).show()
            if (isImageUpload == true) {
                imageUpload(key)
            }
            finish()

        }
        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }
    }

    private fun imageUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.imageArea.setImageURI(data?.data)
        }
    }
}