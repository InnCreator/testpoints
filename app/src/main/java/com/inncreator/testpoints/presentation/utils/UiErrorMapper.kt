package com.inncreator.testpoints.presentation.utils

import com.inncreator.testpoints.features.main.common.UiError
import com.inncreator.testpoints.data.network.ApiError

object UiErrorMapper {
    fun map(apiError: ApiError): UiError {
        return when (apiError) {
            is ApiError.ServerError -> UiError.ServerError(apiError.code, apiError.serverMessage)
            is ApiError.NetworkError -> UiError.NetworkError
            is ApiError.UnknownError -> UiError.UnknownError
        }
    }
}