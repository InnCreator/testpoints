package com.inncreator.testpoints.data.network

sealed class ApiError {
    data class ServerError(val code: Int, val serverMessage: String? = null) : ApiError()
    data object NetworkError : ApiError()
    data object UnknownError : ApiError()
}