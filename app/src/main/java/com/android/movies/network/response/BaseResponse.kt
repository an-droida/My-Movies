package com.android.movies.network.response

sealed class BaseResponse<T>(val data: T? = null, val errorCode: String? = null,
                             val message: String? = null) {
    class Success<T>(data: T) : BaseResponse<T>(data)
    class Loading<T> : BaseResponse<T>()
    class Failure<T>(message: String?, errorCode:String? = null, data: T? = null) : BaseResponse<T>(message = message, errorCode = errorCode, data = data)
}
