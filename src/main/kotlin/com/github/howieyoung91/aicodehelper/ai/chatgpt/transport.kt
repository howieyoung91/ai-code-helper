package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.transport.moshi
import com.squareup.moshi.Json

val JSON = moshi.adapter(JsonBody::class.java)

data class JsonBody(
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
    @Json(name = "finish_reason")
    var finishReason: String = "",
)