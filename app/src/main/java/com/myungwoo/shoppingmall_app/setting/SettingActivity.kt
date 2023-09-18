package com.myungwoo.shoppingmall_app.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.auth.IntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.myungwoo.shoppingmall_app.Delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.Delivery.ProductInfo
import com.myungwoo.shoppingmall_app.databinding.ActivitySettingBinding
import com.myungwoo.shoppingmall_app.utils.FBRef.Companion.deliveryRef


class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivitySettingBinding
    val user = FirebaseAuth.getInstance().currentUser
    private lateinit var orderAdapter : OrderAdapter
    private lateinit var deliveryInfo : DeliveryInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val currentUser = auth.currentUser // Firebase 인증 객체에서 현재 사용자 정보를 가져옵니다.

            if (currentUser != null) {
                val providerId = currentUser.providerId // 로그인 제공자 ID를 가져옵니다.

                when (providerId) {
                    "kakao.com" -> {
                        // 카카오 로그아웃
                        UserApiClient.instance.logout { error: Throwable? ->
                            if (error != null) {
                                // 카카오 로그아웃 중 오류 발생
                                Toast.makeText(this, "카카오톡 로그아웃 실패", Toast.LENGTH_SHORT).show()
                            } else {
                                // 카카오 로그아웃 성공
                                Toast.makeText(this, "카카오톡 로그아웃 성공", Toast.LENGTH_SHORT).show()
                                performFirebaseLogout() // Firebase 로그아웃 함수 호출
                            }
                        }
                    }
                    "firebase" -> {
                        performFirebaseLogout() // Firebase 로그아웃 함수 호출
                    }
                    else -> {
                        // 다른 로그인 방법에 대한 처리 (예: 구글, 페이스북 등)
                        // 원하는 방식으로 처리하거나 필요한 경우 추가적인 분기를 수행하세요.
                    }
                }
            } else {
                // 현재 사용자가 로그인하지 않은 경우
                Toast.makeText(this, "로그인하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.userEmail.text = user!!.email.toString()

        binding.backButton.setOnClickListener {
            finish()
        }

        deliveryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val deliveryList = mutableListOf<DeliveryInfo>()

                for (deliverySnapshot in dataSnapshot.children) {
                    val name = deliverySnapshot.child("name").getValue(String::class.java) ?: ""
                    val phoneNumber = deliverySnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val address = deliverySnapshot.child("address").getValue(String::class.java) ?: ""
                    val product_sum = deliverySnapshot.child("product_sum").getValue(String::class.java) ?: ""

                    val productsList = mutableListOf<ProductInfo>()
                    val productsSnapshot = deliverySnapshot.child("products")
                    for (productSnapshot in productsSnapshot.children) {
                        val product = productSnapshot.getValue(ProductInfo::class.java)
                        product?.let { productsList.add(it) }
                    }

                    val deliveryInfo = DeliveryInfo(
                        name = name,
                        phoneNumber = phoneNumber,
                        address = address,
                        product_sum = product_sum,
                        products = productsList
                    )
                    deliveryList.add(deliveryInfo)
                }

                orderAdapter = OrderAdapter(deliveryList, this@SettingActivity)

                binding.mypageRecyclerview.layoutManager = LinearLayoutManager(this@SettingActivity)
                binding.mypageRecyclerview.adapter = orderAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기 실패. 에러 로그 출력
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }
        })

    }  // Firebase 로그아웃을 수행하는 함수
    private fun performFirebaseLogout() {
        auth.signOut() // Firebase 로그아웃

        Toast.makeText(this, "이메일로 로그아웃", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}