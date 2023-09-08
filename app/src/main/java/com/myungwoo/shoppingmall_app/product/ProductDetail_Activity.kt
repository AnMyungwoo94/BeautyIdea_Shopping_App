package com.myungwoo.shoppingmall_app.product

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivityProductDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*


class ProductDetail_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private var count: Int = 1
    private var count_sum : Int = 0
    private var productPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.detailText.setMovementMethod(ScrollingMovementMethod())

        //ProductRvAdapterd 에서 받아옴
        val receivedData = intent.getSerializableExtra("ITEM_DATA") as ProductModel

        binding.productName.text = receivedData.name
        binding.productPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(receivedData.price.toInt())} "
        binding.productParcel.text = receivedData.parcel
        var deliveryFee = receivedData.delivery_fee
        if(deliveryFee != 0){
            binding.deliveryFee.text = "배송비 ${NumberFormat.getNumberInstance(Locale.US).format(deliveryFee)} 원"
        } else {
            binding.deliveryFee.text = "배송비 무료"
        }

        binding.productParcelDay.text = receivedData.parcel_day
        binding.selectedProductName.text = receivedData.name
        binding.selectedProductPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(receivedData.price.toInt())} "
        productPrice = receivedData.price.toInt()


        val product_name = binding.productName.text
        val pictureRef = Firebase.storage.reference.child("${receivedData.key}.png")
        pictureRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                // Glide 를 통하여 imageView에 사진 로드
                Glide.with(this).load(it.result).into(binding.productImage)
                Glide.with(this).load(it.result).into(binding.selectedProductImage)
            }
        }

        // 수량 증가 버튼의 클릭 리스너 설정
        binding.plusBtn.setOnClickListener {
            count += 1
            updateQuantityAndTotalPrice()
        }

        // 수량 감소 버튼의 클릭 리스너 설정
        binding.minusBtn.setOnClickListener {
            if (count > 1) {
                count -= 1
                updateQuantityAndTotalPrice()
            }
        }
        binding.btnBasket.setOnClickListener {

            // Firebase Realtime Database 참조 가져오기
            val database = FirebaseDatabase.getInstance().reference

            // 현재 사용자의 UID를 가져오기 (Firebase Authentication 사용하는 경우)
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uid = it.uid
               val key = receivedData.key

                val updatedProduct = receivedData.copy(count = count, count_sum = count_sum)

                // 데이터베이스에 상품 데이터 저장하기 (경로: cart/{uid}/{productKey})
                database.child("cart").child(uid).child(key).setValue(updatedProduct)
                    .addOnSuccessListener {
                        // 데이터베이스에 성공적으로 저장되었을 때의 로직
                        Toast.makeText(this, "상품이 장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()

                        // Intent 객체 생성 및 시작
                        val intent = Intent(this, ProductCart_Activity::class.java)
                        intent.putExtra("SELECTED_PRODUCT_PAY", receivedData)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        // 데이터 저장에 실패했을 때의 로직
                        Toast.makeText(this, "장바구니에 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.btnPay.setOnClickListener {
            val intent = Intent(this, ProductPay_Activity::class.java)
            intent.putExtra("SELECTED_PRODUCT_PAY", receivedData)
            intent.putExtra("COUNT", count)
            intent.putExtra("COUNT_SUM", count_sum)
            intent.putExtra("origin", "DETAILS")
            startActivity(intent)
        }

        // 초기 총액 설정
        updateQuantityAndTotalPrice()
    }


    private fun updateQuantityAndTotalPrice() {
        // 변경된 수량을 화면에 표시
        findViewById<TextView>(R.id.quantity_text_view).text = count.toString()

        // 총액 계산 및 화면에 표시
        val totalPrice = count * productPrice
        binding.sumText.text = "${NumberFormat.getNumberInstance(Locale.US).format(totalPrice)} "
        count_sum = totalPrice
    }
}