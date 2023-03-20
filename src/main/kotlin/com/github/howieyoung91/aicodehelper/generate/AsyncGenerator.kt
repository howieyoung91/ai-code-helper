/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Howie Young
 * @date 2023/03/14 14:17
 */
abstract class AsyncGenerator<T : Point<*>, REQ, RESP> : AdvancedGenerator<T, REQ, RESP>(), Generator<T> {
    override fun generate(point: T) {
        val point = beforeRequest(point) ?: return
        val request = createRequest(point) ?: return
        doRequest(request, point)
    }

    protected abstract fun createRequest(point: T): REQ?

    protected abstract fun doRequest(request: REQ, point: T)

    protected fun defaultCallback(point: T, request: REQ) = object : Callback<RESP> {
        override fun onResponse(call: Call<RESP>, response: Response<RESP>) {
            val resolvedResponse = beforeResponse(point, call, request, response)
            var result = onResponse(point, resolvedResponse)
            result = afterResponse(point, result)
            onFinal(point, result)
        }

        override fun onFailure(call: Call<RESP>, t: Throwable) {
            beforeFailure(point, call, request, t)
            var result = onFailure(point, t)
            result = afterFailure(point, result)
            onFinal(point, result)
        }
    }


    protected abstract fun onResponse(
        point: T,
        response: Response<RESP>,
    ): GenerateResult

    protected abstract fun onFailure(point: T, t: Throwable): GenerateResult

    protected abstract fun onFinal(point: T, result: GenerateResult)
}

abstract class AdvancedGenerator<T : Point<*>, REQ, RESP> : Generator<T> {
    protected abstract fun beforeRequest(point: T): T?

    protected open fun beforeResponse(
        point: T,
        call: Call<RESP>,
        request: REQ,
        response: Response<RESP>,
    ) = response

    protected open fun afterResponse(point: T, result: GenerateResult): GenerateResult = result

    protected open fun beforeFailure(
        point: T,
        call: Call<RESP>,
        request: REQ,
        t: Throwable,
    ) {

    }

    protected open fun afterFailure(point: T, result: GenerateResult): GenerateResult = result
}