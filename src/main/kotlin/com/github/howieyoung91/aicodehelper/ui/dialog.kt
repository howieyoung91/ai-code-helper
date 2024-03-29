/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */

package com.github.howieyoung91.aicodehelper.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class Dialog constructor(
    val project: Project,
    p: DialogWrapper.() -> JComponent,
) : DialogWrapper(project) {
    private var panel: JComponent = p(this)

    init {
        init()
    }

    override fun createCenterPanel() = panel
}
