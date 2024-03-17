package com.myungwoo.shoppingmall_app.ui.product

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
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.ui.cart.ProductCartActivity
import java.text.NumberFormat
import java.util.*

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var count: Int = 1
    private var countSum: Int = 0
    private var productPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.detailText.setMovementMethod(ScrollingMovementMethod())

        val receivedData = intent.getSerializableExtra("ITEM_DATA") as ProductModel

        binding.productName.text = receivedData.name
        binding.productPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(receivedData.price.toInt())} "
        binding.productParcel.text = receivedData.parcel
        var deliveryFee = receivedData.deliveryFee
        if (deliveryFee != 0) {
            binding.deliveryFee.text = "배송비 ${NumberFormat.getNumberInstance(Locale.US).format(deliveryFee)} 원"
        } else {
            binding.deliveryFee.text = "배송비 무료"
        }
        binding.productParcelDay.text = receivedData.parcelDay
        binding.selectedProductName.text = receivedData.name
        binding.selectedProductPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(receivedData.price.toInt())} "
        productPrice = receivedData.price.toInt()

        val pictureRef = Firebase.storage.reference.child("${receivedData.key}.png")
        pictureRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(this).load(it.result).into(binding.productImage)
                Glide.with(this).load(it.result).into(binding.selectedProductImage)
            }
        }
        binding.btnExit.setOnClickListener {
            finish()
        }
        binding.plusBtn.setOnClickListener {
            count += 1
            updateQuantityAndTotalPrice()
        }

        binding.minusBtn.setOnClickListener {
            if (count > 1) {
                count -= 1
                updateQuantityAndTotalPrice()
            }
        }
        binding.btnBasket.setOnClickListener {
            val database = FirebaseDatabase.getInstance().reference

            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val uid = it.uid
                val key = receivedData.key
                val updatedProduct = receivedData.copy(count = count, count_sum = countSum)

                database.child("cart").child(uid).child(key).setValue(updatedProduct)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.product_detail_cart_success, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ProductCartActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, R.string.product_detail_cart_fail, Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.btnPay.setOnClickListener {
            val intent = Intent(this, ProductPayActivity::class.java)
            intent.putExtra("SELECTED_PRODUCT_PAY", receivedData)
            intent.putExtra("COUNT", count)
            intent.putExtra("COUNT_SUM", countSum)
            intent.putExtra("origin", "DETAILS")
            startActivity(intent)
        }
        updateQuantityAndTotalPrice()
    }

    private fun updateQuantityAndTotalPrice() {
        findViewById<TextView>(R.id.quantity_text_view).text = count.toString()
        val totalPrice = count * productPrice
        binding.sumText.text = "${NumberFormat.getNumberInstance(Locale.US).format(totalPrice)} "
        countSum = totalPrice
    }
}