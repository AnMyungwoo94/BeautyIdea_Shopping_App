package com.myungwoo.shoppingmall_app.ui.category

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.myungwoo.shoppingmall_app.R

enum class ShopCategory(
    @StringRes val resId: Int,
    @DrawableRes val imageRes: Int,
    val firebaseCategoryName: String
) {
    LIP(R.string.lip, R.drawable.category2_lip, "categoryLip"),
    BLUSHER(R.string.blusher, R.drawable.category3_blusher, "categoryBlusher"),
    MASCARA(R.string.mascara, R.drawable.category4_mascara, "categoryMascara"),
    NAIL(R.string.nail, R.drawable.category5_nail, "categoryNail"),
    SHADOW(R.string.shadow, R.drawable.category6_shadow, "categoryShadow"),
    SKIN(R.string.skin, R.drawable.category7_skin, "categorySkin"),
    SUN(R.string.sun, R.drawable.category8_sun, "categorySun"),
    ETC(R.string.etc, R.drawable.category1_all, "categoryALl")
}
