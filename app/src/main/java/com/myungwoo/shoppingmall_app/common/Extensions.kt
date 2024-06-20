package com.myungwoo.shoppingmall_app.common

import com.myungwoo.common.exception.ApiFailException
import com.myungwoo.common.exception.NetworkUnavailableException
import com.myungwoo.shoppingmall_app.R

fun ApiFailException.getDisplayMessageKey(): Int {
    return when (this) {
        is NetworkUnavailableException -> R.string.network_not_available_message
        else -> R.string.unexpected_error_message
    }
}