package com.github.howieyoung91.commentwriter.generate


/**
 * @author Howie Young
 * @date 2023/03/09 18:14
 */
interface CodeGenerator {
    fun generate(query: Query): String
}

class Query(var text: String)

fun Query(init: Query.() -> Unit): Query {
    val query = Query("")
    query.init()
    return query
}

fun Query.text(text: String): String {
    this.text += text
    return text
}
