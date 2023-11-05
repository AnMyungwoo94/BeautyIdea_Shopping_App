package com.myungwoo.shoppingmall_app.Delivery

data class DeliveryInfo(
    val name: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val memo : String = "",
    val product_sum : String = "",
    val products: List<ProductInfo> = emptyList()
)

data class ProductInfo(
    val key: String = "",
    val name : String = "",
    val count: Int = 0,
    val deliveryFee: Int = 0,
    val totalPaymentAmount: Int = 0,
    val delivery_status : String = "",
    val time : String = "",
)