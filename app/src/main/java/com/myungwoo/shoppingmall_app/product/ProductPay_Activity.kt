package com.myungwoo.shoppingmall_app.product

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.myungwoo.shoppingmall_app.databinding.ActivityProductPayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*

class ProductPay_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityProductPayBinding
    private var productCartList = mutableListOf<ProductModel>()
    private var key: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //장바구니에서 결제하기 버튼을 눌렀을 경우
        val selectedProducts: List<ProductModel> =
            intent.getSerializableExtra("SELECTED_PRODUCTS") as? ArrayList<ProductModel> ?: listOf()
        Log.e("selectedProducts", selectedProducts.toString())

        // 선택된 제품들이 없으면 텍스트와 productInfoLayout을 숨깁니다.
        if (selectedProducts.isEmpty()) {
            binding.purchaseInfo.visibility = View.GONE
            binding.productInfoLayout.visibility = View.GONE
        } else {
            binding.cartItems.visibility = View.GONE
            productCartList.clear()
            productCartList.addAll(selectedProducts)
            binding.productListRV.adapter?.notifyDataSetChanged()
        }


        //제품 상세페이지에서 구매하기 버튼을 눌렸을 경우
        val selecteddProduct = intent.getSerializableExtra("SELECTED_PRODUCT_PAY") as? ProductModel
        val count = intent.getSerializableExtra("COUNT")
        val count_sum = intent.getSerializableExtra("COUNT_SUM")

        // 데이터 확인
        if (selecteddProduct != null) {
            binding.productInfoLayout.visibility = View.VISIBLE  // 뷰를 보이게 설정

            binding.productName.text = selecteddProduct.name
            binding.productPrice.text =
                "${NumberFormat.getNumberInstance(Locale.US).format(count_sum)} "
            binding.quantityTextView.text = count.toString()
            var deliveryFee = selecteddProduct.delivery_fee
            if (deliveryFee != 0) {
                binding.productDeliveryFee.text =
                    "배송비 ${NumberFormat.getNumberInstance(Locale.US).format(deliveryFee)} 원"
            } else {
                binding.productDeliveryFee.text = "배송비 무료"
            }

            val storageRef: StorageReference = Firebase.storage.reference

            // selecteddProduct에서 이미지 가져오기
            selecteddProduct?.let { product ->
                val productKey = product.key
                val pictureRef = storageRef.child("$productKey.png")

                pictureRef.downloadUrl.addOnSuccessListener { uri ->
                    // 성공적으로 다운로드된 이미지 URI를 사용하여 작업 수행
                    Glide.with(this).load(uri).into(binding.productImage)
                }.addOnFailureListener { exception ->
                    // 이미지 다운로드 실패 시 처리할 내용 작성
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
                    // 성공적으로 다운로드된 이미지 URI를 사용하여 작업 수행
                }.addOnFailureListener { exception ->
                    // 이미지 다운로드 실패 시 처리할 내용 작성
                    Log.e(
                        "ProductPay_Activity",
                        "Failed to download image for product $productKey",
                        exception
                    )
                }
            }
        } else {
            binding.productInfoLayout.visibility = View.GONE  // 뷰를 숨김
        }


        binding.productListRV.layoutManager = LinearLayoutManager(this)

        // 어댑터 초기화 및 체크박스 상태 변경 콜백 설정
        val adapter = ProductCartRvAdapter(this, productCartList) {
            updateTotalPaymentAmount()  // 이 부분이 onItemChanged 콜백의 구현입니다.
        }
        binding.productListRV.adapter = adapter

        val origin = intent.getStringExtra("origin")
        when (origin) {
            "CART" -> loadSelectedProductsFromFirebase() // 선택된 제품들만
            "DETAILS" -> loadProductsFromFirebase() //전체
            else -> {
                Log.e("ProductPay_Activity", "Unknown origin: $origin")
            }
        }
        updateTotalPaymentAmount()

        binding.plusBtn.setOnClickListener {
            var currentQuantity = binding.quantityTextView.text.toString().toInt()
            currentQuantity++
            binding.quantityTextView.text = currentQuantity.toString()
            // 상품의 단가 가져오기
            val singleProductPrice = selecteddProduct!!.price.replace(",", "").trim().toInt()
            // 총 가격 계산: 단가 * 수량
            val totalProductPrice = singleProductPrice * currentQuantity
            // 가격 포맷팅 후 표시
            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(totalProductPrice)
            binding.productPrice.text = formattedPrice
            updateTotalPaymentAmount()
        }

        binding.minusBtn.setOnClickListener {
            var currentQuantity = binding.quantityTextView.text.toString().toInt()
            if (currentQuantity > 1) {
                currentQuantity--
                binding.quantityTextView.text = currentQuantity.toString()
                // 상품의 단가 가져오기
                val singleProductPrice = selecteddProduct!!.price.replace(",", "").trim().toInt()
                // 총 가격 계산: 단가 * 수량
                val totalProductPrice = singleProductPrice * currentQuantity
                // 가격 포맷팅 후 표시
                val formattedPrice =
                    NumberFormat.getNumberInstance(Locale.US).format(totalProductPrice)
                binding.productPrice.text = formattedPrice
            }
            updateTotalPaymentAmount()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    //전체 상품을 가져오는 함수
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
                    val parcel_day =
                        productSnapshot.child("parcel_day").getValue(String::class.java) ?: ""
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
                    val isSelected =
                        existingProduct?.isSelected ?: productSnapshot.child("isSelected")
                            .getValue(Boolean::class.java) ?: false

                    val product = ProductModel(
                        key,
                        name,
                        price,
                        time,
                        parcel,
                        delivery_fee,
                        parcel_day,
                        count,
                        count_sum,
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
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("cart/$userUID")

        // 선택된 제품만 필터링하는 쿼리
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
                    val delivery_feeObj = productSnapshot.child("delivery_fee").getValue()
                    val delivery_fee = when (delivery_feeObj) {
                        is Long -> delivery_feeObj.toInt()
                        is String -> delivery_feeObj.toIntOrNull() ?: 0
                        else -> 0
                    }
                    val parcel_day =
                        productSnapshot.child("parcel_day").getValue(String::class.java) ?: ""
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
                    val isSelected =
                        existingProduct?.isSelected ?: productSnapshot.child("isSelected")
                            .getValue(Boolean::class.java) ?: false

                    val product = ProductModel(
                        key,
                        name,
                        price,
                        time,
                        parcel,
                        delivery_fee,
                        parcel_day,
                        count,
                        count_sum,
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

    private fun updateTotalAmount() {
        val selectedAmount = productCartList.filter { it.isSelected }.sumBy { it.count_sum }
        val selectedFee = productCartList.filter { it.isSelected }.sumBy { it.delivery_fee }
        val totalPayment = selectedAmount
        val totalDeliveryFee = selectedFee
        binding.totalPaymentAmount.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(totalPayment)} 원"
        binding.totalDeliveryFee.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(totalDeliveryFee)} 원"
    }

    private fun updateTotalPaymentAmount() {
        // 선택된 제품의 가격 가져오기
        val productPriceText = binding.productPrice.text.toString().replace(",", "").trim()
        val selectedProductPrice = if (productPriceText.isDigitsOnly()) {
            productPriceText.toInt()
        } else {
            0
        }

        // 체크된 상품들의 가격 합산
        val cartTotal = productCartList.filter { it.isSelected }.sumBy {
            val priceWithoutComma = it.price.replace(",", "").trim().toInt()
            priceWithoutComma * it.count
        }

        val totalAmount = selectedProductPrice + cartTotal

        binding.totalPaymentAmount.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(totalAmount)} 원"


        //배송비 가격합산
        val productFeeText =
            binding.productDeliveryFee.text.toString().replace("[^0-9]".toRegex(), "").toIntOrNull()
                ?: 0
        val selectedFee = productCartList.filter { it.isSelected }.sumBy { it.delivery_fee }
        val totalDeliveryFee = selectedFee + productFeeText
        Log.e("totalDeliveryFee", totalDeliveryFee.toString())
        binding.totalDeliveryFee.text =
            "${NumberFormat.getNumberInstance(Locale.US).format(totalDeliveryFee)} 원"


    }

}