/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory

/**
 * 生成点
 *
 * @author Howie Young
 * @date 2023/03/14 09:59
 */
interface GeneratePoint<T : PsiElement> {
    val query: Query
    val target: T
    val factory: PsiElementFactory
    val project: Project

    operator fun component1() = query
    operator fun component2() = target
    operator fun component3() = factory
    operator fun component4() = project
}

data class DefaultGeneratePoint<T : PsiElement>(
    override val query: Query,
    override val target: T,
    override val factory: PsiElementFactory,
    override val project: Project,
) : GeneratePoint<T>

class Query(text: String = "") {
    var prompt: String = text

    fun prompt(prompt: String) {
        this.prompt = prompt
    }
}

inline fun Query(init: Query.() -> Unit): Query {
    val query = Query("")
    query.init()
    return query
}

data class GenerateResult(
    var content: String,
    var isError: Boolean,
)
