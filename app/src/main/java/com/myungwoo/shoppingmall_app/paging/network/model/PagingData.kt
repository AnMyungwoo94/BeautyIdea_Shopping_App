package com.myungwoo.shoppingmall_app.paging.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DataItem(
    val id: Int,
    val url: String,
    val name: String,
    val country: Country,
    val birthday: String,
    val deathday: String,
    val gender: String,
    val image: Image?,
    val updated: Long,
    val _links: Links
)

@Serializable
data class Country(
    val name: String,
    val code: String,
    val timezone: String
)

@Serializable
data class Image(
    val medium: String,
    val original: String
)

@Serializable
data class Links(
    val self: Self
)

@Serializable
data class Self(
    val href: String
)
