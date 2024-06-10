package com.myungwoo.shoppingmall_app.network.adapter

import com.myungwoo.shoppingmall_app.network.exception.ApiFailException
import com.myungwoo.shoppingmall_app.network.exception.ApiFailResponseException
import com.myungwoo.shoppingmall_app.network.response.ApiFailResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ResultCallAdapter<R : Any>(private val responseType: Type) :
    CallAdapter<R, Call<Result<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Call<Result<R>> {
        return ResultCall(call)
    }

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            // 먼저 리턴 타입의 로우 타입이 Call인지 확인한다.
            if (getRawType(returnType) != Call::class.java) {
                return null
            }
            // 이후 리턴타입이 제네릭 인자를 가지는지 확인한다. 리턴 타입은 Call<?>가 돼야 한다.
            check(returnType is ParameterizedType) {
                "return type must be parameterized as Call<Result<Foo>> or Call<Result<out Foo>>"
            }
            // 리턴 타입에서 첫 번째 제네릭 인자를 얻는다.
            val responseType = getParameterUpperBound(0, returnType)

            // 기대한것 처럼 동작하기 위해선 추출한 제네릭 인자가 Result 타입이어야 한다.
            if (getRawType(responseType) != Result::class.java) {
                return null
            }
            // Result 클래스가 제네릭 인자를 가지는지 확인한다. 제네릭 인자로는 응답을 변환할 클래스를 받아야 한다.
            check(responseType is ParameterizedType) {
                "Response must be parameterized as Result<Foo> or Result<out Foo>"
            }
            // 마지막으로 Result의 제네릭 인자를 얻어서 CallAdapter를 생성한다.
            val successBodyType = getParameterUpperBound(0, responseType)

            return ResultCallAdapter<Any>(successBodyType)
        }
    }
}

class ResultCall<T : Any>(private val call: Call<T>) : Call<Result<T>> {

    override fun clone(): Call<Result<T>> = ResultCall(call.clone())

    override fun execute(): Response<Result<T>> {
        throw UnsupportedOperationException("ResultCall doesn't support execute")
    }

    override fun enqueue(callback: Callback<Result<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val result = if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    val errorResponse = ApiFailResponse.fromErrorBody(response.errorBody())
                    Result.failure(
                        ApiFailResponseException(
                            errorResponse.message
                        )
                    )
                }
                callback.onResponse(this@ResultCall, Response.success(result))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ResultCall, Response.success(
                        Result.failure(
                            ApiFailException.getApiFailExceptionFrom(t)
                        )
                    )
                )
            }
        })
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() = call.cancel()

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}