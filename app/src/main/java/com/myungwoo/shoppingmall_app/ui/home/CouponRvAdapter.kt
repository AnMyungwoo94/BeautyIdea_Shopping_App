package com.myungwoo.shoppingmall_app.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.model.CouponModel
import com.myungwoo.shoppingmall_app.databinding.ItemCouponBinding

class CouponRvAdapter(
    val context: Context, private val items: MutableList<CouponModel>
) : RecyclerView.Adapter<CouponRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val itemsData = items[position]

        val name = binding.nameTextView
        val price = binding.couponTextView

        name.text = itemsData.name
        price.text = itemsData.discount

        val pictureRef = Firebase.storage.reference.child("${itemsData.image}.png")
        pictureRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(context).load(it.result).into(binding.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val binding: ItemCouponBinding) :
        RecyclerView.ViewHolder(binding.root)
}
