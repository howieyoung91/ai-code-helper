/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.process

import java.util.*

interface Ordered {
    val order: Int

    companion object {
        const val HIGHEST_PRECEDENCE = Int.MIN_VALUE
        const val LOWEST_PRECEDENCE = Int.MAX_VALUE
    }
}

interface PriorityOrdered : Ordered

class OrderComparator : Comparator<Any?> {
    override fun compare(o1: Any?, o2: Any?): Int {
        if (o1 == null) {
            throw NullPointerException()
        }
        if (o2 == null) {
            throw NullPointerException()
        }
        val p1 = o1 is PriorityOrdered
        val p2 = o2 is PriorityOrdered
        if (p1 && !p2) { // 优先级更高 返回 -1
            return -1
        }
        else if (!p1 && p2) {
            return 1
        }
        return getOrder(o1).compareTo(getOrder(o2))
    }

    private fun getOrder(o: Any): Int {
        return if (o is Ordered) o.order else Ordered.LOWEST_PRECEDENCE / 2
    }

    companion object {
        private val INSTANCE = OrderComparator()
        fun sort(list: List<*>) {
            if (list.size > 1) {
                Collections.sort(list, INSTANCE)
            }
        }

        fun sort(array: Array<*>) {
            if (array.size > 1) {
                Arrays.sort(array, INSTANCE)
            }
        }

        fun sortIfNecessary(value: Any?) {
            if (value is Array<*>) {
                sort(value)
            }
            else if (value is List<*>) {
                sort(value)
            }
        }
    }
}