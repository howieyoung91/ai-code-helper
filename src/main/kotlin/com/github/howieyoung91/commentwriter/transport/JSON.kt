package com.github.howieyoung91.commentwriter.transport

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
val JSON = moshi.adapter(JsonResponse::class.java)

data class JsonResponse(
    var id: String = "",
    var `object`: String = "",
    var created: Int = -1,
    var model: String = "",
    var choices: Array<Choice> = emptyArray(),
)

data class Choice(
    var text: String = "",
    var index: Int = -1,
    var logprobs: String? = null,
    var finish_reason: String = "",
)