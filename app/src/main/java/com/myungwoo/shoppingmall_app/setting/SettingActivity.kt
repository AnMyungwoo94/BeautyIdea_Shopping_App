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
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.myungwoo.shoppingmall_app.Delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.Delivery.ProductInfo
import com.myungwoo.shoppingmall_app.auth.KakaoUserInfo
import com.myungwoo.shoppingmall_app.databinding.ActivitySettingBinding
import com.myungwoo.shoppingmall_app.utils.FBRef.Companion.deliveryRef


class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySettingBinding
    val user = FirebaseAuth.getInstance().currentUser
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var deliveryInfo: DeliveryInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {


            //파이어베이스 로그아웃
            val currentUser = auth.currentUser // Firebase 인증 객체에서 현재 사용자 정보를 가져옵니다.
            if (currentUser != null) {
                val providerId = currentUser.providerId // 로그인 제공자 ID를 가져옵니다.
                Log.e("providerId", providerId)
                when (providerId) {
                    "firebase" -> {
                        performFirebaseLogout() // Firebase 로그아웃 함수 호출
                    }
                    else -> {
                        // 다른 로그아웃 방법 만들기
                    }
                }
            } else {
                //카카오 로그아웃
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

        val kakaonickName = KakaoUserInfo.getKakaoNickName().toString()
        Log.e("kakaonickName", kakaonickName)
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
                "google.com" ->  binding.userEmail.text = currentUser.email.toString()
                "anonymous" -> {
                    binding.userEmail.text = "회원"
                }
                else -> {
                   //다른 로그인 시도시 추가하기
                }
            }
        } else {
            binding.userEmail.text = kakaonickName
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
                    val product_sum =
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

        Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

}