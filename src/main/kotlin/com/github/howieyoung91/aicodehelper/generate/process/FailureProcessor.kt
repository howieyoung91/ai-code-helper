/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate.process

import com.github.howieyoung91.aicodehelper.generate.GeneratePoint
import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.intellij.psi.PsiElement

/**
 * @author Howie Young
 * @date 2023/03/15 22:28
 */
interface FailureProcessor<T : PsiElement> {
    fun beforeRequest(point: GeneratePoint<T>): GeneratePoint<T>? = point
    fun afterFailure(point: GeneratePoint<T>, result: GenerateResult) = result
}


