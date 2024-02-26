package com.myungwoo.shoppingmall_app.product

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.myungwoo.shoppingmall_app.BuildConfig
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.delivery.ProductInfo
import com.myungwoo.shoppingmall_app.databinding.ActivityProductPayBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import java.io.Serializable
import java.text.NumberFormat
import kr.co.bootpay.android.Bootpay
import kr.co.bootpay.android.events.BootpayEventListener
import kr.co.bootpay.android.models.BootExtra
import kr.co.bootpay.android.models.BootItem
import kr.co.bootpay.android.models.BootUser
import kr.co.bootpay.android.models.Payload
import java.util.*

class ProductPayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductPayBinding
    private var productCartList = mutableListOf<ProductModel>()
    private var countSumCart: Serializable = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCard.setOnClickListener {
            inicisPayment()
        }

        val selectedProducts: List<ProductModel> =
            intent.getSerializableExtra("SELECTED_PRODUCTS") as? ArrayList<ProductModel> ?: listOf()

        //제품 상세페이지에서 구매하기 버튼을 눌렸을 경우
        val selectedProduct = intent.getSerializableExtra("SELECTED_PRODUCT_PAY") as? ProductModel
        val count = intent.getSerializableExtra("COUNT")

        intent.getSerializableExtra("COUNT_SUM")?.let {
            countSumCart = intent.getSerializableExtra("COUNT_SUM")!!
        }

        if (selectedProduct != null) {
            binding.productInfoLayout.visibility = View.VISIBLE
            binding.productName.text = selectedProduct.name
            binding.productPrice.text =
                "${NumberFormat.getNumberInstance(Locale.US).format(countSumCart)} "
            binding.quantityTextView.text = count.toString()
            val deliveryFee = selectedProduct.deliveryFee
            if (deliveryFee != 0) {
                binding.productDeliveryFee.text =
                    "배송비 ${NumberFormat.getNumberInstance(Locale.US).format(deliveryFee)} 원"
            } else {
                binding.productDeliveryFee.text = "배송비 무료"
            }

            val storageRef: StorageReference = Firebase.storage.reference
            selectedProduct.let { product ->
                val productKey = product.key
                val pictureRef = storageRef.child("$productKey.png")

                pictureRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(binding.productImage)
                }.addOnFailureListener { exception ->
                    Log.e(
                        "ProductPay_Activity",
                        "Failed to download image for product $productKey",
                        exception
                    )
                }
            }

            // selectedProducts에서 각 제품의 이미지 가져오기
            for (product in selectedProducts) {
                val productKey = product.key
                val pictureRef = storageRef.child("$productKey.png")

                pictureRef.downloadUrl.addOnSuccessListener { uri ->
                }.addOnFailureListener { exception ->
                    Log.e(
                        "ProductPay_Activity",
                        "Failed to download image for product $productKey",
                        exception
                    )
                }
            }
        } else {
            binding.productInfoLayout.visibility = View.GONE
        }

        binding.productListRV.layoutManager = LinearLayoutManager(this)

        val adapter = ProductCartRvAdapter(this, productCartList) {
            updateTotalPaymentAmount()
        }
        binding.productListRV.adapter = adapter

        when (val origin = intent.getStringExtra("origin")) {
            "CART" -> loadSelectedProductsFromFirebase()
            "DETAILS" -> loadProductsFromFirebase()
            else -> {
                Log.e("ProductPay_Activity", "Unknown origin: $origin")
            }
        }
        updateTotalPaymentAmount()

        binding.plusBtn.setOnClickListener {
            var currentQuantity = binding.quantityTextView.text.toString().toInt()
            currentQuantity++
            binding.quantityTextView.text = currentQuantity.toString()
            val singleProductPrice = selectedProduct!!.price.replace(",", "").trim().toInt()
            val totalProductPrice = singleProductPrice * currentQuantity
            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(totalProductPrice)
            binding.productPrice.text = formattedPrice
            updateTotalPaymentAmount()
        }

        binding.minusBtn.setOnClickListener {
            var currentQuantity = binding.quantityTextView.text.toString().toInt()
            if (currentQuantity > 1) {
                currentQuantity--
                binding.quantityTextView.text = currentQuantity.toString()
                val singleProductPrice = selectedProduct!!.price.replace(",", "").trim().toInt()
                val totalProductPrice = singleProductPrice * currentQuantity
                val formattedPrice =
                    NumberFormat.getNumberInstance(Locale.US).format(totalProductPrice)
                binding.productPrice.text = formattedPrice
            }
            updateTotalPaymentAmount()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.payButton.setOnClickListener {
            val name = binding.editUserName.text.toString()
            val phoneNumber = binding.editPhone.text.toString()
            val address = binding.editAddress.text.toString()
            val memo = binding.deliveryMemo.text.toString()
            val productSum = binding.totalPaymentAmount.text.toString()
            val time = FBAuth.getTime()

            if (name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, R.string.product_pay_address, Toast.LENGTH_SHORT).show()
            } else if (!binding.btnCheck1.isChecked || !binding.btnCheck2.isChecked) {
                Toast.makeText(this, R.string.product_pay_pay_check, Toast.LENGTH_SHORT).show()
            } else {
                var selectedProductsList = mutableListOf<ProductInfo>()
                if (selectedProduct != null) {
                    selectedProductsList.add(
                        ProductInfo(
                            selectedProduct.key,
                            selectedProduct.name,
                            selectedProduct.count,
                            selectedProduct.deliveryFee,
                            (selectedProduct.price.replace(",", "").trim()
                                .toInt()) * binding.quantityTextView.text.toString().toInt(),
                            "배송중",
                            time
                        )
                    )
                }

                for (product in productCartList) {
                    if (product.isSelected) {
                        val totalPaymentAmount =
                            (product.price.replace(",", "").trim().toInt()) * product.count

                        selectedProductsList.add(
                            ProductInfo(
                                key = product.key,
                                name = product.name,
                                count = product.count,
                                deliveryFee = product.deliveryFee,
                                totalPaymentAmount = totalPaymentAmount,
                                deliveryStatus = "배송중",
                                time = time,
                            )
                        )
                        Log.e("selectedProductsList", selectedProductsList.toString())
                    }
                }

                val database = FirebaseDatabase.getInstance().reference
                val uid = FirebaseAuth.getInstance().uid
                val deliveryInfo = DeliveryInfo(
                    name = name,
                    phoneNumber = phoneNumber,
                    address = address,
                    memo = memo,
                    productSum = productSum,
                    products = selectedProductsList
                )

                database.child("orders").child(uid!!).push().setValue(deliveryInfo)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.product_pay_order_success, Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, R.string.product_pay_order_fail, Toast.LENGTH_SHORT)
                            .show()
                    }
                startActivity(Intent(this, ProductSuccessActivity::class.java))
            }
        }
    }

    private fun inicisPayment() {
        val appId = BuildConfig.inicis
        val user = BootUser().setPhone("010-1234-5678")
        val extra = BootExtra()
            .setCardQuota("0,2,3")

        val price = 1000.0
        val pg = "나이스페이"
        val method = "카드"

        val items: MutableList<BootItem> = ArrayList()
        val item1 = BootItem().setName("마우스").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(500.0)
        val item2 = BootItem().setName("키보드").setId("ITEM_KEYBOARD_MOUSE").setQty(1).setPrice(500.0)
        items.add(item1)
        items.add(item2)

        val payload = Payload()
        payload.setApplicationId(appId)
            .setOrderName("부트페이 결제 테스트")
            .setPg(pg)
            .setOrderId("1234")
            .setMethod(method)
            .setPrice(price)
            .setUser(user)
            .setExtra(extra).items = items

        val map: MutableMap<String, Any> = HashMap()
        map["1"] = "abcdef"
        map["2"] = "abcdef55"
        map["3"] = 1234
        payload.metadata = map

        Bootpay.init(supportFragmentManager, applicationContext)
            .setPayload(payload)
            .setEventListener(object : BootpayEventListener {
                override fun onCancel(data: String) {
                    Log.d("bootpay", "cancel: $data")
                }

                override fun onError(data: String) {
                    Log.d("bootpay", "error: $data")
                }

                override fun onClose() {
                    Log.d("bootpay", "close")
                    Bootpay.removePaymentWindow()
                }


                override fun onIssued(data: String) {
                    Log.d("bootpay", "issued: $data")
                }

                override fun onConfirm(data: String): Boolean {
                    Log.d("bootpay", "confirm: $data")
                    return true
                }

                override fun onDone(data: String) {
                    Log.d("done", data)
                }
            }).requestPayment()
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
                        count,
                        countSum,
                        isSelected
                    )
                    updatedList.add(product)
                }
                productCartList.clear()
                productCartList.addAll(updatedList)
                if (productCartList.isEmpty()) {
                    binding.cartItems.visibility = View.GONE
                    binding.productListRV.visibility = View.GONE
                } else {
                    binding.cartItems.visibility = View.VISIBLE
                    binding.productListRV.visibility = View.VISIBLE
                }
                binding.productListRV.adapter?.notifyDataSetChanged()
                updateTotalPaymentAmount()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun loadSelectedProductsFromFirebase() {
        val selectedProducts: List<ProductModel> =
            intent.getSerializableExtra("SELECTED_PRODUCTS") as? ArrayList<ProductModel> ?: listOf()
        Log.e("selectedProducts", selectedProducts.toString())

        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("cart/$userUID")

        val query = ref
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedList = mutableListOf<ProductModel>()
                for (productSnapshot in dataSnapshot.children) {
                    val key = productSnapshot.child("key").getValue(String::class.java) ?: ""
                    val name = productSnapshot.child("name").getValue(String::class.java) ?: ""
                    val price = productSnapshot.child("price").getValue(String::class.java) ?: ""
                    val time = productSnapshot.child("time").getValue(String::class.java) ?: ""
                    val parcel = productSnapshot.child("parcel").getValue(String::class.java) ?: ""
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
                        count,
                        countSum,
                        isSelected
                    )
                    updatedList.add(product)
                }
                productCartList.clear()
                productCartList.addAll(selectedProducts)
                if (productCartList.isEmpty()) {
                    binding.cartItems.visibility = View.GONE
                    binding.productListRV.visibility = View.GONE
                } else {
                    binding.cartItems.visibility = View.GONE
                    binding.productListRV.visibility = View.VISIBLE
                }
                binding.productListRV.adapter?.notifyDataSetChanged()
                updateTotalPaymentAmount()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun updateTotalPaymentAmount() {
        val productPriceText = binding.productPrice.text.toString().replace(",", "").trim()
        val selectedProductPrice = if (productPriceText.isDigitsOnly()) {
            productPriceText.toIntOrNull()
        } else {
            0
        }

        val cartTotal = productCartList.filter { it.isSelected }.sumBy {
            val priceWithoutComma = it.price.replace(",", "").trim().toInt()
            priceWithoutComma * it.count
        }

        val totalAmount = selectedProductPrice?.plus(cartTotal)
        if (totalAmount != null) {
            binding.totalPaymentAmount.text = "${NumberFormat.getNumberInstance(Locale.US).format(totalAmount)} 원"
        }
        val productFeeText =
            binding.productDeliveryFee.text.toString().replace("[^0-9]".toRegex(), "").toIntOrNull()
                ?: 0
        val selectedFee = productCartList.filter { it.isSelected }.sumBy { it.deliveryFee }
        val totalDeliveryFee = selectedFee + productFeeText
        binding.totalDeliveryFee.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(totalDeliveryFee)} 원"
    }
}