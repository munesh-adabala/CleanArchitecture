package com.code.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.code.data.ml.MLInterpreter
import com.code.data.ml.ModelDownloader
import com.code.domain.models.FaceModel
import com.code.domain.repository.FaceDetectionRepo
import java.io.File
import javax.inject.Inject

class FaceDetectionRepoImpl @Inject constructor(private val mlInterpreter: MLInterpreter): FaceDetectionRepo {

    @Inject
    lateinit var modelDownloader:ModelDownloader

    private var customModel: File? = null

    override suspend fun getFaceDetected(baseContext: Context, image: Bitmap): FaceModel? {
        if(customModel == null){
            customModel = ModelDownloader().downloadModel()
        }

        return customModel?.let {
            image.let { it1 ->
                mlInterpreter.detectFace(
                    context = baseContext,
                    inputImg = it1,
                    it
                )
            }
        }
    }
}