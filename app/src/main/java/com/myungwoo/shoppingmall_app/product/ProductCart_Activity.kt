package com.myungwoo.shoppingmall_app.product

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.shoppingmall_app.databinding.ActivityProductCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.*

class ProductCart_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityProductCartBinding
    private var productCartList = mutableListOf<ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selecteddProduct = intent.getSerializableExtra("SELECTED_PRODUCT") as? ProductModel
        Log.e("ProductCart_Activity", selecteddProduct.toString())


        binding.productRecyclerview.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화 및 체크박스 상태 변경 콜백 설정
        val adapter = ProductCartRvAdapter(this, productCartList) {
            updateTotalAmount()  // 이 부분이 onItemChanged 콜백의 구현입니다.
        }
        binding.productRecyclerview.adapter = adapter

        loadProductsFromFirebase()

        // backButton 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.payBtn.setOnClickListener {
            val intent = Intent(this, ProductPay_Activity::class.java)
            // 선택된 제품들을 Intent에 추가
            val selectedProducts = productCartList.filter { it.isSelected }
            intent.putExtra("SELECTED_PRODUCTS", ArrayList(selectedProducts))
            intent.putExtra("origin", "CART")

            startActivity(intent)
        }
    }

    private fun loadProductsFromFirebase() {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("cart/$userUID")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedList = mutableListOf<ProductModel>()
                for (productSnapshot in dataSnapshot.children) {
                    val key = productSnapshot.child("key").getValue(String::class.java) ?: ""
                    val name = productSnapshot.child("name").getValue(String::class.java) ?: ""
                    val price = productSnapshot.child("price").getValue(String::class.java) ?: ""
                    val time = productSnapshot.child("time").getValue(String::class.java) ?: ""
                    val parcel = productSnapshot.child("parcel").getValue(String::class.java) ?: ""
                    val delivery_feeObj = productSnapshot.child("delivery_fee").getValue()
                    val delivery_fee = when (delivery_feeObj) {
                        is Long -> delivery_feeObj.toInt()
                        is String -> delivery_feeObj.toIntOrNull() ?: 0
                        else -> 0
                    }
                    val parcel_day = productSnapshot.child("parcel_day").getValue(String::class.java) ?: ""
                    val countObj = productSnapshot.child("count").getValue()
                    val count = when (countObj) {
                        is Long -> countObj.toInt()
                        is String -> countObj.toIntOrNull() ?: 0
                        else -> 0
                    }
                    val count_sumObj = productSnapshot.child("count_sum").getValue()
                    val count_sum = when (count_sumObj) {
                        is Long -> count_sumObj.toInt()
                        is String -> count_sumObj.toIntOrNull() ?: 1
                        else -> 1
                    }

                    // 기존 리스트에서 상품 찾기
                    val existingProduct = productCartList.find { it.key == key }
                    val isSelected = existingProduct?.isSelected ?: productSnapshot.child("isSelected").getValue(Boolean::class.java) ?: false

                    val product = ProductModel(key, name, price, time, parcel,delivery_fee, parcel_day, count, count_sum, isSelected)
                    updatedList.add(product)
                }
                productCartList.clear()
                productCartList.addAll(updatedList)
                binding.productRecyclerview.adapter?.notifyDataSetChanged()
                updateTotalAmount()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun updateTotalAmount() {
        val selectedAmount = productCartList.filter { it.isSelected }.sumBy { it.count_sum }
        val selectedFee = productCartList.filter { it.isSelected }.sumBy { it.delivery_fee }
        val totalPayment = selectedAmount
        val totalDeliveryFee = selectedFee
        binding.totalPaymentAmount.text = "${NumberFormat.getNumberInstance(Locale.US).format(totalPayment)} 원"
        binding.totalDeliveryFee.text = "${NumberFormat.getNumberInstance(Locale.US).format(totalDeliveryFee)} 원"
    }
}