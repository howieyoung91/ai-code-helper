package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory

/**
 * @author Howie Young
 * @date 2023/03/09 17:18
 */
class CommentGenerate4MethodsInClassAction : VisibleAction<PsiClass>() {
    override val targetClass: Class<PsiClass> = PsiClass::class.java;

    override fun actionPerformed(e: AnActionEvent) {
        val clazz = threadLocal.get() ?: return
        threadLocal.remove()
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            val factory = PsiElementFactory.getInstance(project) ?: return
            generate4Classes(arrayOf(clazz), factory)
        }
    }

    private fun generate4Classes(classes: Array<out PsiClass>, factory: PsiElementFactory) {
        classes.forEach { clazz ->
            clazz.methods.filterNotNull().forEach { method ->
                JavaDocCommentGenerator.generate(
                    Query {
                        prompt(method.text)
                    },
                    method,
                    factory
                )
            }
        }
    }
}
