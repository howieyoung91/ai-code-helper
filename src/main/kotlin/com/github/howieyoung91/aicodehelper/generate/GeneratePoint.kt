/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory

/**
 * 生成点
 *
 * @author Howie Young
 * @date 2023/03/14 09:59
 */
interface GeneratePoint<T : PsiElement> {
    val target: T
    val query: Query
    val factory: PsiElementFactory
    operator fun component1(): T = target
    operator fun component2(): Query = query
    operator fun component3(): PsiElementFactory = factory
}

data class DefaultGeneratePoint<T : PsiElement>(
    override val target: T,
    override val query: Query,
    override val factory: PsiElementFactory,
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
