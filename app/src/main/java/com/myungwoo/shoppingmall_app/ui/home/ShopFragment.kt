package com.myungwoo.shoppingmall_app.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.common.ProductRvAdapter
import com.myungwoo.shoppingmall_app.data.CouponModel
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.databinding.FragmentShopBinding
import com.myungwoo.shoppingmall_app.ui.product.ProductInputActivity
import com.myungwoo.shoppingmall_app.utils.FBRef

class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShopBinding
    private lateinit var runnable: Runnable
    private lateinit var productRvAdapter: ProductRvAdapter
    private lateinit var couponRvAdapter: CouponRvAdapter
    private var sliderHandler = Handler()
    private val itemKeyList = ArrayList<String>()
    private val productList = mutableListOf<ProductModel>()
    private val couponList = mutableListOf<CouponModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater)

        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != "admin1@admin1@") {
            binding.floatingActionButton.visibility = View.GONE
        } else {
            binding.floatingActionButton.setOnClickListener {
                val intent = Intent(requireContext(), ProductInputActivity::class.java)
                startActivity(intent)
            }
        }

        binding.categoryTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_categoryFragment)
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

        binding.sbSearch.setOnClickListener {
            it.findNavController().navigate(R.id.action_shopFragment_to_searchResultFragment)
        }

        val rvProduct: RecyclerView = binding.mainRVproduct
        productRvAdapter = ProductRvAdapter(requireContext(), productList)
        rvProduct.layoutManager = GridLayoutManager(requireContext(), 3)
        rvProduct.adapter = productRvAdapter
        getCategoryDataProduct()

        val rvCoupon: RecyclerView = binding.mainRVcoupon
        couponRvAdapter = CouponRvAdapter(requireContext(), couponList)
        rvCoupon.adapter = couponRvAdapter
        getCouponData()

        val images = listOf(R.drawable.shop_img, R.drawable.shop_img2, R.drawable.shop_img3)
        val sliderViewPager = binding.sliderViewPager
        sliderViewPager.offscreenPageLimit = 1
        sliderViewPager.adapter = ImageSliderAdapter(requireContext(), images)

        sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (isAdded) {
                    setCurrentIndicator(position)
                } else {
                    Log.e("setCurrentIndicator", "setCurrentIndicator 오류")
                }
            }
        })
        setupIndicators(images.size)
        autoSlideViewPager()

        return binding.root
    }

    private fun getCouponData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CouponModel::class.java)
                    couponList.add(item!!)
                }
                couponRvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("쿠폰 목록 불러오기", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.couponRef.addValueEventListener(postListener)
    }

    private fun getCategoryDataProduct() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                productList.clear()
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(ProductModel::class.java)
                    itemKeyList.add(dataModel.key.toString())
                    productList.add(item!!)
                }
                productRvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
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
        val childCount = binding.layoutIndicators.childCount
        val context = requireContext()

        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as? ImageView
            if (imageView != null) {
                if (i == position) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_indicator_active))
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_indicator_inactive))
                }
            }
        }
    }

    private fun autoSlideViewPager() {
        val sliderViewPager = binding.sliderViewPager
        val totalItemCount = sliderViewPager.adapter?.itemCount ?: 0

        runnable = Runnable {
            val nextItem: Int = (sliderViewPager.currentItem + 1) % totalItemCount
            sliderViewPager.setCurrentItem(nextItem, true)
            sliderHandler.postDelayed(runnable, 3000L)
        }

        sliderHandler.postDelayed(runnable, 3000L)
    }

    override fun onResume() {
        super.onResume()
        autoSlideViewPager()
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(runnable)
    }
}