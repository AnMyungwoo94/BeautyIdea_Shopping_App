package com.myungwoo.shoppingmall_app.network.exception

class UnexpectedServerException(message: String) :
    ApiFailException(
        "Unexpected exception in network process, $message",
        null
    )