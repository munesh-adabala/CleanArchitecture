package com.code.data.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.code.domain.models.FaceModel
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MLInterpreter {

    private val LABELS_SIZE = 3;
    private var interpreter:Interpreter? = null

    fun detectFace(context: Context, inputImg: Bitmap, model: File): FaceModel {
        val scaledBitmap = Bitmap.createScaledBitmap(inputImg,224,224,false)
        val input = getInput(scaledBitmap)
        val interpreter = createInterpreter(model)
        val output = getOutput(input, interpreter)
        return getFaceModelFromOutput(output, context)
    }

    private fun getInput(inputImg: Bitmap): ByteBuffer {
        val bitmap = Bitmap.createScaledBitmap(inputImg, 224, 224, true)
        val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)

                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                // For example, some models might require values to be normalized to the range
                // [0.0, 1.0] instead.
                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }
        return input;
    }

    private fun createInterpreter(model: File): Interpreter {
        if(interpreter == null){
            interpreter = Interpreter(model)
        }
        return interpreter as Interpreter
    }

    private fun getOutput(input: ByteBuffer, interpreter: Interpreter): ByteBuffer {
        val bufferSize = LABELS_SIZE * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        interpreter.run(input, modelOutput)
        return modelOutput
    }

    private fun getFaceModelFromOutput(modelOutput: ByteBuffer, context: Context): FaceModel {
        modelOutput.rewind()
        val probabilities = modelOutput.asFloatBuffer()
        try {
            val reader = BufferedReader(
                InputStreamReader(context.assets.open("labels.txt"))
            )
            var maxPropability = 0f;
            var finalLabel = ""
            for (i in 0 until probabilities.capacity()) {
                val label = reader.readLine()
                if(probabilities[i]>maxPropability){
                    maxPropability = probabilities[i]
                    finalLabel = label
                }
            }
            return FaceModel(maxPropability, finalLabel)
        } catch (e: IOException) {
            Log.e("ML_Face", "getFaceModelFromOutput: IO Exception ${e.message}" )
        } catch (e: Exception){
            Log.e("ML_Face", "getFaceModelFromOutput: Exception ${e.message}" )
        }
        return FaceModel(-1f, "Undefined")
    }
}