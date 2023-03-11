package com.github.howieyoung91.aicodehelper.transport

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
