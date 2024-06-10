package com.myungwoo.shoppingmall_app.network.exception

class NetworkUnavailableException(cause: Throwable) :
    ApiFailException(
        "Network not available exception from " +
                "${cause.javaClass.kotlin.simpleName} : " +
                "${cause.message}",
        cause
    )