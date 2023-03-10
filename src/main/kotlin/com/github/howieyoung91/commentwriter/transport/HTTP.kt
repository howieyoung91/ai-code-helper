package com.github.howieyoung91.commentwriter.transport

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author Howie Young
 * @date 2023/03/10 01:58
 */
class HTTP private constructor() {
    companion object {
        val client by lazy {
            OkHttpClient.Builder()
                .connectionPool(ConnectionPool(20, 10, TimeUnit.MINUTES))
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }
    }
}
