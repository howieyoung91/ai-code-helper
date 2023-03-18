/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.completion

import com.github.howieyoung91.aicodehelper.ai.AiBased
import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.generate.ElementPoint
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.process.*
import com.github.howieyoung91.aicodehelper.util.PSI
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.javadoc.PsiDocComment

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : CompletionGenerator<ElementPoint<PsiElement>>(), AiBased {
    override val processors: ArrayList<Processor<ElementPoint<PsiElement>>> = ArrayList()

    init {
        processors.add(RequestLimitedProcessor(NoticeRequestingProcessor()))
        processors.add(EscapeProcessor())
        processors.add(WrapCommentProcessor())
    }

    override fun createRequest(point: ElementPoint<PsiElement>): CompletionRequest {
        val request = CompletionRequest(ChatGPT.config.model, point.query.prompt)
        request.maxTokens = ChatGPT.config.maxToken
        request.temperature = ChatGPT.config.temperature
        return request
    }

    override fun onFinal(point: ElementPoint<PsiElement>, result: GenerateResult) {
        val content: String = result.content.ifEmpty { failMessage }
        val (_, target, factory, project) = point

        // do write
        var commentElem: PsiDocComment? = null
        ReadAction.run<Throwable> { // 使用 IDEA 内部线程运行，否则读操作是不被允许的
            commentElem = try {
                factory.createDocCommentFromText("/** $content */", target)
            }
            catch (e: Throwable) {
                e.printStackTrace()
                factory.createDocCommentFromText("/** $failMessage */", target)
            }
        }
        PSI.writeJavadoc(project, target, commentElem!!)
    }

}
