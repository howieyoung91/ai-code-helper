/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate

import com.github.howieyoung91.aicodehelper.config.FAIL_MESSAGE
import com.github.howieyoung91.aicodehelper.generate.process.EscapeProcessor
import com.github.howieyoung91.aicodehelper.generate.process.NoticeRequestingProcessor
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import com.intellij.psi.PsiElement
import retrofit2.Response

/**
 * @author Howie Young
 * @date 2023/03/14 13:47
 */
abstract class ChatGPTAsyncGenerator<T : PsiElement> : RetryGenerator<T>() {
    protected val processors = listOf(NoticeRequestingProcessor<T>(), EscapeProcessor())
    protected val failMessage: String = FAIL_MESSAGE

    /**
     * apply processors before request
     */
    override fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T> {
        processors.forEach { it.beforeRequest(point) }
        return point
    }

    /**
     * generate comment
     */
    override fun onResponse(point: GeneratePoint<T>, response: Response<CompletionResponse>): GenerateResult {
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

    /**
     * apply processors after response
     */
    override fun afterResponse(point: GeneratePoint<T>, result: GenerateResult): GenerateResult {
        var r = result
        processors.forEach { r = it.afterResponse(point, result) } // TODO 处理 ChatGPT 响应的非法字符
        return r
    }

    override fun onFailure(point: GeneratePoint<T>, t: Throwable): GenerateResult {
        val comment = if (t.message != null) "[AI Code Helper] Fail to generate\nCause: ${t.message}" else failMessage
        return GenerateResult(comment, true)
    }
}
