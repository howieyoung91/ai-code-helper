/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import com.intellij.psi.PsiElement
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

abstract class RetryGenerator<T : PsiElement> : AbstractAsyncGenerator<T>() {
    private val cache = ConcurrentHashMap<GeneratePoint<T>, Int>()
    private val maxRetryCount: AtomicInteger = AtomicInteger(3)

    fun maxRetryCount(count: Int) {
        this.maxRetryCount.set(count)
    }

    fun maxRetryCount() = maxRetryCount.get()

    override fun beforeResponse(
        point: GeneratePoint<T>,
        call: Call<CompletionResponse>,
        request: CompletionRequest,
        response: Response<CompletionResponse>,
    ): Response<CompletionResponse> {

        val code = response.code()
        if (code == 200) {
            val body = response.body()
            if (body != null) {
                val choices = body.choices
                if (choices.isNotEmpty() && choices[0].text.isNotEmpty()) {
                    cache.remove(point)
                    return response
                }
            }
        }

        var count = cache.computeIfAbsent(point) { 0 }
        if (++count > maxRetryCount.get()) {
            cache.remove(point)
            return response
        }
        cache[point] = count
        doRequest(request, point)

        return if (code != 200) {
            val errorBody = response.errorBody()!!
            Response.error(
                ProxyResponseBody(
                    errorBody,
                    "Fail to generate. Retrying $count/${maxRetryCount.get()}\nCause: ${errorBody.string()}"
                ), response.raw()
            )
        }
        else {
            val body = response.body()!!
            body.choices[0].text = "Fail to generate. Retrying $count/${maxRetryCount.get()}"
            response
        }
    }
}

private class ProxyResponseBody(private val actual: ResponseBody, private val string: String = "") : ResponseBody() {
    override fun toString(): String = string.ifBlank { actual.string() }
    override fun contentType(): MediaType? = actual.contentType()
    override fun contentLength(): Long = actual.contentLength()
    override fun source(): BufferedSource = actual.source()
}