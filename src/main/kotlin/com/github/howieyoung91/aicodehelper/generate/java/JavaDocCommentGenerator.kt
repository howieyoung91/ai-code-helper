/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.java

import com.github.howieyoung91.aicodehelper.ai.AiBased
import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.generate.ChatGPTAsyncGenerator
import com.github.howieyoung91.aicodehelper.generate.GeneratePoint
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.util.CommentWriter
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.javadoc.PsiDocComment

const val FAIL_MESSAGE = "[AI Code Helper] Fail to generate."
const val REQUESTING_MESSAGE = "/** [AI Code Helper] requesting, just a moment. */"

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : ChatGPTAsyncGenerator<PsiMethod>(), AiBased {
    override fun createRequest(point: GeneratePoint<PsiMethod>): CompletionRequest {
        val request = CompletionRequest(ChatGPT.config.model, point.query.prompt)
        request.maxTokens = ChatGPT.config.maxToken
        request.temperature = ChatGPT.config.temperature
        return request
    }

    override fun onFinal(point: GeneratePoint<PsiMethod>, result: GenerateResult) {
        val content: String = result.content.ifEmpty { FAIL_MESSAGE }
        val target = point.target
        val factory = point.factory
        var commentElem: PsiDocComment? = null
        var project: Project? = null

        // do write
        ReadAction.run<Throwable> { // 使用 IDEA 内部线程运行，否则读操作是不被允许的
            try {
                project = target.project
                commentElem = factory.createDocCommentFromText("/** $content */", target)
            }
            catch (e: Throwable) {
                e.printStackTrace()
                commentElem = factory.createDocCommentFromText("/** $FAIL_MESSAGE */", target)
            }
        }
        if (project == null || commentElem == null) {
            return
        }
        CommentWriter.writeJavadoc(project!!, target, commentElem!!)
    }
}
