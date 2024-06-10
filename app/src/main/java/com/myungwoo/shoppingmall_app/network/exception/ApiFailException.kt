package com.myungwoo.shoppingmall_app.network.exception

import com.myungwoo.shoppingmall_app.R
import java.net.ConnectException
import java.net.UnknownHostException

sealed class ApiFailException(
    message: String?,
    cause: Throwable? = null
) : Exception(message, cause) {

    companion object {
        fun getApiFailExceptionFrom(throwable: Throwable): ApiFailException {
            return when (throwable) {
                is UnknownHostException -> NetworkUnavailableException(throwable)
                is ConnectException -> NetworkUnavailableException(throwable)
                else -> UnexpectedServerException("On convert exception to ApiFailException. Exception from ${throwable.printStackTrace()}")
            }
        }
    }

    fun getDisplayMessageKey(): Int {
        return when (this) {
            is NetworkUnavailableException -> R.string.network_not_available_message
            else -> R.string.unexpected_error_message
        }
    }
}