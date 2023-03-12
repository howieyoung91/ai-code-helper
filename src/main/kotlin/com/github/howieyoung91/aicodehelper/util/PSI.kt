package com.github.howieyoung91.aicodehelper.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * @author Howie Young
 * @date 2023/03/13 00:50
 */

object PSI {
    inline fun <reified T> getParent(elem: PsiElement): T? {
        var e = elem
        while (e !is PsiFile && e !is T) {
            e = e.parent
        }

        return if (e is T) e else null
    }
}