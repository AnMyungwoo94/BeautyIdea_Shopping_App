package com.myungwoo.common.exception

class UnexpectedServerException(message: String) :
    ApiFailException(
        "Unexpected exception in network process, $message",
        null
    )