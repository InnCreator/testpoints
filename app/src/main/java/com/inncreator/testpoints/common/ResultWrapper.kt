package com.inncreator.testpoints.common

import com.inncreator.testpoints.data.network.ApiError

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val error: ApiError) : ResultWrapper<Nothing>()
}