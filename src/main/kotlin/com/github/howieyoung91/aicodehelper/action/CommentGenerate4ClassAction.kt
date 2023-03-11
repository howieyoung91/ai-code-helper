package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.ChatGPTConfig
import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.util.PsiUtilCore

/**
 * @author Howie Young
 * @date 2023/03/09 17:18
 */
class CommentGenerate4ClassAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val instance = ChatGPTConfig.instance
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        if (file is PsiJavaFile) {
            val project = e.project ?: return
            if (Plugin.isAvailable(project)) {
                val startPos = editor.selectionModel.selectionStart
                var elem = PsiUtilCore.getElementAtOffset(file, startPos)
                while (elem !is PsiFile && elem !is PsiClass) {
                    elem = elem.parent
                }

                if (elem is PsiClass) {
                    val factory = PsiElementFactory.getInstance(project) ?: return
                    generate4Classes(arrayOf(elem), factory)
                }
            }
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