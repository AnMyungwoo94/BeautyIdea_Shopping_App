package com.myungwoo.shoppingmall_app.setting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.myungwoo.shoppingmall_app.Delivery.DeliveryInfo
import com.myungwoo.shoppingmall_app.Delivery.ProductInfo
import com.myungwoo.shoppingmall_app.databinding.ItemDeliveryBinding
import com.myungwoo.shoppingmall_app.databinding.ItemDeliveryProductBinding
import java.text.NumberFormat
import java.util.*

class OrderAdapter(
    private val deliveryInfos: List<DeliveryInfo>,
    private val context: Context,
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deliveryInfo = deliveryInfos[position]
        holder.bind(deliveryInfo)
    }

    override fun getItemCount() = deliveryInfos.size

    inner class ViewHolder(private val binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deliveryInfo: DeliveryInfo) {

            val productAdapter = ProductAdapter(deliveryInfo.products, context)
            binding.productRecyclerview.adapter = productAdapter
            binding.productRecyclerview.layoutManager = LinearLayoutManager(context)
        }
    }
}

// ProductInfo 객체의 리스트를 받아와 각 상품 정보를 표시하는 어댑터
class ProductAdapter(
    private val productInfos: List<ProductInfo>,
    private val context: Context,
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDeliveryProductBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productInfo = productInfos[position]
        holder.bind(productInfo)
    }

    override fun getItemCount() = productInfos.size

    inner class ViewHolder(private val binding: ItemDeliveryProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productInfo: ProductInfo) {
            binding.deliveryDate.text = productInfo.time

            val pictureRef = Firebase.storage.reference.child("${productInfo.key}.png")
            pictureRef.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e("pictureAdapter", "Success")
                    Glide.with(context).load(it.result).into(binding.stuffImage)
                }
            }

            binding.productName.text = productInfo.name.toString()
            binding.productPrice.text  = "총 가격 : ${NumberFormat.getNumberInstance(Locale.US).format(productInfo.totalPaymentAmount)} 원"
            binding.deliveryStatus.text = productInfo.delivery_status.toString()
        }
    }
}