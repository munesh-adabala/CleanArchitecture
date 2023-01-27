package com.code.mlfacedetector

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.code.mlfacedetector.camera.SimpleCameraPreview
import com.code.mlfacedetector.ui.theme.MLFaceDetectorTheme
import com.code.mlfacedetector.viewmodels.FaceDetectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var faceDataViewModel: FaceDetectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
        checkIfCameraPermissionIsGranted()
    }

    private fun setUIContent() {
        setContent {
            MLFaceDetectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()) {
                        SimpleCameraPreview(analyzer = imageAnalyser)
                        OutPutText(faceDataViewModel)
                    }
                }
            }
        }
    }

    private fun setViewModel() {
        faceDataViewModel = ViewModelProvider(this)[FaceDetectionViewModel::class.java]
    }

    /**
     * This function is responsible to request the required CAMERA permission
     */
    private fun checkCameraPermission() {
        try {
            val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, requiredPermissions, 0)
        } catch (e: IllegalArgumentException) {
            checkIfCameraPermissionIsGranted()
        }
    }

    /**
     * This function will check if the CAMERA permission has been granted.
     * If so, it will call the function responsible to initialize the camera preview.
     * Otherwise, it will raise an alert.
     */
    private fun checkIfCameraPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted: start the preview
            setUIContent()
        } else {
            // Permission denied
            checkCameraPermission()
        }
    }

    var frameCount = 10
    private val imageAnalyser = ImageAnalysis.Analyzer {
        try {
            if (frameCount == 0) {
                frameCount = 10
                it.image?.toBitmap()
                    ?.let { it1 -> faceDataViewModel.getDetectedFace(baseContext, it1) }
            }
            --frameCount
            it.close()
        } catch (exception: java.lang.Exception) {
            Log.e("ML_Face", "Exception while detecting face ${exception.message}")
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.YUY2, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}

@Composable
fun OutPutText(faceDataViewModel: FaceDetectionViewModel) {
    val data = faceDataViewModel.dataState.observeAsState()
    data.value?.faceModel?.labelName?.let {
        Text(
        text = it, modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(5.dp)
            .size(18.dp),
        style = TextStyle(
            color = Color.Green,
            fontWeight = FontWeight.W700
        ),
        textAlign = TextAlign.End
    )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MLFaceDetectorTheme {
    }
}