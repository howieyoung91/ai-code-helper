package com.github.howieyoung91.aicodehelper.util

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project

/**
 * @author Howie Young
 * @date 2023/03/11 17:50
 */
object Plugin {
    fun isAvailable(project: Project?): Boolean {
        if (project == null) {
            return false;
        }
        val dumbService = DumbService.getInstance(project)
        if (dumbService.isDumb) {
            dumbService.showDumbModeNotification("AI Code Helper isn't available during indexing.")
            return false
        }
        return true
    }
}