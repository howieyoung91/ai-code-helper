/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate

import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod


/**
 * @author Howie Young
 * @date 2023/03/09 18:14
 */
interface CodeGenerator {
    fun generate(query: Query, method: PsiMethod, factory: PsiElementFactory)
}

class Query(text: String = "") {
    var prompt: String = text
        private set

    fun prompt(prompt: String) {
        this.prompt = prompt
    }
}

inline fun Query(init: Query.() -> Unit): Query {
    val query = Query("")
    query.init()
    return query
}
