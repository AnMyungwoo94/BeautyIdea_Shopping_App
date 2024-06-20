package com.myungwoo.network.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody

@Serializable
open class ApiFailResponse(
    val message: String = ""
) {
    companion object {
        fun fromErrorBody(errorBody: ResponseBody?): ApiFailResponse {
            return if (errorBody?.contentLength() != 0L) {
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString(errorBody?.string() ?: "")
            } else {
                ApiFailResponse("empty_error_body")
            }
        }
    }
}