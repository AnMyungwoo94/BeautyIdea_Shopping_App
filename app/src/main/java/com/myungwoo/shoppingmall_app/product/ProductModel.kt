package com.myungwoo.shoppingmall_app.product

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
    var isSelected: Boolean = false
) : java.io.Serializable {
    fun getTotalPrice(): Int {
        return price.toInt() * count
    }
}










