/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

interface Generator<T : Point<*>> {
    fun generate(point: T)
}