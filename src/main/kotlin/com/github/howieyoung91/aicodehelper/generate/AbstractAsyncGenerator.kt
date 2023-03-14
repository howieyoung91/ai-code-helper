/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import com.intellij.psi.PsiElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Howie Young
 * @date 2023/03/14 14:17
 */
abstract class AbstractAsyncGenerator<T : PsiElement> : CodeGenerator<T> {
    override fun generate(point: GeneratePoint<T>) {
        val request = beforeRequest(point)
        // 目前先使用到 ChatGPT
        ChatGPT.client.value.complete(request, object : Callback<CompletionResponse> {
            override fun onResponse(call: Call<CompletionResponse>, response: Response<CompletionResponse>) {
                val result = onResponse(point, response)
                onFinal(point, result)
            }

            override fun onFailure(call: Call<CompletionResponse>, t: Throwable) {
                val result = onFailure(point, t)
                onFinal(point, result)
            }
        })
    }

    abstract fun beforeRequest(point: GeneratePoint<T>): CompletionRequest
    abstract fun createRequest(point: GeneratePoint<T>): CompletionRequest
    abstract fun onResponse(point: GeneratePoint<T>, response: Response<CompletionResponse>): GenerateResult
    abstract fun onFailure(point: GeneratePoint<T>, t: Throwable): GenerateResult
    abstract fun onFinal(point: GeneratePoint<T>, result: GenerateResult)
}