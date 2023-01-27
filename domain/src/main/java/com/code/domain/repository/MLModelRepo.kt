package com.code.domain.repository

import java.io.File

interface MLModelRepo {
    suspend fun getModel():File
}