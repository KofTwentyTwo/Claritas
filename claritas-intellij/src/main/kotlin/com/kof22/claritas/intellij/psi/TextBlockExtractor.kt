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
 * - Existing comments (line, block, or documentation style)
 * - Raw text (selected text without comment markers)
 * - Multi-line text blocks
 *
 * Extracted blocks are classified as:
 * - DOCUMENTATION: Comments starting with JavaDoc/JSDoc/KDoc syntax
 * - STANDARD: All other comments and raw text
 */
object TextBlockExtractor
{
   /**
    * Extract text block at the current caret position.  This is the main entry point for formatting.
    *
    * Strategy:
    * 1. If selection exists, use selected text
    * 2. Check if caret is in an existing comment - use that
    * 3. Otherwise, extract the current line as raw text
    *
    * @return Pair of CommentBlock and TextRange for replacement
    */
   fun extractBlockAtCaret(editor: Editor, psiFile: PsiFile): Pair<CommentBlock, TextRange>
   {
      val document = editor.document
      val selectionModel = editor.selectionModel

      ////////////////////////////////////////////////////
      // Case 1: User has selected text - use selection //
      ////////////////////////////////////////////////////
      if (selectionModel.hasSelection())
      {
         return extractSelectedBlock(selectionModel, document)
      }

      val offset = editor.caretModel.offset
      val element = psiFile.findElementAt(offset)

      /////////////////////////////////////////////////////
      // Case 2: Caret is in a comment - extract comment //
      /////////////////////////////////////////////////////
      val comment = findComment(element)
      if (comment != null)
      {
         return extractCommentBlock(comment, document)
      }

      //////////////////////////////////////////////
      // Case 3: Extract current line as raw text //
      //////////////////////////////////////////////
      return extractBlockFromCurrentLine(offset, document)
   }

   /**
    * Extract selected text as a block.
    *
    * Edge case: If selection is empty (zero-length), falls back to extracting from current line.
    */
   private fun extractSelectedBlock(selectionModel: com.intellij.openapi.editor.SelectionModel, document: Document): Pair<CommentBlock, TextRange>
   {
      ///////////////////////////////////////////////////////
      // get the starting and ending line of the selection //
      ///////////////////////////////////////////////////////
      val startOffset = selectionModel.selectionStart
      val endOffset = selectionModel.selectionEnd
      
      ///////////////////////////////////////////////
      // Handle edge case: empty selection         //
      ///////////////////////////////////////////////
      if (startOffset == endOffset)
      {
         return extractBlockFromCurrentLine(startOffset, document)
      }
      
      val startLine = document.getLineNumber(startOffset)
      val endLine = document.getLineNumber(endOffset)

      /////////////////////////////////////////////////////////////////////
      // get the full lines for the first and last line of the selection //
      /////////////////////////////////////////////////////////////////////
      val blockStartOffset = document.getLineStartOffset(startLine)
      val blockEndOffset = document.getLineEndOffset(endLine)

      val blockText = document.getText(TextRange(blockStartOffset, blockEndOffset))
      val firstLineText = getLineText(document, startLine)
      val indent = firstLineText.takeWhile { it == ' ' || it == '\t' }.length

      val block =
         CommentBlock(
            rawText = blockText,
            type = detectCommentType(blockText),
            indentLevel = indent,
            preserveParagraphs = true
         )

      return Pair(block, TextRange(blockStartOffset, blockEndOffset))
   }

   /**
    * Extract an existing comment, expanding to include entire block.
    *
    * For block comments, this expands from the opening marker to the
    * closing marker using the default PSI parsing.
    */
   private fun extractCommentBlock(comment: PsiComment, document: Document): Pair<CommentBlock, TextRange>
   {
      val text = comment.text
      val type = detectCommentType(text)
      val indent = calculateIndent(comment, document)

      //////////////////////////////////////////////////////////////////////////////////////////////////
      // We can just use the default PSI parsing for this, it should handle the entire comment nicely //
      //////////////////////////////////////////////////////////////////////////////////////////////////
      val block = CommentBlock(
         rawText = text,
         type = type,
         indentLevel = indent,
         preserveParagraphs = true
      )

      return Pair(block, comment.textRange)
   }

   /**
    * Extract raw text block around cursor.
    *
    * Expands upward and downward from current line until hitting:
    * - Blank lines (whitespace only)
    * - Start/end of document
    */
   private fun extractBlockFromCurrentLine(offset: Int, document: Document): Pair<CommentBlock, TextRange>
   {
      val currentLine = document.getLineNumber(offset)
      val totalLines = document.lineCount

      ///////////////////////////////////////////////
      // Find the start of the block (scan upward) //
      ///////////////////////////////////////////////
      var startLine = currentLine
      while (startLine > 0)
      {
         val lineText = getLineText(document, startLine - 1)
         if (lineText.isBlank())
         {
            break
         }
         startLine--
      }

      ///////////////////////////////////////////////
      // Find the end of the block (scan downward) //
      ///////////////////////////////////////////////
      var endLine = currentLine
      while (endLine < totalLines - 1)
      {
         val lineText = getLineText(document, endLine + 1)
         if (lineText.isBlank())
         {
            break
         }
         endLine++
      }

      //////////////////////////////
      // Extract the entire block //
      //////////////////////////////
      val blockStartOffset = document.getLineStartOffset(startLine)
      val blockEndOffset = document.getLineEndOffset(endLine)

      val blockText = document.getText(TextRange(blockStartOffset, blockEndOffset))

      ///////////////////////////////////////////
      // Calculate indentation from first line //
      ///////////////////////////////////////////
      val firstLineText = getLineText(document, startLine)
      val indent = firstLineText.takeWhile { it == ' ' || it == '\t' }.length

      val block =
         CommentBlock(
            rawText = blockText,
            type = detectCommentType(blockText), // Detect from content
            indentLevel = indent,
            preserveParagraphs = true
         )

      return Pair(block, TextRange(blockStartOffset, blockEndOffset))
   }

   /**
    * Get text of a specific line.
    */
   private fun getLineText(document: Document, lineNumber: Int): String
   {
      val startOffset = document.getLineStartOffset(lineNumber)
      val endOffset = document.getLineEndOffset(lineNumber)
      return document.getText(TextRange(startOffset, endOffset))
   }

   /**
    * Find a comment element starting from the given PSI element.
    */
   private fun findComment(element: PsiElement?): PsiComment?
   {
      if (element == null) return null

      //////////////////////////////////////////////
      // Check if the element itself is a comment //
      //////////////////////////////////////////////
      if (element is PsiComment)
      {
         return element
      }

      //////////////////////////////////////
      // Check if the parent is a comment //
      //////////////////////////////////////
      val parent = element.parent
      if (parent is PsiComment)
      {
         return parent
      }

      ////////////////////////////////////////////////////////////
      // If we're in whitespace, check previous sibling comment //
      ////////////////////////////////////////////////////////////
      if (element is PsiWhiteSpace)
      {
         val prevSibling = PsiTreeUtil.skipWhitespacesBackward(element)
         if (prevSibling is PsiComment)
         {
            return prevSibling
         }
      }

      return null
   }

   /**
    * Detect the type of comment from text.
    *
    * Returns:
    * - DOCUMENTATION: For JavaDoc/JSDoc/KDoc style comments
    * - STANDARD: For all other comments or raw text
    */
   private fun detectCommentType(text: String): CommentType =
      when
      {
         text.startsWith("/**") -> CommentType.DOCUMENTATION
         else -> CommentType.STANDARD
      }

   /**
    * Calculate indentation from PSI comment element.
    */
   private fun calculateIndent(comment: PsiComment, document: Document): Int
   {
      val startOffset = comment.textRange.startOffset
      val lineNumber = document.getLineNumber(startOffset)
      val lineStartOffset = document.getLineStartOffset(lineNumber)

      return calculateIndentFromOffset(document, lineStartOffset, startOffset)
   }

   /**
    * Calculate indentation from offsets.
    *
    * Counts spaces and tabs (tabs count as 1 unit of indentation).
    * This matches the raw character count approach used throughout the codebase.
    */
   private fun calculateIndentFromOffset(document: Document, lineStartOffset: Int, contentStartOffset: Int): Int
   {
      val lineText = document.getText(TextRange(lineStartOffset, contentStartOffset))
      return lineText.takeWhile { it == ' ' || it == '\t' }.length
   }
}
