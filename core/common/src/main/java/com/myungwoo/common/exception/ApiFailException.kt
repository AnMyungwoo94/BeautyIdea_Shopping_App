package com.myungwoo.common.exception

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
}