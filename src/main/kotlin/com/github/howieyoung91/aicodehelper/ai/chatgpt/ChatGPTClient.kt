package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.ai.exception.AiException
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val JSON_MEDIA_TYPE = "application/json"

/**
 * ChatGPT 客户端
 *
 * @author Howie Young
 * @date 2023/03/10 00:22
 */
class ChatGPTClient private constructor() {
    fun complete(request: ChatGPTRequest) {
        if (canComplete(request, ChatGPT.config)) {
            doComplete(request)
        }
    }

    private fun canComplete(request: ChatGPTRequest, config: ChatGPTConfig): Boolean {
        if (config.apiKey.isEmpty()) {
            request.callback.onFail(
                AiException(
                    """
                        Fail to request ChatGPT. Cause: your API Key is null.
                        Get Your API Key from OpenAI: https://platform.openai.com/account/api-keys
                        """.trimMargin()
                )
            )
            return false
        }
        return true
    }

    private fun doComplete(request: ChatGPTRequest, config: ChatGPTConfig = ChatGPT.config) {
        httpclient.newCall(createRequest(request, config)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                request.callback.onFail(AiException(e))
            }

            override fun onResponse(call: Call, response: Response) {
                request.callback.onResponse(
                    ChatGPTResponse {
                        content(response.body()?.string() ?: "")
                    }
                )
            }
        })
    }

    private fun createRequest(chatgptRequest: ChatGPTRequest, config: ChatGPTConfig = ChatGPT.config): Request {
        return Request.Builder()
            .header("Content-Type", JSON_MEDIA_TYPE)
            .header("Authorization", config.apiKey)
            .post(
                RequestBody.create(
                    MediaType.get(JSON_MEDIA_TYPE), body(chatgptRequest, config)
                )
            )
            .url("https://api.openai.com/v1/completions") // TODO 支持自己的服务器
            .build()
    }

    private fun body(chatgptRequest: ChatGPTRequest, config: ChatGPTConfig = ChatGPT.config): String = """
                {
                  "model": "${config.model}",
                  "prompt": "${chatgptRequest.prompt}",
                  "max_tokens": ${config.maxToken},
                  "temperature": ${config.temperature}
                }
            """.trimIndent()

    companion object {
        val instance by lazy {
            ChatGPTClient()
        }

        private val httpclient by lazy {
            OkHttpClient.Builder()
                .connectionPool(ConnectionPool(20, 10, TimeUnit.MINUTES))
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }
    }
}