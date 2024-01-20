package com.myungwoo.shoppingmall_app.product

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.myungwoo.shoppingmall_app.databinding.ActivityProductInputBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ProductInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductInputBinding
    private var isImageUpload = false
    private var key: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val time = FBAuth.getTime()

        binding.btnSave.setOnClickListener {
            val name = binding.productName.text.toString()
            val price = binding.productPrice.text.toString()
            val parcel = binding.productParcel.text.toString()
            val parcel_day = binding.productParcelDay.text.toString()
            val delivery_fee = binding.deliveryFee.text.toString().toInt()
            val count = 1
            val count_sum = 0

            key = FBRef.productRef.push().key.toString()
            FBRef.productRef.child(key)
                .setValue(ProductModel(key, name, price, time, parcel, delivery_fee, parcel_day, count, count_sum, isImageUpload))

            Toast.makeText(this, "게시글 입력완료", Toast.LENGTH_SHORT).show()
            if (isImageUpload) {
                imageUpload(key)
            }
            finish()

        }
        binding.ProductPicture.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }
    }

    private fun imageUpload(key: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child(key + ".png")

        val imageView = binding.ProductPicture
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
        }.addOnSuccessListener { taskSnapshot ->
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.ProductPicture.setImageURI(data?.data)
        }
    }
}