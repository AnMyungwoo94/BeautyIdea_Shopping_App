package com.myungwoo.shoppingmall_app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.contentList.ContentListActivity
import com.myungwoo.shoppingmall_app.databinding.FragmentTalkBinding
import com.myungwoo.shoppingmall_app.databinding.FragmentTipBinding

class TipFragment : Fragment() {
    private lateinit var binding: FragmentTipBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipBinding.inflate(inflater)

        binding.categoryALl.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryALl")
            startActivity(intent)
        }

        binding.categoryLip.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryLip")
            startActivity(intent)
        }
        binding.categoryBlusher.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryBlusher")
            startActivity(intent)
        }
        binding.categoryMascara.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryMascara")
            startActivity(intent)
        }
        binding.categoryNail.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryNail")
            startActivity(intent)
        }
        binding.categoryShadow.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categoryShadow")
            startActivity(intent)
        }
        binding.categorySkin.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categorySkin")
            startActivity(intent)
        }
        binding.categorySun.setOnClickListener {
            val intent = Intent(context, ContentListActivity::class.java)
            intent.putExtra("category", "categorySun")
            startActivity(intent)
        }

//        binding.homeTap.setOnClickListener {
//            it.findNavController().navigate(R.id.action_tipFragment_to_homeFragment2)
//        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tipFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tipFragment_to_bookmarkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tipFragment_to_shopFragment)
        }

        return binding.root
    }

}