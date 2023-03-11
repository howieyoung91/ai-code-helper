package com.github.howieyoung91.aicodehelper.generate.java

import com.github.howieyoung91.aicodehelper.ai.AiBased
import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPTRequest
import com.github.howieyoung91.aicodehelper.ai.chatgpt.JSON
import com.github.howieyoung91.aicodehelper.generate.CommentGenerator
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.util.CommentWriter
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : CommentGenerator, AiBased {
    private val log = Logger.getInstance(JavaDocCommentGenerator::class.java)

    override fun generate(query: Query, method: PsiMethod, factory: PsiElementFactory) {
        noticeRequesting(factory, method)
        ChatGPT.client.complete(
            ChatGPTRequest {
                // TODO prompt 支持模板
                prompt("以下代码的具体逻辑是什么？\n ${query.prompt}".replace("\n", "\\n").replace("\"", "\\\""))
                callback {
                    onFail = {
                        writeJavaDocComment(factory, method, this.message ?: "[AI Code Helper] Fail to generate.")
                    }
                    onResponse = onResponse@{
                        val jsonBody = JSON.fromJson(this.content)
                        log.info(content)
                        if (jsonBody == null) {
                            writeJavaDocComment(factory, method, "")
                            return@onResponse
                        }

                        val choice = jsonBody.choices.firstOrNull()
                        // TODO handle error message
                        writeJavaDocComment(factory, method, choice?.text ?: "")
                    }
                }
            }
        )
    }

    private fun noticeRequesting(factory: PsiElementFactory, method: PsiMethod) {
        val commentElem = factory.createDocCommentFromText("/** requesting, just a moment. */", method)
        CommentWriter.writeJavadoc(method, commentElem)
    }

    private fun writeJavaDocComment(factory: PsiElementFactory, method: PsiMethod, comment: String) {
        var c: String = comment
        if (comment.isEmpty()) {
            c = "[AI Code Helper] Fail to generate";
        }
        val commentElem = factory.createDocCommentFromText("/** $c */", method)
        CommentWriter.writeJavadoc(method, commentElem)
    }
}