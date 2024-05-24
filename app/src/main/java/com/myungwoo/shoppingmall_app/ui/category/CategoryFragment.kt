package com.myungwoo.shoppingmall_app.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.FragmentCategoryBinding
import com.myungwoo.shoppingmall_app.utils.ShopCategory

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_categoryFragment_to_shopFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_categoryFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_categoryFragment_to_bookmarkFragment)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_categoryFragment_to_tipFragment)
        }
    }

    private fun setViewPager() {
        binding.vpHome.adapter = ViewPagerAdapter(this)
        attachTabLayout()
    }

    private fun attachTabLayout() {
        TabLayoutMediator(binding.tlHome, binding.vpHome) { tab, position ->
            tab.text = getString(ShopCategory.entries[position].resId)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}