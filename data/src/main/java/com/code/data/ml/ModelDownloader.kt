package com.code.data.ml
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File

class ModelDownloader {

    companion object{
        const val ML_MODEL = "m_face_detect"
    }
    suspend fun downloadModel(): File? {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        var file: File? = null
        val result = CoroutineScope(Dispatchers.IO).async {
            FirebaseModelDownloader.getInstance()
                .getModel(ML_MODEL, DownloadType.LOCAL_MODEL, conditions)
                .addOnSuccessListener {
                    val model = it?.file
                    model?.let { tf_file ->
                        file = tf_file
                        Log.d("ModelDownloader", "downloadModel: Model downloaded with name $ML_MODEL")
                    }
                }
            return@async file
        }
        result.await()
        return file
    }
}