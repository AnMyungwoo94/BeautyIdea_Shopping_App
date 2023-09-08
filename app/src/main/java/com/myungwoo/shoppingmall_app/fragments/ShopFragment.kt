package com.myungwoo.shoppingmall_app.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.FragmentShopBinding
import com.myungwoo.shoppingmall_app.product.ImageSliderAdapter
import com.myungwoo.shoppingmall_app.product.ProductInput_Activity
import com.myungwoo.shoppingmall_app.product.ProductModel
import com.myungwoo.shoppingmall_app.product.ProductRvAdapter
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class ShopFragment : Fragment() {
    private lateinit var binding: FragmentShopBinding

    //뷰페이지2 이미지 슬라이드 연결
    private var sliderHandler = Handler()
    private lateinit var runnable: Runnable

    //상품 데이터 불러오기
    val itemKeyList = ArrayList<String>()
    lateinit var productrvAdapter: ProductRvAdapter
    val productList = mutableListOf<ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false)

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != "admin1@admin.com") {
            binding.floatingActionButton.visibility = View.GONE
        } else {
            binding.floatingActionButton.setOnClickListener {
                val intent = Intent(requireContext(), ProductInput_Activity::class.java)
                startActivity(intent)
            }
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_homeFragment2)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_tipFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_bookmarkFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_talkFragment)
        }

        //제품 리스트를 메인에 불러오고 싶을때 사용
        // 어댑터 초기화 및 데이터 설정
        val rv : RecyclerView = binding.mainRV
        productrvAdapter = ProductRvAdapter(requireContext(), productList)
        rv.layoutManager = GridLayoutManager(requireContext(),2)
        rv.adapter = productrvAdapter
        getCategoryData_Product()

        //이미지 슬라이드
        val images = listOf(R.drawable.shop_img, R.drawable.shop_img2, R.drawable.shop_img3)
        val sliderViewPager = binding.sliderViewPager
        sliderViewPager.offscreenPageLimit = 1
        sliderViewPager.adapter = ImageSliderAdapter(requireContext(), images)

        sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        setupIndicators(images.size)
        // 자동 슬라이드 시작
        autoSlideViewPager2()
        Log.d("ViewPager2", "Initial Page: ${sliderViewPager.currentItem}")


        return binding.root
    }
    //상품리스트에 있는 값들 가져오기
    private fun getCategoryData_Product() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    Log.e("ddddddddddddd",dataModel.toString() )
                    val item = dataModel.getValue(ProductModel::class.java)
                    itemKeyList.add(dataModel.key.toString())
                    productList.add(item!!)
                }
                productrvAdapter.notifyDataSetChanged() // 어댑터에 변경 사항 알림
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("제품 불러오기", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.productRef.addValueEventListener(postListener)
    }

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 8, 16, 8)
        }

        for (i in 0 until count) {
            indicators[i] = ImageView(requireContext()).apply {
                setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_indicator_inactive))
                layoutParams = params
                binding.layoutIndicators.addView(this)
            }
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount =  binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_indicator_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_indicator_inactive))
            }
        }
    }
    private fun autoSlideViewPager2() {
        val sliderViewPager = binding.sliderViewPager
        val totalItemCount = sliderViewPager.adapter?.itemCount ?: 0

        runnable = Runnable {
            val nextItem: Int = (sliderViewPager.currentItem + 1) % totalItemCount //순서대로 가게끔 하려고
            sliderViewPager.setCurrentItem(nextItem, true)
            sliderHandler.postDelayed(runnable, 3000L)
        }

        // 처음 시작할 때의 딜레이 설정.
        sliderHandler.postDelayed(runnable, 3000L)
    }

    override fun onResume() {
        super.onResume()
        autoSlideViewPager2()
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(runnable)
    }
}