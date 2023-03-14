/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.intellij.psi.PsiElement


/**
 * @author Howie Young
 * @date 2023/03/09 18:14
 */
interface CodeGenerator<T : PsiElement> {
    fun generate(point: GeneratePoint<T>)
}
