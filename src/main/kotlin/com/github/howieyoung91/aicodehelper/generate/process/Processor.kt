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
import com.github.howieyoung91.aicodehelper.generate.java.REQUESTING_MESSAGE
import com.github.howieyoung91.aicodehelper.util.CommentWriter
import com.intellij.psi.PsiElement

/**
 * @author Howie Young
 * @date 2023/03/14 09:58
 */
interface Processor<T : PsiElement> : Ordered {
    fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T> = point
    fun afterResponse(response: GenerateResult) = response
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

    override fun afterResponse(response: GenerateResult): GenerateResult {
        response.content = ChatGPT.config.outputTemplate.replace(OUTPUT_PLACEHOLDER, response.content)
        return response
    }
}

/**
 * 在每次请求前先提示用户正在请求

 * @author Howie Young
 */
class NoticeRequestingProcessor<T : PsiElement> : Processor<T>, PriorityOrdered {
    override val order = Ordered.HIGHEST_PRECEDENCE

    override fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T> {
        val (method, _, factory) = point
        val commentElem = factory.createDocCommentFromText(REQUESTING_MESSAGE, method)
        CommentWriter.writeJavadoc(method.project, method, commentElem)
        return point
    }
}