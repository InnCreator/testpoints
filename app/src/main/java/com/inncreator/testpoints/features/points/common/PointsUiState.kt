package com.inncreator.testpoints.features.points.common

import com.inncreator.testpoints.domain.models.ProcessedPoint

sealed class PointsUiState {
    data object Loading : PointsUiState()
    data class Init(
        val processedPoints: List<ProcessedPoint>,
        val minWidth: Triple<Int, Int, Int>,
        val chartSettings: ChartSettings,
    ) : PointsUiState()
    data class Update(
        val processedPoints: List<ProcessedPoint>,
        val chartSettings: ChartSettings,
    ) : PointsUiState()
    data class Error(val error: Throwable) : PointsUiState()
}