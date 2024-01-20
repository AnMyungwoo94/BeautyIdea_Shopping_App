package com.myungwoo.shoppingmall_app.setting

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
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
import com.kakao.sdk.user.UserApiClient
import com.myungwoo.shoppingmall_app.delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.delivery.ProductInfo
import com.myungwoo.shoppingmall_app.auth.KakaoUserInfo
import com.myungwoo.shoppingmall_app.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySettingBinding
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            val currentUser = auth.currentUser
            if (currentUser != null) {
                val providerId = currentUser.providerId
                Log.e("providerId", providerId)
                when (providerId) {
                    "firebase" -> {
                        performFirebaseLogout()
                    }

                    else -> {
                        // 다른 로그아웃 방법 만들기
                    }
                }
            } else {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, IntroActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
        }

        val kakaoName = KakaoUserInfo.getKakaoNickName().toString()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val providerData = currentUser.providerData
            val providerId = if (providerData.size > 1) providerData[1]?.providerId else providerData[0]?.providerId
            when (providerId) {
                "password" -> {
                    user?.email?.let { email ->
                        binding.userEmail.text = email
                    } ?: run {
                        binding.userEmail.text = "회원"
                    }
                }

                "google.com" -> binding.userEmail.text = currentUser.email.toString()
                "anonymous" -> {
                    binding.userEmail.text = "회원"
                }

                else -> {
                    //다른 로그인 시도시 추가하기
                }
            }
        } else {
            binding.userEmail.text = kakaoName
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("orders/$userUID")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val deliveryList = mutableListOf<DeliveryInfo>()

                for (deliverySnapshot in dataSnapshot.children) {
                    val name = deliverySnapshot.child("name").getValue(String::class.java) ?: ""
                    val phoneNumber =
                        deliverySnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val address =
                        deliverySnapshot.child("address").getValue(String::class.java) ?: ""
                    val productSum =
                        deliverySnapshot.child("product_sum").getValue(String::class.java) ?: ""

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
                        productSum = productSum,
                        products = productsList
                    )
                    deliveryList.add(deliveryInfo)
                }

                orderAdapter = OrderAdapter(deliveryList, this@SettingActivity)

                binding.mypageRecyclerview.layoutManager = LinearLayoutManager(this@SettingActivity)
                binding.mypageRecyclerview.adapter = orderAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }
        })

    }

    private fun performFirebaseLogout() {
        auth.signOut() // Firebase 로그아웃
        Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}