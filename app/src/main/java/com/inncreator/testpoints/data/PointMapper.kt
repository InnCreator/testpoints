package com.inncreator.testpoints.data

import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.domain.models.ProcessedPoint

object PointMapper {
    fun mapToProcessedPoints(points: List<Point>): List<ProcessedPoint> {
        return points.sortedBy { it.x }
            .mapIndexed { index, point -> ProcessedPoint(index + 1, point.x, point.y) }
    }
}