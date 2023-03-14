/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate

import com.github.howieyoung91.aicodehelper.config.FAIL_MESSAGE
import com.github.howieyoung91.aicodehelper.generate.process.EscapeProcessor
import com.github.howieyoung91.aicodehelper.generate.process.NoticeRequestingProcessor
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import com.intellij.psi.PsiElement
import retrofit2.Response

/**
 * @author Howie Young
 * @date 2023/03/14 13:47
 */
abstract class ChatGPTAsyncGenerator<T : PsiElement> : AbstractAsyncGenerator<T>() {
    protected val processors = listOf(NoticeRequestingProcessor<T>(), EscapeProcessor())
    override fun beforeRequest(point: GeneratePoint<T>): CompletionRequest {
        processors.forEach { it.beforeRequest(point) }
        return createRequest(point)
    }

    override fun onResponse(point: GeneratePoint<T>, response: Response<CompletionResponse>): GenerateResult {
        val result = createGenerateResult(response)
        // TODO 处理 ChatGPT 响应的非法字符
        processors.forEach { it.afterResponse(result) }
        return result
    }

    override fun onFailure(point: GeneratePoint<T>, t: Throwable): GenerateResult {
        val comment = if (t.message != null) "[AI Code Helper] ${t.message}" else FAIL_MESSAGE
        return GenerateResult(comment, true)
    }

    override fun onFinal(point: GeneratePoint<T>, result: GenerateResult) {

    }

    private fun createGenerateResult(response: Response<CompletionResponse>): GenerateResult {
        var comment = ""
        if (response.code() != 200) {
            val errorBody = response.errorBody()
            if (errorBody != null) {
                comment = "[AI Code Helper] ${errorBody.string()}"
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
