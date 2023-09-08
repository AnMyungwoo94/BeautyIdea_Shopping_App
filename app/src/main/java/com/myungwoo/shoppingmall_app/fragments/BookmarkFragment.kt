package com.myungwoo.shoppingmall_app.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.FragmentBookmarkBinding
import com.myungwoo.shoppingmall_app.contentList.BookmarkRvAdapter
import com.myungwoo.shoppingmall_app.contentList.ContentModel
import com.myungwoo.shoppingmall_app.utils.FBAuth
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding
    lateinit var rvAdapter: BookmarkRvAdapter
    val bookmarkList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

       //사용자가 북마크한 정보를 다 가져옴
        getBookmarkData()

        rvAdapter = BookmarkRvAdapter(requireContext(), items, itemKeyList, bookmarkList)
        val rv : RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter
        rv.layoutManager = GridLayoutManager(requireContext(),2)

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment2)
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
    private fun getCategoryData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    //데이터 받아 올 때
                    val item = dataModel.getValue(ContentModel::class.java)
                    //전체 컨텐츠 중에서, 사용자가 북마크한 정보를 보여줌
                    if(bookmarkList.contains(dataModel.key.toString())){
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }
                }
                rvAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.conetent.child("categoryALl").addValueEventListener(postListener)
        FBRef.conetent.child("categoryLip").addValueEventListener(postListener)
        FBRef.conetent.child("categoryBlusher").addValueEventListener(postListener)
        FBRef.conetent.child("categoryMascara").addValueEventListener(postListener)
        FBRef.conetent.child("categoryNail").addValueEventListener(postListener)
        FBRef.conetent.child("categoryShadow").addValueEventListener(postListener)
        FBRef.conetent.child("categorySkin").addValueEventListener(postListener)
        FBRef.conetent.child("categorySun").addValueEventListener(postListener)

    }
    private fun getBookmarkData(){
        //파이어베이스 데이터 읽기(북마크)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    bookmarkList.add(dataModel.key.toString())
                }
                //전체 카테고리에 있는 컨텐츠 데이터들을 다 가져옴
                getCategoryData()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }
}