package com.inncreator.testpoints.data.network

import okhttp3.ResponseBody

object NetworkUtils {
    fun extractError(errorBody: ResponseBody?, code: Int): ApiError {
        return try {
            val message = errorBody?.string()
            ApiError.ServerError(code, message)
        } catch (e: Exception) {
            ApiError.ServerError(code, null)
        }
    }
}