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

package com.kof22.claritas.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.psi.PsiManager
import com.kof22.claritas.formatter.CommentFormatter
import com.kof22.claritas.intellij.psi.TextBlockExtractor
import com.kof22.claritas.intellij.settings.ClaritasSettings
import com.kof22.claritas.model.CommentType
import com.kof22.claritas.model.FlowerboxStyle

/**
 * Action: Reformat Comment
 *
 * Reformats the comment at the current caret position using flowerbox style.
 *
 * This action:
 * - Detects the comment at caret position
 * - Extracts its content and metadata
 * - Formats it using the core CommentFormatter
 * - Replaces the original comment with formatted version
 * - Supports undo/redo
 */
class ReformatCommentAction : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {
      // ////////////////////////////////////
      // Get editor and project context   //
      // ////////////////////////////////////
      val editor = e.getData(CommonDataKeys.EDITOR)
      if (editor == null) {
         thisLogger().warn("No editor available")
         return
      }

      val project = e.project
      if (project == null) {
         thisLogger().warn("No project available")
         return
      }

      // ////////////////////////////////////
      // Get PSI file                      //
      // ////////////////////////////////////
      val virtualFile =
         com.intellij.openapi.fileEditor.FileDocumentManager
            .getInstance()
            .getFile(editor.document)

      if (virtualFile == null) {
         thisLogger().warn("No file for document")
         return
      }

      val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
      if (psiFile == null) {
         thisLogger().warn("No PSI file available")
         return
      }

      // ////////////////////////////////////
      // Extract text block at caret       //
      // ////////////////////////////////////
      val extraction = TextBlockExtractor.extractBlockAtCaret(editor, psiFile)
      if (extraction == null) {
         thisLogger().info("No text block found at caret position")
         // TODO: Show user notification
         return
      }

      val (commentBlock, replacementRange) = extraction

      // ////////////////////////////////////
      // Get formatting style              //
      // ////////////////////////////////////
      val style = determineStyle(commentBlock.type)

      // ////////////////////////////////////
      // Format the comment                //
      // ////////////////////////////////////
      val formatter = CommentFormatter(style)
      val formatted = formatter.format(commentBlock)

      // ////////////////////////////////////
      // Apply indentation                 //
      // ////////////////////////////////////
      val indentedText =
         if (commentBlock.indentLevel > 0) {
            val indent = " ".repeat(commentBlock.indentLevel)
            formatted.formattedText.lines().joinToString("\n") { line ->
               if (line.isNotEmpty()) indent + line else line
            }
         } else {
            formatted.formattedText
         }

      // ////////////////////////////////////
      // Replace using WriteCommandAction  //
      // ////////////////////////////////////
      WriteCommandAction.runWriteCommandAction(project) {
         editor.document.replaceString(
            replacementRange.startOffset,
            replacementRange.endOffset,
            indentedText
         )
      }

      thisLogger().info("Formatted text block: ${commentBlock.type} at offset ${replacementRange.startOffset}")
   }

   override fun update(e: AnActionEvent) {
      // ////////////////////////////////////////////////////
      // Enable only when editor is available and enabled //
      // ////////////////////////////////////////////////////
      val editor = e.getData(CommonDataKeys.EDITOR)
      val project = e.project
      val settings = ClaritasSettings.getInstance()

      e.presentation.isEnabled = editor != null &&
         project != null &&
         settings.state.enableClaritas
   }

   /**
    * Determine the appropriate style based on comment type.
    */
   private fun determineStyle(type: CommentType): FlowerboxStyle =
      when (type) {
         CommentType.JAVADOC -> FlowerboxStyle.JAVADOC_DEFAULT
         CommentType.INLINE -> FlowerboxStyle.INLINE_DEFAULT
         CommentType.BLOCK,
         CommentType.LINE ->
            FlowerboxStyle(
               borderChar = '*',
               linePrefix = " ** ",
               fixedWidth = 80,
               useJavadocStyle = false
            )
      }
}
