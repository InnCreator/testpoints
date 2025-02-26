package com.inncreator.testpoints.features.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inncreator.testpoints.features.points.common.ChartSettings
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.data.PointMapper
import com.inncreator.testpoints.domain.models.ProcessedPoint
import com.inncreator.testpoints.features.points.common.PointsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PointsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<PointsUiState>(PointsUiState.Loading)
    val uiState: StateFlow<PointsUiState> get() = _uiState

    fun processPoints(points: List<ProcessedPoint>) {
        viewModelScope.launch {
            try {
                val minWidth = withContext(Dispatchers.Default) {
                    points.fold(Triple(0, 0, 0)) { acc, point ->
                        Triple(
                            maxOf(acc.first, point.counter.toString().length),
                            maxOf(acc.second, point.x.toString().length),
                            maxOf(acc.third, point.y.toString().length)
                        )
                    }
                }
                val currentSettings = when (val state = _uiState.value) {
                    is PointsUiState.Init -> state.chartSettings
                    is PointsUiState.Update -> state.chartSettings
                    else -> ChartSettings()
                }
                _uiState.value = PointsUiState.Init(
                    processedPoints = points,
                    minWidth = minWidth,
                    chartSettings = currentSettings
                )
            } catch (e: Throwable) {
                _uiState.value = PointsUiState.Error(e)
            }
        }
    }

    fun updateChartSettings(update: (ChartSettings) -> ChartSettings) {
        viewModelScope.launch {
            when (val currentState = _uiState.value) {
                is PointsUiState.Init -> {
                    val newSettings = update(currentState.chartSettings)
                    _uiState.value = PointsUiState.Update(
                        processedPoints = currentState.processedPoints,
                        chartSettings = newSettings
                    )
                }
                is PointsUiState.Update -> {
                    val newSettings = update(currentState.chartSettings)
                    _uiState.value = PointsUiState.Update(
                        processedPoints = currentState.processedPoints,
                        chartSettings = newSettings
                    )
                }
                else -> {}
            }
        }
    }
}