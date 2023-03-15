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
abstract class AbstractAsyncGenerator<T : PsiElement> : AdvancedGenerator<T>(), CodeGenerator<T> {
    final override fun generate(point: GeneratePoint<T>) {
        val point = beforeRequest(point)
        val request = createRequest(point)
        doRequest(request, point)
    }

    protected fun doRequest(request: CompletionRequest, point: GeneratePoint<T>) {
        // 目前先使用到 ChatGPT
        ChatGPT.client.value.complete(request, object : Callback<CompletionResponse> {
            override fun onResponse(call: Call<CompletionResponse>, response: Response<CompletionResponse>) {
                val resolvedResponse = beforeResponse(point, call, request, response)
                var result = onResponse(point, resolvedResponse)
                result = afterResponse(point, result)
                onFinal(point, result)
            }

            override fun onFailure(call: Call<CompletionResponse>, t: Throwable) {
                beforeFailure(point, call, request, t)
                val result = onFailure(point, t)
                onFinal(point, result)
            }
        })
    }

    protected abstract fun createRequest(point: GeneratePoint<T>): CompletionRequest

    protected abstract fun onResponse(point: GeneratePoint<T>, response: Response<CompletionResponse>): GenerateResult

    protected abstract fun onFailure(point: GeneratePoint<T>, t: Throwable): GenerateResult

    protected abstract fun onFinal(point: GeneratePoint<T>, result: GenerateResult)
}

abstract class AdvancedGenerator<T : PsiElement> : CodeGenerator<T> {
    protected abstract fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T>

    protected open fun beforeResponse(
        point: GeneratePoint<T>,
        call: Call<CompletionResponse>,
        request: CompletionRequest,
        response: Response<CompletionResponse>,
    ) = response

    protected open fun afterResponse(point: GeneratePoint<T>, result: GenerateResult): GenerateResult = result

    protected open fun beforeFailure(
        point: GeneratePoint<T>,
        call: Call<CompletionResponse>,
        request: CompletionRequest,
        t: Throwable,
    ) {

    }

    protected open fun afterFailure(point: GeneratePoint<T>, result: GenerateResult): GenerateResult = result
}