/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.util

/**
 * @author Howie Young
 * @date 2023/03/18 17:29
 */
class Type {
    companion object {
        inline fun <reified T> isA(value: Any) = value is T
    }
}