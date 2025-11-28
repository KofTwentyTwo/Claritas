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

package com.kof22.claritas.intellij.psi

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.kof22.claritas.model.CommentBlock
import com.kof22.claritas.model.CommentType

/**
 * Extracts text blocks from the editor for formatting.
 *
 * Can extract:
 * - Existing comments (line, block, javadoc)
 * - Raw text lines (will be converted to comments)
 * - Multi-line text blocks
 */
object TextBlockExtractor {
   /**
    * Extract text block at the current caret position.
    *
    * Strategy:
    * 1. Check if caret is in an existing comment - use that
    * 2. Otherwise, extract the current line
    * 3. If selection exists, use selected text
    *
    * @return Pair of CommentBlock and TextRange for replacement
    */
   fun extractBlockAtCaret(
      editor: Editor,
      psiFile: PsiFile
   ): Pair<CommentBlock, TextRange>? {
      val document = editor.document
      val selectionModel = editor.selectionModel

      // /////////////////////////////////////////////////////////
      // Case 1: User has selected text - use selection       //
      // /////////////////////////////////////////////////////////
      if (selectionModel.hasSelection()) {
         return extractSelectedBlock(selectionModel, document)
      }

      val offset = editor.caretModel.offset
      val element = psiFile.findElementAt(offset)

      // /////////////////////////////////////////////////////////
      // Case 2: Caret is in a comment - extract comment      //
      // /////////////////////////////////////////////////////////
      val comment = findComment(element)
      if (comment != null) {
         return extractCommentBlock(comment, document)
      }

      // /////////////////////////////////////////////////////////
      // Case 3: Extract current line as raw text             //
      // /////////////////////////////////////////////////////////
      return extractCurrentLine(offset, document)
   }

   /**
    * Extract selected text as a block.
    */
   private fun extractSelectedBlock(
      selectionModel: com.intellij.openapi.editor.SelectionModel,
      document: Document
   ): Pair<CommentBlock, TextRange> {
      val startOffset = selectionModel.selectionStart
      val endOffset = selectionModel.selectionEnd
      val selectedText = selectionModel.selectedText ?: ""

      val startLine = document.getLineNumber(startOffset)
      val lineStartOffset = document.getLineStartOffset(startLine)
      val indent = calculateIndentFromOffset(document, lineStartOffset, startOffset)

      val block =
         CommentBlock(
            rawText = selectedText,
            type = detectCommentType(selectedText),
            indentLevel = indent,
            preserveParagraphs = true
         )

      return Pair(block, TextRange(startOffset, endOffset))
   }

   /**
    * Extract an existing comment.
    */
   private fun extractCommentBlock(
      comment: PsiComment,
      document: Document
   ): Pair<CommentBlock, TextRange> {
      val text = comment.text
      val type = detectCommentType(text)
      val indent = calculateIndent(comment, document)

      val block =
         CommentBlock(
            rawText = text,
            type = type,
            indentLevel = indent,
            preserveParagraphs = true
         )

      return Pair(block, comment.textRange)
   }

   /**
    * Extract the current line as raw text.
    */
   private fun extractCurrentLine(
      offset: Int,
      document: Document
   ): Pair<CommentBlock, TextRange> {
      val lineNumber = document.getLineNumber(offset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)
      val lineEndOffset = document.getLineEndOffset(lineNumber)

      val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
      val indent = lineText.takeWhile { it == ' ' }.length
      val trimmedText = lineText.trim()

      val block =
         CommentBlock(
            rawText = trimmedText,
            type = CommentType.LINE, // Will be converted to comment
            indentLevel = indent,
            preserveParagraphs = false
         )

      return Pair(block, TextRange(lineStartOffset, lineEndOffset))
   }

   /**
    * Find a comment element starting from the given PSI element.
    */
   private fun findComment(element: PsiElement?): PsiComment? {
      if (element == null) return null

      // /////////////////////////////////////////////
      // Check if the element itself is a comment //
      // /////////////////////////////////////////////
      if (element is PsiComment) {
         return element
      }

      // /////////////////////////////////////////
      // Check if the parent is a comment      //
      // /////////////////////////////////////////
      val parent = element.parent
      if (parent is PsiComment) {
         return parent
      }

      // ///////////////////////////////////////////////////////////
      // If we're in whitespace, check previous sibling comment //
      // ///////////////////////////////////////////////////////////
      if (element is PsiWhiteSpace) {
         val prevSibling = PsiTreeUtil.skipWhitespacesBackward(element)
         if (prevSibling is PsiComment) {
            return prevSibling
         }
      }

      return null
   }

   /**
    * Detect the type of comment from text.
    */
   private fun detectCommentType(text: String): CommentType =
      when {
         text.startsWith("/**") -> CommentType.JAVADOC
         text.startsWith("/*") -> CommentType.BLOCK
         text.startsWith("//") -> CommentType.LINE
         else -> CommentType.LINE // Raw text becomes line comment
      }

   /**
    * Calculate indentation from PSI comment element.
    */
   private fun calculateIndent(
      comment: PsiComment,
      document: Document
   ): Int {
      val startOffset = comment.textRange.startOffset
      val lineNumber = document.getLineNumber(startOffset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)

      return calculateIndentFromOffset(document, lineStartOffset, startOffset)
   }

   /**
    * Calculate indentation from offsets.
    */
   private fun calculateIndentFromOffset(
      document: Document,
      lineStartOffset: Int,
      contentStartOffset: Int
   ): Int {
      val lineText = document.getText(TextRange(lineStartOffset, contentStartOffset))
      return lineText.takeWhile { it == ' ' }.length
   }
}
