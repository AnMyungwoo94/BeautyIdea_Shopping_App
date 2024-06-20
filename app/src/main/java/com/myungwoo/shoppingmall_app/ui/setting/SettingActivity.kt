package com.myungwoo.shoppingmall_app.ui.setting

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import com.myungwoo.data.repository.UserRepository
import com.myungwoo.model.DeliveryInfo
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.ActivitySettingBinding
import com.myungwoo.shoppingmall_app.ui.MainActivity
import com.myungwoo.shoppingmall_app.ui.auth.IntroActivity
import com.myungwoo.shoppingmall_app.ui.auth.kakao.KakaoUserInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySettingBinding
    private lateinit var orderAdapter: OrderAdapter
    private val user = FirebaseAuth.getInstance().currentUser

    @Inject
    lateinit var usersRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {

            val currentUser = auth.currentUser
            if (currentUser != null) {
                val providerId = currentUser.providerId
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
                        Toast.makeText(this, R.string.setting_logout_fail, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, R.string.setting_logout_success, Toast.LENGTH_SHORT)
                            .show()
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
            val providerId =
                if (providerData.size > 1) providerData[1]?.providerId else providerData[0]?.providerId
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

                    val productsList = mutableListOf<com.myungwoo.model.ProductInfo>()
                    val productsSnapshot = deliverySnapshot.child("products")
                    for (productSnapshot in productsSnapshot.children) {
                        val product =
                            productSnapshot.getValue(com.myungwoo.model.ProductInfo::class.java)
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
        auth.signOut()
        lifecycleScope.launch {
            usersRepository.clearUserLogin()
        }
        Toast.makeText(this, R.string.setting_logout_success, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}

