package com.inncreator.testpoints.features.main.common

sealed class UiEffect<out T> {
    data class Success<out T>(val data: T) : UiEffect<T>()
    data class Error(val error: UiError) : UiEffect<Nothing>()
    // todo добавить Лоадер, для долгих сетевых запросов. Дизейблить кнопку/ показывать прогресс бар
}