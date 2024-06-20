package com.myungwoo.shoppingmall_app.ui.cart

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.myungwoo.model.ProductModel
import com.myungwoo.shoppingmall_app.databinding.ActivityProductCartBinding
import com.myungwoo.shoppingmall_app.ui.product.ProductPayActivity
import java.text.NumberFormat
import java.util.Locale

class ProductCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductCartBinding
    private var productCartList = mutableListOf<ProductModel>()
    private val adapter = ProductCartRvAdapter(this, productCartList) {
        updateTotalAmount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProductsFromFirebase()
        binding.productRecyclerview.adapter = adapter
        binding.productRecyclerview.layoutManager = LinearLayoutManager(this)

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.payBtn.setOnClickListener {
            val intent = Intent(this, ProductPayActivity::class.java)
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
                    val category =
                        productSnapshot.child("category").getValue(String::class.java) ?: ""
                    val deliveryFeeObj = productSnapshot.child("delivery_fee").value
                    val deliveryFee = when (deliveryFeeObj) {
                        is Long -> deliveryFeeObj.toInt()
                        is String -> deliveryFeeObj.toIntOrNull() ?: 0
                        else -> 0
                    }
                    val parcelDay =
                        productSnapshot.child("parcel_day").getValue(String::class.java) ?: ""
                    val countObj = productSnapshot.child("count").value
                    val count = when (countObj) {
                        is Long -> countObj.toInt()
                        is String -> countObj.toIntOrNull() ?: 0
                        else -> 0
                    }
                    val countSumObj = productSnapshot.child("count_sum").value
                    val countSum = when (countSumObj) {
                        is Long -> countSumObj.toInt()
                        is String -> countSumObj.toIntOrNull() ?: 1
                        else -> 1
                    }
                    val existingProduct = productCartList.find { it.key == key }
                    val isSelected =
                        existingProduct?.isSelected ?: productSnapshot.child("isSelected")
                            .getValue(Boolean::class.java) ?: false
                    val product = ProductModel(
                        key,
                        name,
                        price,
                        time,
                        parcel,
                        deliveryFee,
                        parcelDay,
                        category,
                        count,
                        countSum,
                        isSelected
                    )
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
        val selectedFee = productCartList.filter { it.isSelected }.sumBy { it.deliveryFee }
        binding.totalPaymentAmount.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(selectedAmount)} 원"
        binding.totalDeliveryFee.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(selectedFee)} 원"
    }
}