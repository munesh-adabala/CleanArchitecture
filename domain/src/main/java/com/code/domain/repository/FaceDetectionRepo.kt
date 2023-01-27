package com.code.domain.repository

import android.content.Context
import android.graphics.Bitmap
import com.code.domain.models.FaceModel


interface FaceDetectionRepo {
    suspend fun getFaceDetected(baseContext: Context, image: Bitmap): FaceModel?
}