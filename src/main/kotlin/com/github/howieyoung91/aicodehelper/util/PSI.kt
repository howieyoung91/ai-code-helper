/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.util

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaDocumentedElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.javadoc.PsiDocComment

/**
 * @author Howie Young
 * @date 2023/03/13 00:50
 */
object PSI {
    fun <T> getParent(elem: PsiElement, clazz: Class<T>): T? {
        var e = elem
        while (e !is PsiFile && !clazz.isAssignableFrom(e.javaClass)) {
            e = e.parent
        }
        return if (clazz.isAssignableFrom(e.javaClass)) e as T else null
    }

    private val log = Logger.getInstance(PSI::class.java)

    /**
     * 对某一个元素添加文档注释
     *
     * @param target  要添加注释的元素
     * @param comment 注释
     *
     * @author Howie Young
     */
    @JvmStatic
    fun writeJavadoc(project: Project, target: PsiElement, comment: PsiDocComment) {
        try {
            WriteCommandAction.runWriteCommandAction(project) {
                if (target.containingFile != null && target is PsiJavaDocumentedElement) {
                    doWriteJavaDoc(target, comment)
                    reformat(project, target)
                }
            }
        }
        catch (throwable: Throwable) {
            log.error("Fail to write Java Doc. Cause: ", throwable)
        }
    }

    private fun doWriteJavaDoc(elem: PsiJavaDocumentedElement, comment: PsiDocComment) {
        val docComment = elem.docComment
        if (docComment == null) {
            elem.node.addChild(comment.node, elem.firstChild.node)
        }
        else {
            docComment.replace(comment)
        }
    }

    /**
     * 格式化文档
     *
     * @author Howie Young
     */
    fun reformat(project: Project, elem: PsiElement) {
        val javadocElement = elem.firstChild
        val startOffset = javadocElement.textOffset
        val endOffset = javadocElement.textOffset + javadocElement.text.length
        reformat(elem.containingFile, project, startOffset, endOffset)
    }

    fun reformat(psiFile: PsiFile?, project: Project, startOffset: Int, endOffset: Int) {
        if (psiFile == null) {
            return
        }
        val manager = CodeStyleManager.getInstance(project) ?: return
        manager.reformatText(psiFile, startOffset, endOffset + 1)
    }

    fun reformat(
        project: Project,
        document: Document,
        selectionModel: SelectionModel,
        content: String,
        file: PsiFile,
    ) {
        // WriteCommandAction.runWriteCommandAction(project) {
        document.replaceString(selectionModel.selectionStart, selectionModel.selectionEnd, content)
        reformat(file, project, selectionModel.selectionStart, selectionModel.selectionEnd)
        // }
    }
}