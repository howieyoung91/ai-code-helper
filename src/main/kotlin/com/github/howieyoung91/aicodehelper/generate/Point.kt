/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory

interface Point<T> {
    val query: Query
    val target: T
    val key: String
}

/**
 * 生成点
 *
 * @author Howie Young
 * @date 2023/03/14 09:59
 */
interface ElementGeneratePoint<T : PsiElement> : Point<T> {
    val factory: PsiElementFactory
    val project: Project
    operator fun component1() = query
    operator fun component2() = target
    operator fun component3() = factory
    operator fun component4() = project
}

data class StringGeneratePoint(
    override val query: Query,
    override val target: String,
    val start: Int,
    val end: Int,
) : Point<String> {
    override val key: String = target
}

data class DefaultGeneratePoint<T : PsiElement>(
    override val query: Query,
    override val target: T,
    override val factory: PsiElementFactory,
    override val project: Project,
) : ElementGeneratePoint<T> {
    override val key = target.toString()
}
