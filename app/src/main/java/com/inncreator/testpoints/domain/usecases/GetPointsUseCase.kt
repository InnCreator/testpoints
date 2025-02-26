package com.inncreator.testpoints.domain.usecases

import com.inncreator.testpoints.common.ResultWrapper
import com.inncreator.testpoints.data.repository.PointsRepository
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.domain.models.ProcessedPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * по хорошему тут должен быть интерфейс, а не прямая завязка на Дата слой,
 * но посчитал это излишним в данном случае
 */
class GetPointsUseCase @Inject constructor(private val repository: PointsRepository) {
    operator fun invoke(count: Int): Flow<ResultWrapper<List<ProcessedPoint>>> {
        return repository.fetchPoints(count)
    }
}