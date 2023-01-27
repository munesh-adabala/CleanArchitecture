package com.code.mlfacedetector.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.domain.common.Resource
import com.code.domain.usecases.FaceDetectionUseCase
import com.code.mlfacedetector.models.FaceDetectStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val faceDetectionUseCase: FaceDetectionUseCase
): ViewModel() {

    private val _dataState = MutableLiveData<FaceDetectStateModel?>()
    val dataState: LiveData<FaceDetectStateModel?>
        get() = _dataState
    init {
        _dataState.value = FaceDetectStateModel().apply {
            loading = true
        }
    }

    fun getDetectedFace(baseContext: Context, image: Bitmap) {
        viewModelScope.launch {
            faceDetectionUseCase.invoke(baseContext,image).onEach {
                when(it){
                    is Resource.Success->{
                        Log.e("ML_Face", "getDetectedFace: Success ${it.data}")
                        _dataState.value = dataState.value?.also {data->
                            data.loading = false
                            data.faceModel = it.data
                        }
                    }
                    is Resource.Error->{

                    }
                    is Resource.Loading->{

                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}