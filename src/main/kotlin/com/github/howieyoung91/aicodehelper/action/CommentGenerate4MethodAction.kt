package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.generate.Query
import com.github.howieyoung91.aicodehelper.generate.java.JavaDocCommentGenerator
import com.github.howieyoung91.aicodehelper.util.Plugin
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiUtilCore

/**
 * @author Howie Young
 * @date 2023/03/11 17:04
 */
class CommentGenerate4MethodAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        if (Plugin.isAvailable(project)) {
            val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
            val editor = e.getData(CommonDataKeys.EDITOR) ?: return
            val startPos = editor.selectionModel.selectionStart

            var elem = PsiUtilCore.getElementAtOffset(file, startPos)
            while (elem !is PsiFile && elem !is PsiMethod) {
                elem = elem.parent;
            }

            if (elem is PsiMethod) {
                val factory = PsiElementFactory.getInstance(project) ?: return
                JavaDocCommentGenerator.generate(
                    Query {
                        prompt(elem.text)
                    },
                    elem,
                    factory
                )
            }
        }
    }
}