/*
 * All Rights Reserved
 *
 * Copyright (c) 2025. Kof22
 *
 * THE CONTENTS OF THIS PROJECT ARE PROPRIETARY AND CONFIDENTIAL.
 * UNAUTHORIZED COPYING, TRANSFERRING, OR REPRODUCTION OF ANY PART OF THIS PROJECT, VIA ANY MEDIUM, IS STRICTLY PROHIBITED.
 *
 * The receipt or possession of the source code and/or any parts thereof does not convey or imply any right to use them
 * for any purpose other than the purpose for which they were provided to you.
 *
 *
 *
 */

package com.kof22.claritas.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.TextRange

/**
 * POC Action: Format Current Line
 *
 * This is a simple proof-of-concept action that demonstrates the plugin mechanics.
 * It replaces the current line with "// CLARITAS: [original text]"
 *
 * This action proves:
 * - Plugin action registration works
 * - Editor context access works
 * - Document modification works
 * - Undo/redo functionality works
 */
class FormatCurrentLineAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      val editor =
         e.getData(CommonDataKeys.EDITOR) ?: run {
            thisLogger().warn("No editor available")
            return
         }

      val project =
         e.project ?: run {
            thisLogger().warn("No project available")
            return
         }

      val document = editor.document
      val caretModel = editor.caretModel
      val currentOffset = caretModel.offset

      // Get the current line number and offsets
      val lineNumber = document.getLineNumber(currentOffset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)
      val lineEndOffset = document.getLineEndOffset(lineNumber)

      // Read the current line text
      val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

      // Create the replacement text
      val replacementText = "// CLARITAS: $lineText"

      // Replace the line using WriteCommandAction for proper undo support
      WriteCommandAction.runWriteCommandAction(project) {
         document.replaceString(lineStartOffset, lineEndOffset, replacementText)
      }

      thisLogger().info("Formatted line $lineNumber: '$lineText' -> '$replacementText'")
   }

   override fun update(e: AnActionEvent) {
      // Enable the action only when an editor is available
      val editor = e.getData(CommonDataKeys.EDITOR)
      val project = e.project
      e.presentation.isEnabled = editor != null && project != null
   }
}
