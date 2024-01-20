package com.myungwoo.shoppingmall_app.product

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myungwoo.shoppingmall_app.databinding.ProductcartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*

class ProductCartRvAdapter(
    val context: Context,
    private val items: MutableList<ProductModel>,
    val onItemChanged: () -> Unit
) : RecyclerView.Adapter<ProductCartRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductcartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = items[position]

        with(holder.binding) {
            productName.text = product.name
            productPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(product.countSum)}"
            quantityTextView.text = product.count.toString()
            val deliveryFee = product.deliveryFee
            if (deliveryFee != 0) {
                productDeliveryFee.text = "배송비 ${NumberFormat.getNumberInstance(Locale.US).format(deliveryFee)} 원"
            } else {
                productDeliveryFee.text = "배송비 무료"
            }
            val pictureRef = Firebase.storage.reference.child("${product.key}.png")
            pictureRef.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Glide.with(context).load(it.result).into(productImage)
                }
            }
            deleteButton.setOnClickListener {
                val userUID = FirebaseAuth.getInstance().currentUser?.uid
                FirebaseDatabase.getInstance().getReference("cart/$userUID/${product.key}").removeValue()
            }

            minusBtn.setOnClickListener {
                if (product.count > 1) {
                    product.count -= 1
                    product.countSum = product.getTotalPrice()
                    productPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(product.countSum)}"
                    updateProductInFirebase(product)  // Firebase 업데이트
                    quantityTextView.text = product.count.toString()
                    onItemChanged()
                }
            }

            plusBtn.setOnClickListener {
                product.count += 1
                product.countSum = product.getTotalPrice()
                productPrice.text = "${NumberFormat.getNumberInstance(Locale.US).format(product.countSum)}"
                updateProductInFirebase(product)
                quantityTextView.text = product.count.toString()
                onItemChanged()

            }
            productCheckBox.setOnCheckedChangeListener(null)
            productCheckBox.isChecked = product.isSelected
            productCheckBox.setOnCheckedChangeListener { _, isChecked ->
                items[position].isSelected = isChecked
                onItemChanged()

                val userUID = FirebaseAuth.getInstance().currentUser?.uid
                val productKey = items[position].key
                val ref = FirebaseDatabase.getInstance().getReference("cart/$userUID/$productKey")
                ref.child("isSelected").setValue(isChecked)
            }
        }
    }

    private fun updateProductInFirebase(product: ProductModel) {
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("cart/$userUID/${product.key}")
        ref.child("count").setValue(product.count.toString())
        ref.child("count_sum").setValue(product.countSum.toString())
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ProductcartItemBinding) : RecyclerView.ViewHolder(binding.root)
}

