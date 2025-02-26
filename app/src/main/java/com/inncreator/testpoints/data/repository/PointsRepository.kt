package com.inncreator.testpoints.data.repository

import com.inncreator.testpoints.common.ResultWrapper
import com.inncreator.testpoints.data.PointMapper
import com.inncreator.testpoints.data.network.ApiError
import com.inncreator.testpoints.data.network.ApiService
import com.inncreator.testpoints.data.network.NetworkUtils
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.domain.models.ProcessedPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PointsRepository @Inject constructor(private val apiService: ApiService) {

    fun fetchPoints(count: Int): Flow<ResultWrapper<List<ProcessedPoint>>> = flow {
        try {
            val response = apiService.getPoints(count)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(ResultWrapper.Success(PointMapper.mapToProcessedPoints(body.points)))
                } else {
                    emit(ResultWrapper.Error(ApiError.ServerError(response.code())))
                }
            } else {
                emit(
                    ResultWrapper.Error(
                        NetworkUtils.extractError(
                            response.errorBody(),
                            response.code()
                        )
                    )
                )
            }
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(ApiError.ServerError(e.code(), e.message())))
        } catch (e: IOException) {
            emit(ResultWrapper.Error(ApiError.NetworkError))
        } catch (e: Exception) {
            emit(ResultWrapper.Error(ApiError.UnknownError))
        }
    }.flowOn(Dispatchers.IO)
}