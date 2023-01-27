package com.code.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.code.domain.common.Resource
import com.code.domain.models.FaceModel
import com.code.domain.repository.FaceDetectionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FaceDetectionUseCase @Inject constructor(private val faceDetectionRepo: FaceDetectionRepo) {
    operator fun invoke(baseContext: Context, image: Bitmap): Flow<Resource<FaceModel?>> = flow {
        try {
      //      emit(Resource.Loading())
            val faceModel = faceDetectionRepo.getFaceDetected(baseContext,image)
            Log.e("ML_Face", "invoke: ${faceModel?.labelName}")
            emit(Resource.Success(faceModel))
        }catch (e: java.lang.Exception){
            emit(Resource.Error("Error while detecting face"))
        }
    }
}