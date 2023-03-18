/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.generate.process

import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.Point

/**
 * @author Howie Young
 * @date 2023/03/15 22:28
 */
interface FailureProcessor<T : Point<*>> {
    fun beforeRequest(point: T): T? = point
    fun afterFailure(point: T, result: GenerateResult) = result
}


