package com.github.howieyoung91.commentwriter.generate.java

import com.github.howieyoung91.commentwriter.ai.chatgpt.ChatGPT
import com.github.howieyoung91.commentwriter.ai.chatgpt.ChatGPTCallback
import com.github.howieyoung91.commentwriter.ai.chatgpt.ChatGPTRequest
import com.github.howieyoung91.commentwriter.generate.CommentGenerator
import com.github.howieyoung91.commentwriter.generate.Query
import com.github.howieyoung91.commentwriter.transport.JSON
import com.github.howieyoung91.commentwriter.util.CommentWriter
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : CommentGenerator {
    override fun generate(query: Query, method: PsiMethod, factory: PsiElementFactory): String {
        val commentElem = factory.createDocCommentFromText("/** requesting, just a moment. */", method)
        CommentWriter.writeJavadoc(method, commentElem)
        ChatGPT.client.complete(
            ChatGPTRequest(
                "以下代码的具体逻辑是什么？\n ${query.prompt}".replace("\n", "\\n").replace("\"", "\\\""),
                ChatGPTCallback {
                    val jsonResponse = JSON.fromJson(this.content)
                    var comment = ""
                    if (jsonResponse!!.choices.isNotEmpty()) {
                        comment = jsonResponse.choices.get(0).text
                    }

                    val commentElem = factory.createDocCommentFromText("/** $comment */", method)
                    CommentWriter.writeJavadoc(method, commentElem)
                }
            )
        )
        return query.prompt
    }
}