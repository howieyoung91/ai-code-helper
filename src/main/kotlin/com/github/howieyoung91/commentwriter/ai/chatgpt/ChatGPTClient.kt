package com.github.howieyoung91.commentwriter.ai.chatgpt

import com.github.howieyoung91.commentwriter.ai.exception.AiException
import com.github.howieyoung91.commentwriter.transport.HTTP
import okhttp3.*
import java.io.IOException

/**
 * ChatGPT 客户端
 *
 * @author Howie Young
 * @date 2023/03/10 00:22
 */
class ChatGPTClient private constructor() {
    fun complete(request: ChatGPTRequest) {
        checkApiKey()
        val r = createRequest(request)
        HTTP.client.newCall(r).enqueue(object : Callback {
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

    private fun checkApiKey() {
        if (ChatGPT.config.apiKey.isEmpty()) {
            throw AiException(
                """
                    Fail to request ChatGPT. Cause: your API Key is null.
                    Get Your API Key from OpenAI: https://platform.openai.com/account/api-keys
                    """.trimMargin()
            )
        }
    }

    private fun createRequest(chatgptRequest: ChatGPTRequest, config: ChatGPTConfig = ChatGPT.config): Request {
        return Request.Builder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${config.apiKey}")
            .post(
                RequestBody.create(
                    MediaType.get("application/json"), body(chatgptRequest)
                )
            )
            .url("https://api.openai.com/v1/completions")
            .build()
    }

    private fun body(chatgptRequest: ChatGPTRequest) = """
                {
                  "model": "text-davinci-003",
                  "prompt": "${chatgptRequest.prompt}",
                  "max_tokens": 1000,
                  "temperature": 0
                }
            """.trimIndent()

    companion object {
        val instance by lazy {
            ChatGPTClient()
        }
    }
}