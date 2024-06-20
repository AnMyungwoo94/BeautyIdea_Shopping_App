package com.myungwoo.shoppingmall_app.common

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.model.ProductModel
import com.myungwoo.shoppingmall_app.databinding.ProductRvItemBinding
import com.myungwoo.shoppingmall_app.ui.product.ProductDetailActivity
import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale

class ProductRvAdapter(
    val context: Context, private val items: MutableList<ProductModel>
) : RecyclerView.Adapter<ProductRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ProductRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding
        val itemsData = items[position]

        val name = binding.nameText
        val price = binding.priceText
        val product_item = binding.productItem

        name.text = itemsData.name
        price.text = "${NumberFormat.getNumberInstance(Locale.US).format(itemsData.price.toInt())} "

        val pictureRef = Firebase.storage.reference.child("${itemsData.key}.png")
        pictureRef.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(context).load(it.result).into(binding.imageArea)
            }
        }
        product_item.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("ITEM_DATA", itemsData as Serializable)
            binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}
