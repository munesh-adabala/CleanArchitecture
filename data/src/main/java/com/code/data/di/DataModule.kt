package com.code.data.di

import android.graphics.ColorSpace.Model
import com.code.data.ml.MLInterpreter
import com.code.data.ml.ModelDownloader
import com.code.data.repository.FaceDetectionRepoImpl
import com.code.domain.repository.FaceDetectionRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMLInterpreter():MLInterpreter{
        return MLInterpreter()
    }

    @Provides
    @Singleton
    fun provideFaceDetectionRepo(mlInterpreter: MLInterpreter): FaceDetectionRepo{
        return FaceDetectionRepoImpl(mlInterpreter)
    }

    @Provides
    @Singleton
    fun provideModelDownloader(): ModelDownloader{
        return ModelDownloader()
    }
}