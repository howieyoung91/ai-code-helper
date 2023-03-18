/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate.edit

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT.Companion.client
import com.github.howieyoung91.aicodehelper.generate.ChatGPTAsyncGenerator
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.Point
import com.github.howieyoung91.chatgpt.client.edit.EditRequest
import com.github.howieyoung91.chatgpt.client.edit.EditResponse
import retrofit2.Response

/**
 * @author Howie Young
 * @date 2023/03/18 19:02
 */
abstract class EditGenerator<T : Point<*>> : ChatGPTAsyncGenerator<T, EditRequest, EditResponse>() {
    override fun doRequest(request: EditRequest, point: T) {
        client.value.edit(request, defaultCallback(point, request))
    }

    override fun onResponse(point: T, response: Response<EditResponse>): GenerateResult {
        var comment = ""
        if (response.code() != 200) {
            val errorBody = response.errorBody()
            if (errorBody != null) {
                comment = "[AI Code Helper] $errorBody"
            }
        }
        else {
            val body = response.body()
            if (body != null && body.choices.isNotEmpty()) {
                comment = body.choices[0].text
            }
        }
        return GenerateResult(comment, false)
    }
}