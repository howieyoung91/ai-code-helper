/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

class Query(text: String = "") {
    var prompt: String = text

    fun prompt(prompt: String) {
        this.prompt = prompt
    }
}

inline fun Query(init: Query.() -> Unit): Query {
    val query = Query("")
    query.init()
    return query
}

data class GenerateResult(
    var content: String,
    var isError: Boolean,
)
