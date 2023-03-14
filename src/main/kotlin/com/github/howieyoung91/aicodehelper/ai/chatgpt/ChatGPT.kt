/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ai.chatgpt

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.chatgpt.client.ChatGPTClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val converterFactory = MoshiConverterFactory.create(
    Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
)

class ChatGPT private constructor() {
    companion object {
        val config: ChatGPTConfig by lazy { ChatGPTConfig.instance }
        var client: Lazy<ChatGPTClient> = lazy {
            CustomChatGPTClient(config.apikey, config.serverUrl)
        }
    }
}

class CustomChatGPTClient(
    apiKey: String = ChatGPT.config.apikey,
    serverUrl: String = ChatGPT.config.serverUrl,
) : ChatGPTClient(apiKey, {
    client(
        OkHttpClient.Builder()
            .connectionPool(ConnectionPool())
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    )
    baseUrl(serverUrl)
    addConverterFactory(converterFactory)
})