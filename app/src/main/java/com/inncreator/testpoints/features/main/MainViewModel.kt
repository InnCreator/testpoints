package com.inncreator.testpoints.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inncreator.testpoints.common.ResultWrapper
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.domain.models.ProcessedPoint
import com.inncreator.testpoints.domain.usecases.GetPointsUseCase
import com.inncreator.testpoints.features.main.common.UiEffect
import com.inncreator.testpoints.presentation.utils.UiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPointsUseCase: GetPointsUseCase
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<UiEffect<List<ProcessedPoint>>>()
    val uiEffect: SharedFlow<UiEffect<List<ProcessedPoint>>> = _uiEffect

    fun fetchPoints(count: Int) {
        viewModelScope.launch {
            getPointsUseCase(count).collect { result ->
                val effect = when (result) {
                    is ResultWrapper.Success -> UiEffect.Success(result.value)
                    is ResultWrapper.Error -> UiEffect.Error(UiErrorMapper.map(result.error))
                }
                _uiEffect.emit(effect)
            }
        }
    }
}