package com.myungwoo.shoppingmall_app.ui.bookmark

import android.os.Bundle
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
import com.google.firebase.database.ValueEventListener
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.data.ContentModel
import com.myungwoo.shoppingmall_app.databinding.FragmentBookmarkBinding
import com.myungwoo.shoppingmall_app.ui.tipList.BookmarkRvAdapter
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    lateinit var rvAdapter: BookmarkRvAdapter
    val bookmarkList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater)

        getBookmarkData()

        rvAdapter = BookmarkRvAdapter(requireContext(), items, itemKeyList, bookmarkList)
        val rv: RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter
        rv.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.categoryTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_categoryFragment)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_shopFragment)
        }
        return binding.root
    }

    private fun getCategoryData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(ContentModel::class.java)
                    if (bookmarkList.contains(dataModel.key.toString())) {
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.content.child("categoryALl").addValueEventListener(postListener)
        FBRef.content.child("categoryLip").addValueEventListener(postListener)
        FBRef.content.child("categoryBlusher").addValueEventListener(postListener)
        FBRef.content.child("categoryMascara").addValueEventListener(postListener)
        FBRef.content.child("categoryNail").addValueEventListener(postListener)
        FBRef.content.child("categoryShadow").addValueEventListener(postListener)
        FBRef.content.child("categorySkin").addValueEventListener(postListener)
        FBRef.content.child("categorySun").addValueEventListener(postListener)

    }

    private fun getBookmarkData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    bookmarkList.add(dataModel.key.toString())
                }
                getCategoryData()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }
}