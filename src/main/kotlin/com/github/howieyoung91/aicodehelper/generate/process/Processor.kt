/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.process

import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.config.OUTPUT_PLACEHOLDER
import com.github.howieyoung91.aicodehelper.config.PROMPT_PLACEHOLDER
import com.github.howieyoung91.aicodehelper.generate.GeneratePoint
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.util.CommentWriter
import com.github.howieyoung91.aicodehelper.util.StringUtils
import com.intellij.psi.PsiElement
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Howie Young
 * @date 2023/03/14 09:58
 */
interface Processor<T : PsiElement> : Ordered {
    fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T>? = point
    fun afterResponse(point: GeneratePoint<T>, result: GenerateResult) = result
}

/**
 * 过滤掉一些转义字符

 * @author Howie Young
 */
class EscapeProcessor<T : PsiElement> : Processor<T> {
    override val order = Ordered.HIGHEST_PRECEDENCE

    override fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T> {
        val query = point.query
        query.prompt = ChatGPT.config.promptTemplate.replace(PROMPT_PLACEHOLDER, query.prompt)
        query.prompt = query.prompt.replace("\n", "\\n").replace("\"", "\\\"")
        return point
    }

    override fun afterResponse(point: GeneratePoint<T>, result: GenerateResult): GenerateResult {
        result.content = ChatGPT.config.outputTemplate.replace(OUTPUT_PLACEHOLDER, result.content)
        return result
    }
}

/**
 * 在每次请求前先提示用户正在请求

 * @author Howie Young
 */
class NoticeRequestingProcessor<T : PsiElement> : Processor<T>, PriorityOrdered {
    companion object {
        private const val REQUESTING_MESSAGE = "/** [AI Code Helper] Requesting, just a moment. */"
    }

    override val order = Ordered.HIGHEST_PRECEDENCE

    override fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T> {
        val commentElem = point.factory.createDocCommentFromText(REQUESTING_MESSAGE, point.target)
        CommentWriter.writeJavadoc(point.project, point.target, commentElem)
        return point
    }
}

/**
 * 对方法请求限流
 */
class RequestLimitedProcessor<T : PsiElement>
    (private val processor: NoticeRequestingProcessor<T> = NoticeRequestingProcessor()) :
    Processor<T> by processor,
    FailureProcessor<T> {
    companion object {
        private const val LIMITED_MESSAGE = "/** [AI Code Helper] Already requested! Waiting for Response. */"
    }

    private val cache = ConcurrentHashMap<String, Int>()

    override fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T>? {
        val key = point.key
        if (cache.containsKey(key)) {
            val commentElem = point.factory.createDocCommentFromText(LIMITED_MESSAGE, point.target)
            CommentWriter.writeJavadoc(point.project, point.target, commentElem)
            return null
        }
        cache[key] = 0
        return processor.beforeRequest(point)
    }

    override fun afterResponse(point: GeneratePoint<T>, result: GenerateResult): GenerateResult {
        cache.remove(point.key)
        return result
    }

    override fun afterFailure(point: GeneratePoint<T>, result: GenerateResult): GenerateResult {
        cache.remove(point.key)
        return result
    }
}

class WrapCommentProcessor<T : PsiElement>(val limit: Int = 60) : Processor<T> {
    override val order = Ordered.LOWEST_PRECEDENCE
    override fun afterResponse(point: GeneratePoint<T>, result: GenerateResult): GenerateResult {
        val content = result.content
        if (content.isEmpty()) {
            return result
        }

        val toString = StringUtils.wrap(content, limit)
        result.content = toString
        return result
    }
}
