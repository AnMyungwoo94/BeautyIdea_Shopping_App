package com.myungwoo.shoppingmall_app.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.common.ProductRvAdapter
import com.myungwoo.shoppingmall_app.data.ProductModel
import com.myungwoo.shoppingmall_app.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultBinding
    private lateinit var productRvAdapter: ProductRvAdapter
    private val productList = mutableListOf<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultBinding.inflate(inflater)


        binding.ivArrowBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_searchResultFragment_to_shopFragment)
        }

        val rvProduct: RecyclerView = binding.rvProductList
        productRvAdapter = ProductRvAdapter(requireContext(), productList)
        rvProduct.layoutManager = GridLayoutManager(requireContext(), 3)
        rvProduct.adapter = productRvAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    searchInRealtimeDatabase(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    fun searchInRealtimeDatabase(searchText: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("product")
        val query = databaseReference.orderByChild("name").startAt(searchText).endAt("$searchText\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (postSnapshot in snapshot.children) {
                    val data = postSnapshot.getValue(ProductModel::class.java)
                    data?.let {
                        productList.add(it)
                    }
                }
                productRvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("DatabaseQuery", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}