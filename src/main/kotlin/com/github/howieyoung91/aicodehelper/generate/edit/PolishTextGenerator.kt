/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.edit

import com.github.howieyoung91.aicodehelper.generate.ElementPoint
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.process.NoticeRequestingProcessor
import com.github.howieyoung91.aicodehelper.generate.process.Processor
import com.github.howieyoung91.aicodehelper.generate.process.RequestLimitedProcessor
import com.github.howieyoung91.aicodehelper.generate.process.WrapCommentProcessor
import com.github.howieyoung91.chatgpt.client.edit.EditRequest
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiComment
import com.intellij.psi.javadoc.PsiDocComment


/**
 * TODO
 *
 * @author Howie Young
 * @date 2023/03/18 14:38
 */
object PolishTextGenerator : EditGenerator<ElementPoint<PsiComment>>() {
    override val processors: ArrayList<Processor<ElementPoint<PsiComment>>> = ArrayList()

    init {
        processors.add(RequestLimitedProcessor(NoticeRequestingProcessor()))
        processors.add(WrapCommentProcessor())
    }

    override fun createRequest(point: ElementPoint<PsiComment>): EditRequest {
        val request = EditRequest(EditRequest.Model.TEXT_DAVINCI_EDIT_001.modelName, "polish this text")
        request.input = point.query.prompt
        val target = point.target
        replaceComment(target, NoticeRequestingProcessor.REQUESTING_MESSAGE, point)
        return request
    }

    override fun onFinal(point: ElementPoint<PsiComment>, result: GenerateResult) {
        val content: String = result.content.ifEmpty { failMessage }
        val target = point.target

        // do write
        replaceComment(target, content.replace('\n', ' '), point)
    }

    private fun replaceComment(
        target: PsiComment,
        content: String,
        point: ElementPoint<PsiComment>,
    ) {
        WriteCommandAction.runWriteCommandAction(point.project) {
            runUndoTransparentWriteAction {
                try {
                    val notification = when {
                        target is PsiDocComment -> "/** $content */"
                        target.tokenType == JavaTokenType.C_STYLE_COMMENT -> "/* $content */"
                        else -> "// $content"
                    }
                    println(123)
                    target.replace(
                        when (target) {
                            is PsiDocComment -> point.factory.createDocCommentFromText(notification, target)
                            else -> point.factory.createCommentFromText(notification, target)
                        }
                    )
                }
                catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
}