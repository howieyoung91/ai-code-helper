/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.util

/**
 * @author Howie Young
 * @date 2023/03/17 23:26
 */
class StringUtils {
    companion object {
        fun isLetter(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z'
        fun isNumber(c: Char): Boolean = c in '0'..'9'
        fun isWhitespace(c: Char): Boolean = c == ' ' || c == '\t' || c == '\n'

        fun wrap(content: String, limit: Int): String {
            val builder = StringBuilder()
            var curr = 0
            val length = content.length
            while (true) {
                while (curr < length && isWhitespace(content[curr])) {
                    curr++
                }

                var next = curr + limit
                if (next >= length) {
                    break
                }

                do {
                    if (shouldWrap(content[next])) {
                        next++
                    }
                    else {
                        break
                    }
                }
                while (next < length)

                builder.append(content.substring(curr, next), '\n')
                curr = next
            }

            builder.append(content.substring(curr))
            return builder.toString()
        }

        private val C: Set<Char> = object : HashSet<Char>() {
            init {
                add('.')
                add(',')
                add('(')
                add(')')
                add('[')
                add(']')
                add('{')
                add('}')
                add('@')
                add('!')
                add('~')
                add('#')
                add('$')
                add('%')
                add('^')
                add('&')
                add('*')
                add('_')
                add('+')
                add('=')
                add(';')
                add('\'')
                add('"')
                add(':')
                add('<')
                add('>')
                add('\\')
                add('|')
                add('?')
                add('/')
                add('，')
                add('。')
            }
        }

        private fun shouldWrap(c: Char): Boolean = isLetter(c) || isNumber(c) || C.contains(c)
    }
}