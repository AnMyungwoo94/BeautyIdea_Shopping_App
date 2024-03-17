package com.myungwoo.shoppingmall_app.ui.category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.utils.ShopCategory
import com.myungwoo.shoppingmall_app.databinding.FragmentShopCategoryBinding
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.common.ProductRvAdapter

class ShopCategoryFragment : Fragment() {

    private lateinit var binding: FragmentShopCategoryBinding
    private lateinit var category: String
    private lateinit var productRvAdapter: ProductRvAdapter
    private val categoryList = mutableListOf<ProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryResId = arguments?.getInt(CATEGORY_RES_ID)
        if (categoryResId != null) {
            category = getString(categoryResId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopCategoryBinding.inflate(inflater)

        val rvProduct: RecyclerView = binding.rvCategoryList
        productRvAdapter = ProductRvAdapter(requireContext(), categoryList)
        rvProduct.layoutManager = GridLayoutManager(requireContext(), 3)
        rvProduct.adapter = productRvAdapter

        val dbRef = FirebaseDatabase.getInstance().getReference("product")
        dbRef.orderByChild("category").equalTo(category).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (categorySnapshot in snapshot.children) {
                    val product = categorySnapshot.getValue(ProductModel::class.java)
                    product?.let { categoryList.add(it) }
                }
                productRvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("카테고리 오류", error.toString())
            }
        })

        return binding.root
    }

    companion object {
        const val CATEGORY_RES_ID = "category_res_id"

        fun create(category: ShopCategory) = ShopCategoryFragment().apply {
            arguments = Bundle().apply {
                putInt(CATEGORY_RES_ID, category.resId)
            }
        }
    }
}