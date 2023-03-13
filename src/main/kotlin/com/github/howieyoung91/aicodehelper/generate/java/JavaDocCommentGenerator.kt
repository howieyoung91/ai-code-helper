/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.java

import com.github.howieyoung91.aicodehelper.ai.AiBased
import com.github.howieyoung91.aicodehelper.ai.chatgpt.ChatGPT
import com.github.howieyoung91.aicodehelper.config.OUTPUT_PLACEHOLDER
import com.github.howieyoung91.aicodehelper.config.PROMPT_PLACEHOLDER
import com.github.howieyoung91.aicodehelper.generate.CommentGenerator
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.util.CommentWriter
import com.github.howieyoung91.chatgpt.client.completion.CompletionRequest
import com.github.howieyoung91.chatgpt.client.completion.CompletionResponse
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import com.intellij.psi.javadoc.PsiDocComment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val FAIL_MESSAGE = "[AI Code Helper] Fail to generate."
private const val REQUESTING_MESSAGE = "/** [AI Code Helper] requesting, just a moment. */"

/**
 * @author Howie Young
 * @date 2023/03/09 18:16
 */
object JavaDocCommentGenerator : CommentGenerator, AiBased {
    override fun generate(query: Query, method: PsiMethod, factory: PsiElementFactory) {
        noticeRequesting(factory, method)

        val request = createRequest(query)
        ChatGPT.client.complete(request,
            object : Callback<CompletionResponse> {
                override fun onResponse(call: Call<CompletionResponse>, response: Response<CompletionResponse>) {
                    val comment = determineComment(response)
                    val resolved = ChatGPT.config.outputTemplate.replace(OUTPUT_PLACEHOLDER, comment)
                    writeJavaDocComment(factory, method, resolved)
                }

                override fun onFailure(call: Call<CompletionResponse>, t: Throwable) {
                    val comment = if (t.message != null) "[AI Code Helper] ${t.message}" else FAIL_MESSAGE
                    writeJavaDocComment(factory, method, comment)
                }
            }
        )
    }
}

private fun createRequest(query: Query): CompletionRequest {
    val resolvedPrompt =
        ChatGPT.config.promptTemplate.replace(
            PROMPT_PLACEHOLDER,
            query.prompt.replace("\n", "\\n").replace("\"", "\\\"")
        )
    val request = CompletionRequest(ChatGPT.config.model, resolvedPrompt)
    request.maxTokens = ChatGPT.config.maxToken
    request.temperature = ChatGPT.config.temperature
    return request
}

private fun noticeRequesting(factory: PsiElementFactory, method: PsiMethod) {
    val commentElem = factory.createDocCommentFromText(REQUESTING_MESSAGE, method)
    CommentWriter.writeJavadoc(method.project, method, commentElem)
}

private fun determineComment(response: Response<CompletionResponse>): String {
    var comment = ""
    if (response.code() != 200) {
        val errorBody = response.errorBody()
        if (errorBody != null) {
            comment = "[AI Code Helper] ${errorBody.string()}"
        }
    }
    else {
        val body = response.body()
        if (body != null && body.choices.isNotEmpty()) {
            comment = body.choices[0].text
        }
    }
    return comment
}

private fun writeJavaDocComment(factory: PsiElementFactory, method: PsiMethod, comment: String?) {
    var c: String? = comment
    if (c.isNullOrEmpty()) {
        c = FAIL_MESSAGE
    }

    var commentElem: PsiDocComment? = null
    var project: Project? = null
    ReadAction.run<Throwable> { // 使用 IDEA 内部线程运行，否则读操作是不被允许的
        try {
            project = method.project
            commentElem = factory.createDocCommentFromText("/** $c */", method)
        }
        catch (e: Throwable) {
            // TODO 处理 ChatGPT 响应的非法字符
            e.printStackTrace()
            commentElem = factory.createDocCommentFromText("/** $FAIL_MESSAGE */", method)
        }
    }
    if (project == null || commentElem == null) {
        return
    }
    CommentWriter.writeJavadoc(project!!, method, commentElem!!)
}