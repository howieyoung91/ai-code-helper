/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.generate.edit

import com.github.howieyoung91.aicodehelper.generate.GenerateResult
import com.github.howieyoung91.aicodehelper.generate.SelectionPoint
import com.github.howieyoung91.aicodehelper.generate.process.Processor
import com.github.howieyoung91.aicodehelper.util.PSI
import com.github.howieyoung91.chatgpt.client.edit.EditRequest
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager


/**
 * @author Howie Young
 * @date 2023/03/18 14:38
 */
object OptimizedCodeGenerator : EditGenerator<SelectionPoint>() {
    override val processors: ArrayList<Processor<SelectionPoint>> = ArrayList()

    override fun createRequest(point: SelectionPoint): EditRequest {
        val request = EditRequest(EditRequest.Model.CODE_DAVINCI_EDIT_001.modelName, "optimized code")
        request.input = point.target.selectedText
        return request
    }

    override fun onFinal(point: SelectionPoint, result: GenerateResult) {
        val content: String = result.content.ifEmpty { failMessage }
        var document: Document? = null
        var target: SelectionModel? = null
        var project: Project? = null
        var file: PsiFile? = null
        ReadAction.run<Throwable> {
            target = point.target
            document = target!!.editor.document
            project = point.target.editor.project
            file = PsiManager.getInstance(project!!).findFile(project!!.projectFile!!)
        }
        if (document == null || target == null || project == null) {
            return
        }
        PSI.reformat(project!!, document!!, target!!, content, file!!)
    }
}