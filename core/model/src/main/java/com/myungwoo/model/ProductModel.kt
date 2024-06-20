package com.myungwoo.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ProductModel(
    val key: String = "",
    val name: String = "",
    val price: String = "",
    val time: String = "",
    val parcel: String = "",
    val deliveryFee: Int = 0,
    val parcelDay: String = "",
    val category: String = "",
    var count: Int = 0,
    var count_sum: Int = 1,
    @SerializedName("selected")
    var isSelected: Boolean = false
) : java.io.Serializable {
    fun getTotalPrice(): Int {
        return price.toInt() * count
    }
}








