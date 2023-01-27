package com.code.mlfacedetector.models

import com.code.domain.models.FaceModel

class FaceDetectStateModel {
    val error: String = ""
    var loading: Boolean = false
    var faceModel: FaceModel? = null
}