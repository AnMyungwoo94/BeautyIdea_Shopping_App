package com.myungwoo.shoppingmall_app.product

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.myungwoo.shoppingmall_app.Delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.Delivery.ProductInfo
import com.myungwoo.shoppingmall_app.databinding.ActivityProductPayBinding
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import java.io.Serializable
import java.text.NumberFormat
import java.util.*

class ProductPay_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityProductPayBinding
    private var productCartList = mutableListOf<ProductModel>()
    private var key: String = ""
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var count_sum_cart: Serializable = ""
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
        count_sum_cart = intent.getSerializableExtra("COUNT_SUM")!!
        Log.e("count_sum_cart", count_sum_cart.toString())

        // 데이터 확인
        if (selecteddProduct != null) {
            binding.productInfoLayout.visibility = View.VISIBLE  // 뷰를 보이게 설정

            binding.productName.text = selecteddProduct.name
            binding.productPrice.text =
                "${NumberFormat.getNumberInstance(Locale.US).format(count_sum_cart)} "
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

        binding.payButton.setOnClickListener {
            // 배송 정보 입력값 가져오기
            val name = binding.nameEditText.text.toString()
            val phoneNumber = binding.phoneEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val product_sum = binding.totalPaymentAmount.text.toString()
            val time = FBAuth.getTime()


            // 선택한 제품들의 정보 저장할 리스트 생성
            var selectedProductsList = mutableListOf<ProductInfo>()

            // 선택한 단일 상품 정보 생성 및 리스트에 추가
            if (selecteddProduct != null) {
                selectedProductsList.add(
                    ProductInfo(
                        selecteddProduct.key,selecteddProduct.name, selecteddProduct.count,
                        selecteddProduct.delivery_fee,
                        (selecteddProduct.price.replace(",", "").trim()
                            .toInt()) * binding.quantityTextView.text.toString().toInt(), "배송중", time
                    )
                )
            }

            for (product in productCartList) {
                if (product.isSelected) {
                    val totalPaymentAmount = (product.price.replace(",", "").trim().toInt()) * product.count

                    selectedProductsList.add(
                        ProductInfo(
                            key = product.key,
                            name = product.name,
                            count = product.count, // 각 상품의 수량 정보를 반영
                            deliveryFee = product.delivery_fee,
                            totalPaymentAmount = totalPaymentAmount,
                            delivery_status = "배송중",
                             time = time,
                        )
                    )
                    Log.e("selectedProductsList", selectedProductsList.toString())
                }

            }

            // Firebase Database 참조 가져오기
            val database = FirebaseDatabase.getInstance().reference

            // 사용자 UID - 실제로는 Firebase Authentication에서 가져올 수 있습니다.
            val uid = FirebaseAuth.getInstance().uid  // Firebase Auth를 사용하여 실제 UID를 가져오도록 수정하세요

            // 배송 정보와 상품 정보를 하나의 객체로 묶어서 Database에 저장
            val deliveryInfo = DeliveryInfo(
                name = name,
                phoneNumber = phoneNumber,
                address = address,
                product_sum = product_sum,

                products = selectedProductsList
            )

            // Database에 주문 정보 저장
            database.child("orders").child(uid!!).push().setValue(deliveryInfo)
                .addOnSuccessListener {
                    // 주문 정보가 성공적으로 저장되었을 때 실행할 코드
                    Toast.makeText(this, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // 주문 정보 저장 실패 시 실행할 코드
                    Toast.makeText(this, "주문이 실패되었습니다. ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            startActivity(Intent(this, ProductSuccess_Activity::class.java))
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

                    // 기존 리스트에서 상품 찾기8
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