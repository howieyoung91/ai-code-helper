/*
 * Copyright ©2023 Howie Young
 * Licensed under the GPL version 3
 */
package com.github.howieyoung91.aicodehelper.action

import com.github.howieyoung91.aicodehelper.ai.chatgpt.config.COMPLETION_MODELS
import com.github.howieyoung91.aicodehelper.ui.Dialog
import com.github.howieyoung91.aicodehelper.ui.DocumentEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.dsl.builder.panel
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.*
import javax.swing.DefaultComboBoxModel

/**
 * TODO
 * @author Howie Young
 * @date 2023/03/20 19:05
 */
class CustomizeTemplateAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        CustomizeTemplateDialog(project, e).show()
    }
}

private val maps by lazy {
    LinkedHashMap<String, Vector<String>>().apply {
        put("Completion", Vector<String>().apply {
            COMPLETION_MODELS.forEach { add(it) }
        })
        put("Edit", Vector(arrayListOf("code", "text")))
    }
}

private val actions = maps.keys.toList()
private var currentAction = actions[0] // aka "Completion"
private fun CustomizeTemplateDialog(project: Project, e: AnActionEvent) =
    Dialog(project) {
        panel {
            row {
                val actionSelector = comboBox(actions).applyToComponent {
                    selectedItem = currentAction
                }.comment("Actions you want")
                val modelSelector = comboBox(maps[currentAction]!!).comment("Models supported")
                actionSelector.applyToComponent {
                    addItemListener {
                        currentAction = it.item.toString()
                        modelSelector.applyToComponent {
                            setModel(DefaultComboBoxModel(maps[currentAction]!!))
                        }
                    }
                }
                textField().comment("Write instruction")
            }
            row {
                label("Input:")
            }
            row {
                cell(
                    NonOpaquePanel(BorderLayout()).apply {
                        DocumentEditor(determineVirginContent(e)) {
                            settings.isLineNumbersShown = false
                        }
                    }
                ).applyToComponent {
                    preferredSize = Dimension(800, 400)
                }.comment("Write your own template please")
            }
            row {
                label("Output:")
            }
            row {
                cell(
                    NonOpaquePanel(BorderLayout()).apply {
                        DocumentEditor {
                            settings.isLineNumbersShown = false
                        }
                    }
                ).applyToComponent {
                    preferredSize = Dimension(800, 400)
                }.comment("Response will be shown here")
            }
            row {
            }
        }
    }.apply {
        title = "Customize Template"
        isResizable = false
    }

private fun determineVirginContent(e: AnActionEvent) =
    e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText ?: ""