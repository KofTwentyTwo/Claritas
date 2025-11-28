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

package com.kof22.claritas.formatter

import com.kof22.claritas.model.CommentBlock
import com.kof22.claritas.model.FlowerboxStyle
import com.kof22.claritas.model.FormattedComment
import com.kof22.claritas.util.BorderRenderer
import com.kof22.claritas.util.LineWrapper
import com.kof22.claritas.util.TextNormalizer

/**
 * Main formatter for converting comments to flowerbox style.
 *
 * This is the primary entry point for the core formatting logic.
 */
class CommentFormatter(
   private val style: FlowerboxStyle
)
{
   /**
    * Format a comment block into flowerbox style.
    *
    * Process:
    * 1. Normalize text (strip markers, clean whitespace)
    * 2. Split into paragraphs if preserving
    * 3. Wrap lines to fit content width
    * 4. Render with flowerbox borders
    *
    * @param comment The comment block to format
    * @return Formatted comment with flowerbox styling
    */
   fun format(comment: CommentBlock): FormattedComment
   {
      // Step 1: Normalize text
      val normalizedText = TextNormalizer.normalize(
         comment.rawText,
         comment.preserveParagraphs
      )

      if (normalizedText.isEmpty())
      {
         // Empty comment - return minimal flowerbox
         val emptyFormatted = BorderRenderer.render(emptyList(), style)
         return FormattedComment(
            formattedText = emptyFormatted,
            lineCount = emptyFormatted.lines().size,
            actualWidth = style.fixedWidth ?: style.minWidth
         )
      }

      // Step 2: Split into paragraphs
      val paragraphs = TextNormalizer.splitIntoParagraphs(normalizedText)

      // Step 3: Calculate content width and wrap lines
      val contentWidth = if (style.fixedWidth != null)
      {
         BorderRenderer.calculateContentWidth(style.fixedWidth, style)
      }
      else
      {
         // For dynamic width, use maxWidth for wrapping
         BorderRenderer.calculateContentWidth(style.maxWidth, style)
      }

      val wrappedLines = LineWrapper.wrapParagraphs(paragraphs, contentWidth)

      // Step 4: Render with borders
      val formattedText = BorderRenderer.render(wrappedLines, style)

      // Calculate actual width from the first line (top border)
      val actualWidth = formattedText.lines().firstOrNull()?.length
         ?: (style.fixedWidth ?: style.minWidth)

      return FormattedComment(
         formattedText = formattedText,
         lineCount = formattedText.lines().size,
         actualWidth = actualWidth
      )
   }

   /**
    * Format raw comment text directly (convenience method).
    *
    * @param rawText The raw comment text
    * @param preserveParagraphs Whether to preserve paragraph breaks
    * @return Formatted comment string
    */
   fun formatText(
      rawText: String,
      preserveParagraphs: Boolean = true
   ): String
   {
      val comment = CommentBlock(
         rawText = rawText,
         type = com.kof22.claritas.model.CommentType.BLOCK,
         indentLevel = 0,
         preserveParagraphs = preserveParagraphs
      )

      return format(comment).formattedText
   }
}

