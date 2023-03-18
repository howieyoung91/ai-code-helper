/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.completion

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.generate.ChatGPTAsyncGenerator
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.Point
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

abstract class CompletionGenerator<T : Point<*>> : ChatGPTAsyncGenerator<T, CompletionRequest, CompletionResponse>() {
    private val cache = ConcurrentHashMap<String, Int>()
    private val maxRetryCount: AtomicInteger = AtomicInteger(3)
    override fun doRequest(request: CompletionRequest, point: T) {
        ChatGPT.client.value.complete(request, defaultCallback(point, request))
    }

    fun maxRetryCount(count: Int) {
        this.maxRetryCount.set(count)
    }

    fun maxRetryCount() = maxRetryCount.get()

    override fun beforeResponse(
        point: T,
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
                    cache.remove(point.key)
                    return response
                }
            }
        }

        var count = cache.computeIfAbsent(point.key) { 0 }
        if (++count > maxRetryCount.get()) {
            cache.remove(point.key)
            if (code != 200) {
                val errorBody = response.errorBody()!!
                return proxy(
                    response,
                    "Fail to generate.\nCause: ${errorBody.string()}",
                    errorBody
                )
            }
            return response
        }
        cache[point.key] = count
        doRequest(request, point)

        return if (code != 200) {
            val errorBody = response.errorBody()!!
            proxy(
                response,
                "Fail to generate. Retrying $count/${maxRetryCount.get()}\nCause: ${errorBody.string()}",
                errorBody
            )
        }
        else {
            val body = response.body()!!
            body.choices[0].text = "Fail to generate. Retrying $count/${maxRetryCount.get()}"
            response
        }
    }

    private fun proxy(
        response: Response<CompletionResponse>,
        s: String,
        errorBody: ResponseBody,
    ): Response<CompletionResponse> = Response.error(ProxyResponseBody(errorBody, s), response.raw())

    override fun onResponse(point: T, response: Response<CompletionResponse>): GenerateResult {
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

private class ProxyResponseBody(private val actual: ResponseBody, private val string: String = "") : ResponseBody() {
    override fun toString(): String = string.ifBlank { actual.string() }
    override fun contentType(): MediaType? = actual.contentType()
    override fun contentLength(): Long = actual.contentLength()
    override fun source(): BufferedSource = actual.source()
}