package com.code.data.repository

import com.code.domain.repository.MLModelRepo
import java.io.File

class MLModelRepoImpl: MLModelRepo {
    override suspend fun getModel(): File {
        TODO("Not yet implemented")
    }
}