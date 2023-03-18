/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate

import com.github.howieyoung91.aicodehelper.config.FAIL_MESSAGE
import com.github.howieyoung91.aicodehelper.generate.process.FailureProcessor
import com.github.howieyoung91.aicodehelper.generate.process.Processor
import com.github.howieyoung91.aicodehelper.util.Type.Companion.isA

/**
 * @author Howie Young
 * @date 2023/03/14 13:47
 */
abstract class ChatGPTAsyncGenerator<T : Point<*>, REQ, RESP> : AsyncGenerator<T, REQ, RESP>() {
    abstract val processors: ArrayList<Processor<T>>

    protected val failMessage: String = FAIL_MESSAGE

    /**
     * apply processors before request
     */
    override fun beforeRequest(point: T): T? {
        var p: T? = point
        processors.forEach { p = it.beforeRequest(point) ?: return null }
        return p
    }

    /**
     * apply processors after response
     */
    override fun afterResponse(point: T, result: GenerateResult): GenerateResult {
        var r = result
        processors.forEach { r = it.afterResponse(point, result) } // TODO 处理 ChatGPT 响应的非法字符
        return r
    }

    override fun onFailure(point: T, t: Throwable): GenerateResult {
        val comment = if (t.message != null) "[AI Code Helper] Fail to generate\nCause: ${t.message}" else failMessage
        return GenerateResult(comment, true)
    }

    override fun afterFailure(point: T, result: GenerateResult): GenerateResult {
        var r = result
        processors.forEach {
            if (isA<FailureProcessor<T>>(it)) {
                it as FailureProcessor<T>
                r = it.afterFailure(point, result)
            }
        }
        return r
    }
}
