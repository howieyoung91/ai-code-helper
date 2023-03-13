/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * @author Howie Young
 * @date 2023/03/13 00:50
 */
object PSI {
    fun <T> getParent(elem: PsiElement, clazz: Class<T>): T? {
        var e = elem
        while (e !is PsiFile && !clazz.isAssignableFrom(e.javaClass)) {
            e = e.parent
        }
        return if (clazz.isAssignableFrom(e.javaClass)) e as T else null
    }
}