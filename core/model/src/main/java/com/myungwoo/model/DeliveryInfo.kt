package com.myungwoo.model

data class DeliveryInfo(
    val name: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val memo: String = "",
    val productSum: String = "",
    val products: List<ProductInfo> = emptyList()
)

data class ProductInfo(
    val key: String = "",
    val name: String = "",
    val count: Int = 0,
    val deliveryFee: Int = 0,
    val totalPaymentAmount: Int = 0,
    val deliveryStatus: String = "",
    val time: String = "",
)