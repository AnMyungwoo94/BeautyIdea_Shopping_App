package com.myungwoo.common.exception

class NetworkUnavailableException(cause: Throwable) :
    ApiFailException(
        "Network not available exception from " +
                "${cause.javaClass.kotlin.simpleName} : " +
                "${cause.message}",
        cause
    )