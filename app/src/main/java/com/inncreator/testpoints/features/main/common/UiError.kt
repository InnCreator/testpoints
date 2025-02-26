package com.inncreator.testpoints.features.main.common

sealed class UiError {
    data class ServerError(val code: Int, val message: String? = null) : UiError()
    data object NetworkError : UiError()
    data object UnknownError : UiError()
}