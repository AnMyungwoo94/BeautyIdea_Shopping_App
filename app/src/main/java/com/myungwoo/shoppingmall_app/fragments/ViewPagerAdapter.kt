package com.myungwoo.shoppingmall_app.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.myungwoo.shoppingmall_app.ShopCategory

class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return ShopCategoryFragment.create(ShopCategory.entries[position])
    }

    override fun getItemCount(): Int {
        return ShopCategory.entries.size
    }
}