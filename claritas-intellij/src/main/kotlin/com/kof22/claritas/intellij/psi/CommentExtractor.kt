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
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.kof22.claritas.model.CommentBlock
import com.kof22.claritas.model.CommentType

/**
 * Extracts comment information from IntelliJ PSI tree.
 */
object CommentExtractor {
   /**
    * Extract comment at the current caret position.
    *
    * @param editor The editor
    * @param psiFile The PSI file
    * @return CommentBlock if a comment is found, null otherwise
    */
   fun extractCommentAtCaret(
      editor: Editor,
      psiFile: PsiFile
   ): CommentBlock? {
      val offset = editor.caretModel.offset
      val element = psiFile.findElementAt(offset) ?: return null

      // /////////////////////////////////////////////
      // Find the comment element at or near caret //
      // /////////////////////////////////////////////
      val comment = findComment(element) ?: return null

      return convertToCommentBlock(comment, editor.document)
   }

   /**
    * Find a comment element starting from the given PSI element.
    *
    * Looks at the current element and its parent.
    */
   private fun findComment(element: PsiElement): PsiComment? {
      // //////////////////////////////////////////////
      // Check if the element itself is a comment   //
      // //////////////////////////////////////////////
      if (element is PsiComment) {
         return element
      }

      // ///////////////////////////////////////
      // Check if the parent is a comment    //
      // ///////////////////////////////////////
      val parent = element.parent
      if (parent is PsiComment) {
         return parent
      }

      // /////////////////////////////////////////////////////////////
      // If we're in whitespace, check previous sibling comment   //
      // /////////////////////////////////////////////////////////////
      if (element is PsiWhiteSpace) {
         val prevSibling = PsiTreeUtil.skipWhitespacesBackward(element)
         if (prevSibling is PsiComment) {
            return prevSibling
         }
      }

      return null
   }

   /**
    * Convert a PSI comment to a CommentBlock.
    */
   private fun convertToCommentBlock(
      comment: PsiComment,
      document: Document
   ): CommentBlock {
      val text = comment.text
      val type = determineCommentType(text)
      val indentLevel = calculateIndent(comment, document)

      return CommentBlock(
         rawText = text,
         type = type,
         indentLevel = indentLevel,
         preserveParagraphs = true
      )
   }

   /**
    * Determine the type of comment from its text.
    */
   private fun determineCommentType(text: String): CommentType =
      when {
         text.startsWith("/**") -> CommentType.JAVADOC
         text.startsWith("/*") -> CommentType.BLOCK
         text.startsWith("//") -> CommentType.LINE
         else -> CommentType.BLOCK
      }

   /**
    * Calculate the indentation level of a comment.
    */
   private fun calculateIndent(
      comment: PsiComment,
      document: Document
   ): Int {
      val startOffset = comment.textRange.startOffset
      val lineNumber = document.getLineNumber(startOffset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)
      val lineText =
         document.getText(
            com.intellij.openapi.util.TextRange(
               lineStartOffset,
               startOffset
            )
         )

      // //////////////////////////////////////////////////
      // Count leading spaces (assume spaces not tabs)  //
      // //////////////////////////////////////////////////
      return lineText.takeWhile { it == ' ' }.length
   }

   /**
    * Get the text range of a comment in the document.
    */
   fun getCommentRange(comment: PsiComment): com.intellij.openapi.util.TextRange = comment.textRange
}
